# RFC-001 — Escolha do Provedor de Nuvem: AWS

| Campo | Valor |
|-------|-------|
| **ID** | RFC-001 |
| **Título** | Escolha do Provedor de Nuvem |
| **Status** | Aceito |
| **Data** | 2026-03-20 |
| **Autores** | Time de Arquitetura |
| **ADRs relacionados** | ADR-004 (Docker), ADR-009 (Testes) |
| **RFCs relacionados** | RFC-002, RFC-003, RFC-004, RFC-005 |

---

## 1. Contexto

O **Sistema de Gestão de Oficina** é um projeto acadêmico do programa **SOAT (Software Architecture) da FIAP**, com requisitos explícitos de deploy em nuvem pública com Kubernetes gerenciado, banco de dados relacional gerenciado, pipeline de CI/CD automatizado e monitoramento de infraestrutura. A escolha do provedor de nuvem é a decisão mais fundamental do projeto, pois condiciona todos os demais serviços, o modelo de custo e as habilidades técnicas necessárias.

O contexto acadêmico impõe uma restrição relevante: o ambiente disponível é o **AWS Academy**, que oferece créditos temporários com acesso a um subconjunto de serviços AWS através de uma role `LabRole` com permissões pré-definidas — sem acesso root à conta e com sessões de até 4 horas, exigindo estratégias de automação via Terraform para reprovisionar o ambiente de forma consistente.

---

## 2. Problema

Precisamos escolher um provedor de nuvem que:

- Ofereça Kubernetes gerenciado compatível com Helm e integração com pipeline GitHub Actions
- Disponha de banco de dados relacional gerenciado com backup automático e senhas gerenciadas por cofre de segredos
- Tenha suporte a container registry privado integrado ao pipeline de build
- Permita API Gateway com roteamento HTTP e integração nativa com funções serverless
- Seja suportado pelo ambiente educacional disponível (AWS Academy)
- Possua Terraform provider maduro para IaC completo

---

## 3. Alternativas consideradas

### 3.1 Amazon Web Services (AWS) ✅ Escolhida

**Serviços relevantes para o projeto:**

| Necessidade | Serviço AWS |
|-------------|-------------|
| Kubernetes gerenciado | Amazon EKS |
| Banco relacional gerenciado | Amazon RDS (MySQL 8.0) |
| Container registry | Amazon ECR |
| API Gateway HTTP | Amazon API Gateway v2 (HTTP API) |
| Serverless / Auth | AWS Lambda (container image) |
| Segredos | AWS Secrets Manager |
| Terraform state | Amazon S3 + DynamoDB |
| Monitoramento nativo | CloudWatch (complementado por New Relic) |

**Vantagens:**
- Disponível no AWS Academy com créditos suficientes para o projeto
- EKS é o Kubernetes gerenciado mais maduro e amplamente documentado
- Integração nativa entre todos os serviços (IAM, VPC, ECR, EKS, Lambda, Secrets Manager)
- Terraform AWS Provider é o mais completo e estável do ecossistema
- GitHub Actions tem actions oficiais da AWS (`aws-actions/*`)
- Maior adoção no mercado — habilidade transferível

**Desvantagens:**
- Console e CLI têm curva de aprendizado mais acentuada
- Custo elevado em produção real (mitigado pelos créditos do Academy)
- Sessões temporárias do AWS Academy exigem re-autenticação frequente

---

### 3.2 Google Cloud Platform (GCP)

**Serviços equivalentes:** GKE, Cloud SQL, Artifact Registry, API Gateway, Cloud Functions, Secret Manager.

**Por que não foi escolhido:**
- Não disponível no ambiente AWS Academy
- GKE, apesar de excelente, exigiria provisionamento com conta pessoal paga
- Menor familiaridade do time com o ecossistema GCP
- Terraform GCP Provider tem cobertura menor para alguns serviços específicos

---

### 3.3 Microsoft Azure

**Serviços equivalentes:** AKS, Azure Database for MySQL, Azure Container Registry, Azure API Management, Azure Functions, Key Vault.

**Por que não foi escolhido:**
- Não disponível no ambiente AWS Academy
- Azure API Management tem configuração mais complexa que API Gateway HTTP v2
- Integração AKS + Functions + APIM exige configuração de identidade (Managed Identity) mais elaborada
- Time sem familiaridade prévia com o ecossistema Azure

---

### 3.4 Kubernetes local (Minikube / kind) + serviços self-hosted

**Por que não foi escolhido:**
- Não atende ao requisito explícito do Tech Challenge de deploy em nuvem pública
- Não possui banco de dados gerenciado — exigiria MySQL auto-gerenciado com risco de perda de dados
- Sem SLA de disponibilidade ou backup automatizado
- Viável apenas para desenvolvimento local (docker-compose é usado neste propósito)

---

## 4. Decisão

**Adotar Amazon Web Services (AWS) como provedor de nuvem único**, utilizando a região `us-east-1` como padrão para todos os recursos.

A decisão é suportada pela disponibilidade do ambiente AWS Academy, pela cobertura completa de serviços necessários ao projeto, pela maturidade do Terraform AWS Provider e pela integração nativa entre os componentes da solução.

---

## 5. Arquitetura de contas e regiões

```
AWS Account (AWS Academy)
└── Região: us-east-1
    ├── VPC: 10.0.0.0/16 (2 AZs: us-east-1a, us-east-1b)
    │   ├── Public Subnets  (10.0.101.0/24, 10.0.102.0/24)  — API GW, NAT
    │   ├── Private Subnets (10.0.1.0/24, 10.0.2.0/24)      — EKS nodes, Lambda
    │   └── Database Subnets (10.0.201.0/24, 10.0.202.0/24) — RDS
    ├── EKS Cluster (oficina-cluster)
    ├── RDS MySQL (oficina-rds)
    ├── Lambda (auth-lambda)
    ├── API Gateway HTTP v2
    ├── ECR (oficina-api, auth-lambda)
    └── S3 + DynamoDB (Terraform state)
```

---

## 6. Estratégia de IaC e ordem de provisionamento

Todo o provisionamento é feito via **Terraform**, organizado em 3 repositórios independentes com state remoto em S3, na seguinte ordem obrigatória:

1. **k8s-infra** — VPC, EKS, ECR, API Gateway, New Relic (outputs consumidos por todos)
2. **db-infra** — RDS MySQL (lê outputs do k8s-infra via `terraform_remote_state`)
3. **auth-lambda** — Lambda + rota API Gateway (lê outputs do k8s-infra)
4. **Pipeline GitHub Actions** — Deploy Helm → EKS (após infraestrutura pronta)

O state remoto usa o bucket S3 `bucket-tfstate-1029` com lock via DynamoDB `meu-terraform-state-lock`, garantindo operações concorrentes seguras.

---

## 7. Restrições do AWS Academy

A role `LabRole` disponível no Academy impõe limitações conhecidas:

| Restrição | Impacto | Mitigação |
|-----------|---------|-----------|
| Sessões de 4h | Credenciais expiram | `AWS_SESSION_TOKEN` parametrizado nos GitHub Secrets; reprovisionar via `terraform apply` |
| Sem criação de roles IAM | Não é possível criar roles customizadas | Todos os recursos usam `LabRole` diretamente: EKS, Node Group, Lambda |
| Sem acesso root | Algumas operações de billing bloqueadas | Irrelevante para o projeto técnico |
| Créditos limitados | Risco de esgotar créditos | Instâncias `t3.medium` (EKS) e `db.t3.micro` (RDS) minimizam custo |

---

## 8. Consequências

**Positivas:**
- Ambiente totalmente reproduzível via `terraform apply` em < 15 minutos
- Pipeline CI/CD completamente automatizado com deploy zero-downtime via Helm
- SLAs gerenciados pela AWS para EKS, RDS e Lambda
- Observabilidade integrada entre New Relic e CloudWatch

**Negativas / Riscos:**
- Dependência de créditos AWS Academy — projeto não pode ficar permanentemente provisionado
- Latência de cold start da Lambda pode impactar a primeira requisição de autenticação após períodos de inatividade
- Custo proibitivo para manter o ambiente 24/7 fora do contexto acadêmico

---

## 9. Revisão futura

Se o projeto evoluir para produção real, avaliar:

- **Multi-região** para alta disponibilidade (adicionar `us-west-2` como DR)
- **AWS Organizations** para separação de contas dev/staging/prod
- **RDS Multi-AZ** para eliminar ponto único de falha no banco
- **Aurora Serverless v2** como substituto ao RDS para elasticidade automática
