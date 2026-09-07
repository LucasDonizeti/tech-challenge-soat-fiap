# Pipeline CI/CD — GitHub Actions

Este documento descreve a pipeline de CI/CD do **repositório principal** (`tech-challenge-soat-fiap`), que cuida exclusivamente do build, publicação da imagem Docker e deploy da aplicação `oficina-api` no EKS.

> **Nota:** O provisionamento de infraestrutura (VPC, EKS, RDS, Lambda) é feito por pipelines separadas nos repositórios `k8s-infra`, `db-infra` e `auth-lambda`. Este repo assume que toda a infraestrutura já está provisionada.

---

## Fluxo da pipeline

```
Push → develop
         │
         ▼
┌─────────────────────────┐
│  Job 1: build           │  mvn -B verify (compilação + testes + JaCoCo)
│  Build & Test           │  Sobe o JAR como artefato GitHub
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│  Job 2: push-image-ecr  │  Docker build + push para ECR oficina-api
│  Build & Push           │  Tags: SHORT_SHA + latest
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────┐
│  Job 3: deploy                                          │
│  Deploy Helm → EKS                                      │
│  ├── aws eks update-kubeconfig                          │
│  ├── Busca endpoint do RDS (aws rds describe-db-instances)│
│  ├── Recupera senha do RDS no Secrets Manager           │
│  ├── Cria/atualiza imagePullSecret (aws-ecr-secret)     │
│  ├── helm upgrade --install (com --rollback-on-failure) │
│  └── kubectl rollout status deployment/oficina-api      │
└─────────────────────────────────────────────────────────┘
```

---

## GitHub Secrets obrigatórios

Acesse: **Repositório → Settings → Secrets and variables → Actions → New repository secret**

| Secret | Descrição | Como obter |
|--------|-----------|-----------|
| `AWS_ACCESS_KEY_ID` | ID da chave de acesso AWS | AWS Academy → **AWS Details** |
| `AWS_SECRET_ACCESS_KEY` | Chave secreta de acesso AWS | AWS Academy → **AWS Details** |
| `AWS_SESSION_TOKEN` | Token de sessão temporário | AWS Academy → **AWS Details** |
| `DB_USERNAME` | Usuário master do RDS | O mesmo configurado no db-infra (padrão: `admindb`) |
| `JWT_SECRET` | Chave de assinatura JWT | Deve ser **idêntico** ao `jwt_secret` do auth-lambda |
| `SECURITY_USER_NAME` | Usuário admin padrão da aplicação | Ex: `admin` |
| `SECURITY_USER_PASSWORD` | Senha do admin padrão | Valor livre — use no login local |
| `NEW_RELIC_LICENSE_KEY` | Ingest License Key do New Relic | New Relic → API Keys → tipo **Ingest - License** |

> ⚠️ **AWS Academy:** Os três secrets AWS expiram a cada ~4h. Atualize-os antes de cada execução.
>
> ⚠️ **JWT_SECRET crítico:** Se este valor for diferente do configurado no `auth-lambda`, todos os tokens gerados pela Lambda serão rejeitados pelo Spring Security.

---

## Detalhes de cada job

### Job 1 — Build & Test

- Runner: `ubuntu-latest`
- JDK: Amazon Corretto 21 com cache Maven
- Comando: `mvn -B verify --file pom.xml` na pasta `./oficina`
  - Compila o código
  - Executa todos os testes (JUnit 5 + Mockito + H2)
  - Verifica cobertura JaCoCo (build falha se < 80%)
- Sobe o JAR como artefato `application-jar` (retenção: 1 dia)

### Job 2 — Build & Push Docker Image

- Autentica na AWS via `aws-actions/configure-aws-credentials@v4`
- Login no ECR: `aws-actions/amazon-ecr-login@v2`
- Tag: `SHORT_SHA` (7 chars do commit) + `latest`
- Build da imagem a partir de `./oficina/Dockerfile`
- Push das duas tags para `<account>.dkr.ecr.us-east-1.amazonaws.com/oficina-api`
- Exporta `image_tag` e `ecr_registry` para o Job 3

### Job 3 — Deploy Helm → EKS

1. Configura `kubectl` via `aws eks update-kubeconfig --name oficina-cluster`
2. Busca o endpoint do RDS:
   ```bash
   aws rds describe-db-instances --db-instance-identifier oficina-rds \
     --query 'DBInstances[0].Endpoint.Address' --output text
   ```
3. Recupera a senha do RDS do Secrets Manager (mascarada com `::add-mask::`)
4. Cria/atualiza o `imagePullSecret` `aws-ecr-secret` via `kubectl create secret docker-registry --dry-run | kubectl apply`
5. Executa `helm upgrade --install oficina ./k8s/oficina` com:
   - `-f k8s/oficina/values-prod.yaml`
   - `--set image.repository=...` e `--set image.tag=$IMAGE_TAG`
   - `--set secret.*` para todas as variáveis sensíveis
   - `--rollback-on-failure` — reverte automaticamente em falha
   - `--timeout 10m --wait`
6. Valida o rollout: `kubectl rollout status deployment/oficina-api --timeout=5m`

---

## Variáveis de ambiente injetadas no pod (via Helm)

| Variável | Origem | Descrição |
|----------|--------|-----------|
| `SPRING_DATASOURCE_URL` | Gerada dinamicamente | `jdbc:mysql://<rds-endpoint>:3306/oficina` |
| `SPRING_DATASOURCE_USERNAME` | Secret `DB_USERNAME` | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | AWS Secrets Manager (dinâmico) | Senha do banco |
| `JWT_SECRET` | Secret `JWT_SECRET` | Chave de validação JWT |
| `SECURITY_USER_NAME` | Secret `SECURITY_USER_NAME` | Usuário admin da aplicação |
| `SECURITY_USER_PASSWORD` | Secret `SECURITY_USER_PASSWORD` | Senha admin da aplicação |
| `NEW_RELIC_LICENSE_KEY` | Secret `NEW_RELIC_LICENSE_KEY` | License key do New Relic |
| `SPRING_APPLICATION_NAME` | ConfigMap | `oficina-api-prod` |
| `LOG_LEVEL` | ConfigMap | `INFO` em produção |

---

## Como acessar a aplicação após o deploy

### 1. Obter o URL do API Gateway

```bash
aws apigatewayv2 get-apis \
  --region us-east-1 \
  --query 'Items[?Name==`oficina-api-gateway`].ApiEndpoint' \
  --output text
```

### 2. URLs disponíveis

```bash
API_URL="https://<api-id>.execute-api.us-east-1.amazonaws.com"

# Swagger UI
echo "$API_URL/swagger-ui/index.html"

# Health check
curl "$API_URL/actuator/health"

# Login
curl -s -X POST "$API_URL/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"secret123"}'
```

> **Em produção:** o `POST /v1/auth/login` é interceptado pela **Lambda Authorizer** antes de chegar ao pod Spring Boot. Todas as outras rotas passam pelo NLB → NodePort 30080 → pod.

### 3. Verificar pods e HPA

```bash
# Conectar ao cluster
aws eks update-kubeconfig --region us-east-1 --name oficina-cluster

# Status dos pods
kubectl get pods

# Status do HPA (escala de 1 a 4 réplicas)
kubectl get hpa

# Logs do pod
kubectl logs -l app.kubernetes.io/name=oficina -f
```

---

## Re-executar ou depurar a pipeline

```bash
# Re-run via GitHub CLI
gh run list --workflow=pipeline.yml
gh run rerun <run-id>

# Acompanhar em tempo real
gh run watch <run-id>
```
