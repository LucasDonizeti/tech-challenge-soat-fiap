# Provisionamento de Infraestrutura com Terraform

Esta documentação descreve como provisionar a infraestrutura da AWS necessária para executar o Sistema de Gestão de Oficina utilizando Terraform.

---

## 🏗️ Arquitetura da Infraestrutura

O Terraform provisiona os seguintes recursos na AWS:

- **S3 Bucket**: Armazenamento do state file do Terraform com controle de versão.
- **DynamoDB Table**: Controle de concorrência (lock) para evitar conflitos no state file.
- **ECR Repository**: Repositório privado para armazenar imagens Docker da aplicação.
- **VPC**: Rede virtual isolada com subnets públicas e privadas.
- **EKS Cluster**: Cluster Kubernetes gerenciado para execução dos containers.
- **RDS Database**: Banco de dados MySQL gerenciado com integração nativa ao AWS Secrets Manager para senhas.

---

## 📋 Pré-requisitos e Configuração Local

Antes de começar, certifique-se de ter as ferramentas instaladas e configuradas em sua máquina local:

### 1. Ferramentas Necessárias
* **AWS CLI** instalado (v2 preferencialmente).
* **Terraform** instalado (versão `1.15.7` ou superior).

### 2. Configurando a CLI da AWS Localmente
Você precisa configurar suas credenciais para que o Terraform consiga autenticar na sua conta AWS. Execute:

```bash
aws configure
```

Insira seu `AWS Access Key ID`, `AWS Secret Access Key`, `Default region name` (use `us-east-1` para manter o padrão do projeto) e o formato de saída (`json`).

Se você estiver utilizando um ambiente temporário (como **AWS Academy**), exporte o token de sessão no seu terminal antes de rodar o Terraform:

```bash
export AWS_ACCESS_KEY_ID="sua-access-key"
export AWS_SECRET_ACCESS_KEY="sua-secret-key"
export AWS_SESSION_TOKEN="seu-session-token"
export AWS_DEFAULT_REGION="us-east-1"
```

### 3. Validando o Acesso Local

Antes de iniciar o Terraform, valide se sua CLI está respondendo e apontando para a conta correta:

```bash
aws sts get-caller-identity
```

---

## 🔧 Configuração Inicial (Bootstrap)

O primeiro passo é provisionar a infraestrutura base necessária para o funcionamento do backend remoto do Terraform e o repositório de imagens.

### 1. Provisionar Backend (S3 + DynamoDB + ECR)

Navegue para o diretório de bootstrap:

```bash
cd terraform/bootstrap
```

Inicialize o Terraform:

```bash
terraform init
```

Revise o plano de execução:

```bash
terraform plan
```

Aplique as mudanças:

```bash
terraform apply -auto-approve
```

Este comando criará:

* Bucket S3: `bucket-tfstate-1029` 
* Tabela DynamoDB: `meu-terraform-state-lock` 
* Repositório ECR: `oficina-api` 

---

## 🚀 Provisionamento da Infraestrutura Principal

Após o bootstrap, mude para o diretório raiz do Terraform para provisionar a infraestrutura principal (VPC, EKS, RDS).

### 1. Configurar Backend Remoto

Navegue para o diretório principal do Terraform:

```bash
cd ../
```

Inicialize o Terraform configurando o backend remoto apontando para os recursos gerados no bootstrap:

```bash
terraform init \
  -backend-config="bucket=bucket-tfstate-1029" \
  -backend-config="key=global/s3/terraform.tfstate" \
  -backend-config="region=us-east-1" \
  -backend-config="dynamodb_table=meu-terraform-state-lock" \
  -backend-config="encrypt=true"
```

### 2. Definir Variáveis

Crie um arquivo `terraform.tfvars` para evitar expor dados sensíveis no terminal:

```hcl
db_username  = "admin"
environment  = "production"
cluster_name = "oficina-cluster"
```

### 3. Planejar e Aplicar a Infraestrutura

```bash
terraform plan -out=tfplan
terraform apply tfplan
```

Os outputs importantes gerados serão:

* `rds_endpoint`: Endpoint do banco de dados (ex: `oficina-rds.xxxx.us-east-1.rds.amazonaws.com`).
* `cluster_endpoint`: Endpoint do cluster EKS.
* `cluster_name`: Nome do cluster Kubernetes (`oficina-cluster`).

