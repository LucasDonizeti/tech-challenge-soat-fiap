# Segurança e Autenticação de APIs

Detalha o fluxo de autenticação, geração e validação de tokens JWT no **Sistema de Gestão de Oficina**.

---

## Visão geral

A autenticação funciona de forma diferente dependendo do ambiente:

| Ambiente | Quem gera o token | Quem valida |
|----------|------------------|-------------|
| **Local** (Docker Compose) | `AutenticarUsuarioUseCase` no Spring Boot | `JwtAuthenticationFilter` no Spring Security |
| **Produção** (AWS) | **AWS Lambda Authorizer** | `JwtAuthenticationFilter` no Spring Security |

Em ambos os casos o token usa **HMAC-SHA256 (HS256)** com a mesma chave `JWT_SECRET`. O `oficina-api` nunca gera tokens em produção — ele apenas os valida.

---

## Fluxo em produção

```
[Admin/Cliente]
     │
     │  POST /v1/auth/login
     │  { "username": "admin", "password": "secret123" }
     ▼
[API Gateway HTTP v2]
     │  Rota dedicada → AWS_PROXY → Lambda Authorizer
     ▼
[Auth Lambda] (Java 21, BCrypt + JJWT)
     ├── Admin → verifica env var (sem banco)  → gera JWT ADMIN
     └── CPF/CNPJ → JDBC → RDS MySQL → BCrypt → gera JWT CLIENTE
     │
     ▼
[Cliente recebe]  { "token": "eyJ...", "type": "Bearer", "username": "admin" }

----

[Requisição subsequente]
     │  GET /v1/admin/clientes
     │  Authorization: Bearer eyJ...
     ▼
[API Gateway → NLB → EKS NodePort 30080]
     ▼
[JwtAuthenticationFilter — Spring Security]
     ├── Extrai token do header Authorization: Bearer
     ├── getUsernameFromToken(jwt) → "admin"
     ├── getRoleFromToken(jwt) → "ADMIN"
     ├── adminUserDetailsService.loadUserByUsername("admin")
     ├── validateToken(jwt, userDetails) → true ✅
     └── SecurityContextHolder.setAuthentication(admin, [ROLE_ADMIN])
     ▼
[Controller executa normalmente]
```

---

## Credenciais padrão (local e produção)

| Campo | Valor |
|-------|-------|
| Usuário Admin | `admin` |
| Senha Admin | `secret123` (configurável via `SECURITY_USER_NAME` / `SECURITY_USER_PASSWORD`) |
| Role | `ADMIN` |

> Em produção, as credenciais do admin são lidas de variáveis de ambiente (`SPRING_SECURITY_USER_NAME` e `SPRING_SECURITY_USER_PASSWORD`) injetadas pela Lambda e pelo Helm — nunca hardcoded.

---

## Obtendo o token

### Local

```bash
curl -s -X POST http://localhost:8080/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"secret123"}' \
  | python3 -m json.tool
```

### Produção

```bash
API_URL=$(aws apigatewayv2 get-apis \
  --region us-east-1 \
  --query 'Items[?Name==`oficina-api-gateway`].ApiEndpoint' \
  --output text)

curl -s -X POST "$API_URL/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"secret123"}' \
  | python3 -m json.tool
```

**Resposta (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "username": "admin"
}
```

---

## Usando o token

```bash
# Salvar o token
TOKEN=$(curl -s -X POST http://localhost:8080/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"secret123"}' \
  | python3 -c "import sys,json; print(json.load(sys.stdin)['token'])")

# Usar em endpoint protegido
curl -s http://localhost:8080/v1/admin/clientes \
  -H "Authorization: Bearer $TOKEN" \
  | python3 -m json.tool
```

---

## Mapeamento de endpoints

### Endpoints públicos (sem autenticação)

| Padrão | Descrição |
|--------|-----------|
| `POST /v1/auth/login` | Login — roteado para Lambda em produção |
| `GET /v1/os/**` | Acompanhamento de OS pelo cliente |
| `GET /actuator/**` | Health checks (Kubernetes probes + New Relic) |
| `GET /swagger-ui/**` | Documentação interativa da API |
| `GET /v3/api-docs/**` | Spec OpenAPI 3.0 em JSON |

### Endpoints protegidos (requer `ADMIN`)

| Padrão | Descrição |
|--------|-----------|
| `GET/POST/PUT/DELETE /v1/admin/**` | CRUD: clientes, veículos, serviços, peças, estoque, listagem de OS |

---

## Estrutura do token JWT

| Claim | Admin | Cliente (CPF/CNPJ) |
|-------|-------|-------------------|
| `sub` | username (ex: `admin`) | CPF/CNPJ normalizado |
| `role` | `ADMIN` | `CLIENTE` |
| `nome` | — | Nome do cliente |
| `clienteId` | — | UUID do cliente |
| `iat` | Timestamp de emissão | Timestamp de emissão |
| `exp` | `iat + 3600s` (1h) | `iat + 86400s` (24h) |

---

## Configurações do JWT

**Local (`application.yaml`):**
```yaml
security:
  jwt:
    secret: chavesecreta    # alterar em produção
    expiration: 86400       # tempo de expiração em segundos
```

**Produção:** injetado como variável de ambiente `JWT_SECRET` pelo Helm (vindo do GitHub Secret). O mesmo valor deve estar configurado no `auth-lambda`.

---

## Erros comuns

| Situação | Status | Causa |
|----------|--------|-------|
| Sem header `Authorization` | `401` | Spring Security bloqueia sem autenticação |
| Token expirado | `401` | Expiração da claim `exp` |
| Token inválido / assinatura errada | `401` | `JWT_SECRET` diferente entre Lambda e Spring Boot |
| Role incorreta (ex: CLIENTE em `/v1/admin/**`) | `403` | Autorização insuficiente |
| Body vazio no login | `400` | Lambda retorna erro de validação |
| Credenciais incorretas | `401` | `{"error": "Credenciais inválidas"}` da Lambda |
