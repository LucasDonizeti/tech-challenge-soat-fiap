# RFC-002 — Estratégia de Autenticação: Lambda Authorizer + JWT

| Campo | Valor |
|-------|-------|
| **ID** | RFC-002 |
| **Título** | Estratégia de Autenticação — Lambda Authorizer + JWT |
| **Status** | Aceito |
| **Data** | 2026-03-22 |
| **Autores** | Time de Arquitetura |
| **ADRs relacionados** | ADR-002 (JWT), ADR-001 (Clean Architecture) |
| **RFCs relacionados** | RFC-001 (AWS), RFC-004 (EKS) |

---

## 1. Contexto

O sistema precisa de uma estratégia de autenticação que atenda a dois perfis distintos:

- **Administradores** (`ADMIN`): usuários internos da oficina que gerenciam clientes, veículos, peças e ordens de serviço via `/v1/admin/**`
- **Clientes** (`CLIENTE`): proprietários de veículos que acompanham o status de suas ordens de serviço via `/v1/os/**`

Os requisitos do Tech Challenge exigem explicitamente autenticação JWT. Além disso, a evolução para a segunda fase do projeto introduziu a necessidade de isolar o mecanismo de geração de token da aplicação principal, permitindo que a auth seja escalada, atualizada e testada de forma independente.

---

## 2. Problema

Precisamos definir:

1. **Onde** o token JWT é gerado (dentro da aplicação Spring Boot ou em serviço separado)
2. **Como** o token é validado nas requisições subsequentes
3. **Qual algoritmo** de assinatura usar
4. **Como** diferenciar tokens de Admin e de Cliente para aplicar autorização correta
5. **Como** integrar essa estratégia ao API Gateway sem expor a aplicação diretamente à internet

---

## 3. Alternativas consideradas

### 3.1 Auth embutida no Spring Boot (autenticação monolítica) — descartada

Gerar e validar o token JWT inteiramente dentro do `oficina-api` com Spring Security.

**Prós:**
- Implementação mais simples
- Sem latência adicional de cold start
- Estado compartilhado (UserDetailsService) entre geração e validação

**Contras:**
- Acoplamento total entre lógica de negócio e autenticação
- Impossível escalar auth independentemente do restante da aplicação
- Qualquer atualização na lógica de auth exige rebuild e redeploy de toda a aplicação
- Não se beneficia do isolamento de rede (auth misturada com endpoints de negócio)

**Por que foi descartada:** Viola o princípio de separação de responsabilidades e impede a evolução independente do mecanismo de autenticação. O Tech Challenge fase 2 tornou esse isolamento um requisito implícito.

---

### 3.2 AWS Cognito como Identity Provider ✗ Descartada

Usar o Amazon Cognito para gerenciar usuários, emitir tokens JWT (OAuth2 / OIDC) e validá-los no API Gateway com o authorizer nativo do Cognito.

**Prós:**
- Serviço totalmente gerenciado — sem código de auth para manter
- Suporte nativo a OAuth2, OIDC, MFA e Social Login
- Integração direta com API Gateway HTTP v2

**Contras:**
- Cognito **não está disponível** na role `LabRole` do AWS Academy
- User Pools têm restrições de customização para validação de CPF/CNPJ (formato não padrão)
- Custo adicional por MAU (Monthly Active User) em produção real
- Acoplamento a um serviço proprietário difícil de migrar

**Por que foi descartada:** Indisponibilidade no ambiente AWS Academy e inflexibilidade para o modelo de identidade com CPF/CNPJ.

---

### 3.3 Auth Service dedicado (microserviço Spring Boot separado) ✗ Descartada

Criar um microserviço Spring Boot independente, hospedado no EKS, responsável apenas pela autenticação.

**Prós:**
- Total autonomia de deploy e escalonamento
- Stack tecnológica consistente (Java/Spring em todo o projeto)

**Contras:**
- Precisa de seu próprio Deployment, Service, HPA e Ingress no EKS — overhead operacional para um serviço de propósito único
- Cold start equivalente ao `oficina-api` (JVM Spring Boot)
- Roteamento via API Gateway para o microserviço adicionaria complexidade sem benefício claro frente à Lambda

**Por que foi descartada:** Custo operacional desproporcionalmente alto para um serviço de responsabilidade simples e de baixa frequência de uso.

---

### 3.4 AWS Lambda Container Image (Java 21) ✅ Escolhida

Implementar a geração de token JWT em uma **AWS Lambda** empacotada como container Docker, integrada ao API Gateway via rota dedicada `POST /v1/auth/login`, deployada em private subnets para acesso direto ao RDS.

**Prós:**
- Isolamento total da lógica de autenticação — deploy e atualização independentes do `oficina-api`
- Escalonamento automático pela AWS — zero instâncias idle quando não há requisições de login
- Acesso direto ao RDS MySQL na mesma VPC sem necessidade de expor endpoints públicos adicionais
- Container image permite usar o mesmo padrão Docker do restante do projeto
- Cold start mitigado pelo tamanho contido do JAR (fat JAR via `maven-shade-plugin` sem Spring)
- Custo praticamente zero no volume acadêmico (1M invocações/mês gratuitas no free tier)

**Contras:**
- Cold start de JVM em Lambda pode atingir 2–5s na primeira invocação após período idle
- Dois repositórios separados (aplicação + lambda) aumentam a complexidade de manutenção
- Conexão JDBC direta ao RDS na Lambda requer cuidado com pool de conexões (não usa HikariCP)

---

## 4. Decisão

**Adotar AWS Lambda (container image, Java 21) para geração de token JWT**, integrada ao API Gateway HTTP v2 como rota dedicada `POST /v1/auth/login`, com validação de token realizada pelo `JwtAuthenticationFilter` dentro do `oficina-api`.

---

## 5. Fluxo de autenticação detalhado

```
[Admin/Cliente]
     │
     │  POST /v1/auth/login
     │  { "username": "admin", "password": "secret123" }
     ▼
[API Gateway HTTP v2]
     │  Route: POST /v1/auth/login → AWS_PROXY → Lambda
     ▼
[Auth Lambda — private subnet]
     │
     ├── 1. Extrai/gera x-correlation-id
     ├── 2. Parse JSON → AuthRequestDto
     │
     ├── [SE username == ADMIN_USERNAME]
     │       └── BCrypt.checkpw(password, adminPasswordHash)
     │           → NÃO consulta RDS
     │
     └── [SE CPF (11 dígitos) ou CNPJ (14 dígitos)]
             └── clienteGateway.findByCpf/Cnpj()
                 └── JDBC → RDS MySQL (private subnet)
                     └── BCrypt.checkpw(password, cliente.senhaHash)
     │
     ├── jwtTokenUtil.generateAdminToken(username)
     │   ou jwtTokenUtil.generateClienteToken(username, nome, clienteId)
     │
     └── HTTP 200 { token: "eyJ...", type: "Bearer", username }


[Requisição protegida subsequente]
     │
     │  POST /v1/os
     │  Authorization: Bearer eyJ...
     ▼
[API Gateway → NLB → EKS NodePort 30080]
     ▼
[JwtAuthenticationFilter — Spring Security]
     ├── Extrai token do header Authorization: Bearer
     ├── getUsernameFromToken(jwt) → "admin"
     ├── getRoleFromToken(jwt) → "ADMIN"
     ├── adminUserDetailsService.loadUserByUsername("admin")
     ├── validateToken(jwt, userDetails) → true
     └── SecurityContextHolder.setAuthentication(admin, [ROLE_ADMIN])
     ▼
[OrdemServicoController — acesso autorizado]
```

---

## 6. Especificação do token JWT

| Campo | Valor |
|-------|-------|
| Algoritmo | HMAC-SHA256 (HS256) |
| Biblioteca | JJWT 0.13.0 (Lambda) / JJWT 0.11.5 (Spring Boot) |
| Expiração — Admin | 1 hora (3600s, configurável via `JWT_EXPIRATION`) |
| Expiração — Cliente | 24 horas |
| Claims customizadas | `role` (ADMIN \| CLIENTE), `nome`, `clienteId` |
| Chave secreta | Injetada via env var `JWT_SECRET` (Secrets Manager / GitHub Secret) |
| Header personalizado | `x-correlation-id` na resposta da Lambda |

**Payload exemplo (Admin):**
```json
{
  "sub": "admin",
  "role": "ADMIN",
  "iat": 1725494400,
  "exp": 1725498000
}
```

**Payload exemplo (Cliente):**
```json
{
  "sub": "12345678901",
  "role": "CLIENTE",
  "nome": "João da Silva",
  "clienteId": "550e8400-e29b-41d4-a716-446655440000",
  "iat": 1725494400,
  "exp": 1725580800
}
```

---

## 7. Segurança — decisões de implementação

### 7.1 Hashing de senhas

Todas as senhas são armazenadas e comparadas usando **BCrypt** (`jbcrypt 0.4`). A senha do admin nunca é armazenada em texto claro — é lida da variável de ambiente e hasheada em memória na inicialização do `AutenticarUsuarioUseCase`.

### 7.2 Segredos

| Segredo | Origem em produção | Origem local |
|---------|-------------------|--------------|
| `JWT_SECRET` | GitHub Secret → Helm Secret → env var | `application.yaml` (valor de teste) |
| `SPRING_SECURITY_USER_PASSWORD` | GitHub Secret → Lambda env var | variável de ambiente local |
| `DB_PASSWORD` | AWS Secrets Manager → Lambda env var | docker-compose |

### 7.3 Endpoints públicos vs protegidos

| Padrão de URL | Visibilidade | Justificativa |
|--------------|-------------|---------------|
| `POST /v1/auth/login` | Público (roteado para Lambda) | Endpoint de autenticação — precisa ser acessível sem token |
| `GET /v1/os/**` | Público | Acompanhamento de OS pelo cliente sem cadastro |
| `GET /actuator/**` | Público | Health check para NLB e New Relic Synthetic |
| `GET /swagger-ui/**` | Público | Documentação da API |
| `/v1/admin/**` | Protegido (ADMIN) | CRUD completo — apenas usuários autenticados |

### 7.4 Mascaramento de logs

A Lambda mascara usernames em todos os logs usando `maskSensitiveData()`:
- Strings ≤ 4 chars → `***`
- Strings > 4 chars → primeiros 2 + `***` + últimos 2

---

## 8. Tratamento de erros

| Cenário | Resposta Lambda | Resposta API |
|---------|----------------|-------------|
| Body vazio ou nulo | 400 Bad Request | `{ "error": "Body da requisição é obrigatório" }` |
| Credenciais inválidas | 401 Unauthorized | `{ "error": "Credenciais inválidas" }` |
| Erro interno | 500 Internal Server Error | `{ "error": "Erro interno ao processar autenticação" }` |
| Token ausente em rota protegida | — | Spring Security retorna 401 |
| Token expirado/inválido | — | Spring Security retorna 401 |
| Usuário sem role ADMIN em `/v1/admin/**` | — | Spring Security retorna 403 |

---

## 9. Consequências

**Positivas:**
- A Lambda pode ser atualizada sem afetar o `oficina-api` e vice-versa
- Responsabilidade de geração de token isolada e testável de forma independente
- Custo efetivamente zero no nível de uso acadêmico
- Prova de conceito de arquitetura serverless integrada a workloads Kubernetes

**Negativas / Riscos:**
- Cold start da JVM pode causar latência de 2–5s na **primeira** requisição de login após idle prolongado
- A Lambda faz conexão JDBC direta sem pool de conexões robusto — em alta carga, pode esgotar conexões do RDS
- O `JWT_SECRET` deve ser **idêntico** na Lambda e no `oficina-api` — dessincronização resulta em falhas de validação silenciosas

---

## 10. Revisão futura

- Avaliar **SnapStart** da Lambda (Java 21) para eliminar o cold start da JVM
- Migrar para **GraalVM native image** se o cold start se tornar problema recorrente
- Adicionar **token refresh** com `refresh_token` de longa duração para evitar re-login frequente
- Avaliar **AWS Lambda Power Tuning** para encontrar o tamanho de memória ideal (atual: 512 MB)
