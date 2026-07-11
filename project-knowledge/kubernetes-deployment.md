# Deploy em Kubernetes com Helm

Esta documentação descreve como realizar o deploy manual da aplicação no cluster Kubernetes utilizando Helm charts, de forma alinhada com a pipeline de CI/CD.

---

## 🏗️ Arquitetura do Deployment

A aplicação é implantada no cluster AWS EKS utilizando as definições contidas na pasta `templates/`:

- **Deployment**: Gerencia os pods da aplicação com estratégia de rolling update.
- **Service**: Expõe a aplicação internamente no cluster (`ClusterIP`).
- **Ingress/HTTPRoute**: Expõe a aplicação externamente via domínio/Gateway API.
- **ConfigMap & Secret**: Armazenam variáveis de ambiente e credenciais da API.
- **HPA**: Escala automaticamente baseado em CPU/memória (Mín: 2, Máx: 10 pods).

---

## 📋 Pré-requisitos Locais

Antes de interagir com o cluster, certifique-se de ter executado localmente:
1. **Configuração da AWS CLI** realizada e validada via `aws sts get-caller-identity`.
2. **kubectl** instalado.
3. **Helm** instalado (versão 3.x).

---

## 🔧 Configuração de Contexto e Autenticação

### 1. Configurar Contexto do EKS localmente
Atualize o arquivo `kubeconfig` da sua máquina para conseguir ditar comandos ao cluster provisionado:
```bash
aws eks update-kubeconfig \
  --region us-east-1 \
  --name oficina-cluster
```

### 2. Verificar Conexão

```bash
kubectl get nodes
kubectl cluster-info
```

---

## 🚀 Deploy Manual com Helm

### 1. Navegar para o Diretório do Chart

```bash
cd k8s/oficina
```

### 2. Autenticar no ECR e Criar ImagePullSecret

Para que o Kubernetes consiga baixar a imagem do seu repositório privado no ECR, rode o comando abaixo para gerar o token dinâmico:

```bash
ECR_REGISTRY=<sua-conta-aws>.dkr.ecr.us-east-1.amazonaws.com
ECR_PASSWORD=$(aws ecr get-login-password --region us-east-1)

kubectl create secret docker-registry aws-ecr-secret \
  --docker-server=$ECR_REGISTRY \
  --docker-username=AWS \
  --docker-password=$ECR_PASSWORD \
  --namespace=default \
  --dry-run=client -o yaml | kubectl apply -f -
```

### 3. Buscar a Senha do RDS (Seguindo o padrão da Pipeline)

A senha do RDS é gerenciada nativamente pelo AWS Secrets Manager. Para fazer o deploy manual idêntico à pipeline, recupere a senha gerada automaticamente:

```bash
SECRET_ARN=$(aws rds describe-db-instances \
  --db-instance-identifier oficina-rds \
  --query 'DBInstances[0].MasterUserSecret.SecretArn' \
  --output text)

DB_PASSWORD=$(aws secretsmanager get-secret-value \
  --secret-id "$SECRET_ARN" \
  --query 'SecretString' \
  --output text | python3 -c "import sys,json; print(json.load(sys.stdin)['password'])")
```

### 4. Instalar/Atualizar o Release com Helm

Substitua os placeholders `<sua-conta-aws>` e `<rds-endpoint>` (obtido nos outputs do Terraform) antes de rodar:

```bash
RDS_ENDPOINT="<rds-endpoint>" # Ex: oficina-rds.xxxx.us-east-1.rds.amazonaws.com
ECR_REGISTRY="<sua-conta-aws>.dkr.ecr.us-east-1.amazonaws.com"

helm upgrade --install oficina . \
  --namespace default \
  --create-namespace \
  -f values-prod.yaml \
  --set image.repository=$ECR_REGISTRY/oficina-api \
  --set image.tag=latest \
  --set secret.SPRING_DATASOURCE_URL="jdbc:mysql://${RDS_ENDPOINT}:3306/oficina" \
  --set secret.SPRING_DATASOURCE_USERNAME="admin" \
  --set secret.SPRING_DATASOURCE_PASSWORD="$DB_PASSWORD" \
  --set secret.JWT_SECRET="sua-chave-jwt-aqui" \
  --set secret.SECURITY_USER_NAME="admin" \
  --set secret.SECURITY_USER_PASSWORD="sua-senha-admin" \
  --atomic \
  --timeout 10m \
  --wait
```

### 5. Validar a Inicialização

Acompanhe o status do deploy até que ele mude para `Running`:

```bash
kubectl rollout status deployment/oficina-api --namespace=default --timeout=5m
```
