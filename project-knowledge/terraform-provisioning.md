# Provisionamento de Infraestrutura com Terraform

A infraestrutura do **Sistema de Gestão de Oficina** é provisionada via Terraform em **três repositórios separados**, com estado remoto em S3 e dependências encadeadas.

> **Este repositório não contém Terraform.** O provisionamento de infraestrutura é feito nos repositórios dedicados listados abaixo.

---

## Visão geral dos repositórios de infraestrutura

```
[1] tech-challenge-soat-fiap-k8s-infra
    └── VPC · EKS · ECR (oficina-api + auth-lambda) · API Gateway · New Relic
    └── State: s3://bucket-tfstate-1029/k8s/terraform.tfstate

[2] tech-challenge-soat-fiap-db-infra
    └── RDS MySQL 8.0 · Security Group · Secrets Manager (senha)
    └── State: s3://bucket-tfstate-1029/db/terraform.tfstate
    └── Depende de: k8s-infra (lê vpc_id, database_subnets, eks_node_sg_id)

[3] tech-challenge-soat-fiap-auth-lambda
    └── Lambda Authorizer · API Gateway route (POST /v1/auth/login)
    └── State: s3://bucket-tfstate-1029/auth-lambda/terraform.tfstate
    └── Depende de: k8s-infra (lê api_gateway_id, ecr_auth_lambda_url, private_subnets)
```

---

## Ordem obrigatória de provisionamento

```
Bootstrap (S3 + DynamoDB)          → executar UMA VEZ no k8s-infra/terraform/bootstrap
         │
         ▼
[1] k8s-infra apply                → ~12-18 min
         │
         ├──────────────┐
         ▼              ▼
[2] db-infra apply    [3] Pode esperar
    ~8-12 min
         │
         ▼
[4] Deploy da aplicação via pipeline Helm (este repo)
         │
         ▼
[5] auth-lambda apply              → ~1-3 min (precisa da imagem no ECR auth-lambda)
```

---

## Recursos criados por repositório

### k8s-infra

| Recurso | Configuração |
|---------|-------------|
| VPC | `10.0.0.0/16`, 2 AZs, 3 tiers de subnets (public/private/database) |
| NAT Gateway | Single (economia de custo) |
| EKS Cluster | v1.36, managed node group `t3.medium` (1–2 nós) |
| Add-ons EKS | `vpc-cni`, `kube-proxy`, `coredns` |
| ECR `oficina-api` | Privado, scan-on-push, lifecycle: 5 imagens |
| ECR `auth-lambda` | Privado, scan-on-push, lifecycle: 5 imagens |
| API Gateway HTTP v2 | Stage `$default`, rota `ANY /{proxy+}` → EKS via NLB |
| NLB Interno | Port 80 → Target Group NodePort 30080 |
| VPC Link | API GW → NLB em private subnets |
| S3 + DynamoDB | Backend de state Terraform |
| New Relic Monitor | Ping `/actuator/health/liveness` a cada 2 min |
| New Relic Dashboard | 4 páginas: Cluster, Logs, OS, Healthcheck |

### db-infra

| Recurso | Configuração |
|---------|-------------|
| RDS MySQL 8.0 | `db.t3.micro`, 20 GB, single-AZ, `utf8mb4` |
| Security Group | Ingress 3306/TCP somente do CIDR da VPC |
| Secrets Manager | Senha gerada e rotacionada automaticamente |

### auth-lambda

| Recurso | Configuração |
|---------|-------------|
| Lambda Function | Java 21, container image, 512 MB, 15s timeout |
| Lambda Permission | API GW pode invocar a Lambda |
| API GW Integration | `AWS_PROXY`, `POST /v1/auth/login` |
| API GW Route | `POST /v1/auth/login` → Lambda |

---

## Pré-requisitos locais

| Ferramenta | Versão |
|-----------|--------|
| Terraform | >= 1.6.0 |
| AWS CLI | v2 |
| kubectl | >= 1.28 |
| Helm | >= 3.14 |

---

## Configuração AWS CLI

```bash
# Credenciais permanentes
aws configure

# AWS Academy (sessão temporária — atualizar a cada ~4h)
aws configure set aws_access_key_id     "ASIA..."
aws configure set aws_secret_access_key "..."
aws configure set aws_session_token     "..."
aws configure set region                "us-east-1"

# Verificar
aws sts get-caller-identity
```

---

## Comandos resumidos por repositório

### k8s-infra (primeiro)

```bash
cd tech-challenge-soat-fiap-k8s-infra/

# Bootstrap (UMA VEZ)
cd terraform/bootstrap && terraform init && terraform apply
cd ..

# Infraestrutura principal
cd terraform/
terraform init \
  -backend-config="bucket=bucket-tfstate-1029" \
  -backend-config="key=k8s/terraform.tfstate" \
  -backend-config="region=us-east-1" \
  -backend-config="dynamodb_table=meu-terraform-state-lock" \
  -backend-config="encrypt=true"

terraform apply \
  -var="newrelic_account_id=SEU_ID" \
  -var="newrelic_api_key=NRAK-..." \
  -var="newrelic_license_key=..."

# Ver outputs (necessários para os próximos repos)
terraform output
```

### db-infra (segundo)

```bash
cd tech-challenge-soat-fiap-db-infra/terraform/

terraform init
terraform apply
```

### Deploy da aplicação (terceiro — via pipeline deste repo)

```bash
# Apenas faça push para a branch develop
git push origin develop
# A pipeline GitHub Actions cuida do resto
```

### auth-lambda (quarto)

```bash
# 1. Build e push da imagem
cd tech-challenge-soat-fiap-auth-lambda/lambda-authorizer/
mvn package -DskipTests
# ... docker build + push para ECR auth-lambda

# 2. Terraform
cd ../terraform/
terraform init
terraform apply \
  -var="account_id=$(aws sts get-caller-identity --query Account --output text)" \
  -var="db_url=jdbc:mysql://<RDS_ENDPOINT>:3306/oficina" \
  -var="db_user=admindb" \
  -var="db_password=<SENHA_SECRETS_MANAGER>" \
  -var="jwt_secret=<MESMA_CHAVE_DO_OFICINA_API>" \
  -var="image_tag=latest"
```

---

## Destruir a infraestrutura

Para economizar créditos do AWS Academy, destrua em ordem inversa:

```bash
# 1. auth-lambda
cd tech-challenge-soat-fiap-auth-lambda/terraform/
terraform destroy -var="account_id=..." -var="..." 

# 2. db-infra
cd tech-challenge-soat-fiap-db-infra/terraform/
terraform destroy

# 3. k8s-infra
cd tech-challenge-soat-fiap-k8s-infra/terraform/
terraform destroy -var="newrelic_account_id=..." -var="..."
```

> O bucket S3 de state (`bucket-tfstate-1029`) sobrevive ao destroy por ter `force_destroy = false` — o estado é preservado para a próxima sessão.

---

## Documentação detalhada por repositório

Cada repositório tem seu próprio README com guias completos:

- [k8s-infra/README.md](../../tech-challenge-soat-fiap-k8s-infra/README.md)
- [db-infra/README.md](../../tech-challenge-soat-fiap-db-infra/README.md)
- [auth-lambda/README.md](../../tech-challenge-soat-fiap-auth-lambda/README.md)
