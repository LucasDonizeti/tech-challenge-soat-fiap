# Deploy em Kubernetes com Helm

Deploy manual da aplicação `oficina-api` no cluster EKS usando Helm, alinhado com o que a pipeline faz automaticamente.

---

## Arquitetura do deployment

```
API Gateway HTTP v2
    │  ANY /{proxy+} → VPC Link → NLB Interno → NodePort 30080
    ▼
EKS Cluster (oficina-cluster)
    └── Namespace: default
        ├── Deployment: oficina-api (1–4 réplicas, rolling update)
        │   ├── initContainer: newrelic-java-init:9.4.0 (copia newrelic.jar)
        │   └── Container: oficina-api (port 8080, New Relic javaagent)
        ├── Service: NodePort 30080 → 8080
        ├── ConfigMap: variáveis não-sensíveis do Spring
        ├── Secret: datasource, JWT, admin creds, NR license key
        └── HPA: min 1, max 4, CPU 70%, Memória 80%
```

> **Ingress e HTTPRoute estão desabilitados** em produção (`values-prod.yaml`). O tráfego chega exclusivamente via API Gateway → NLB → NodePort.

---

## Pré-requisitos

1. AWS CLI configurado com acesso ao EKS
2. `kubectl` instalado
3. `Helm` >= 3.14 instalado
4. k8s-infra, db-infra e a imagem Docker já provisionados

---

## 1. Conectar ao cluster

```bash
aws eks update-kubeconfig \
  --region us-east-1 \
  --name oficina-cluster

# Verificar
kubectl get nodes
kubectl cluster-info
```

---

## 2. Criar o imagePullSecret do ECR

O Kubernetes precisa de credenciais para puxar a imagem privada do ECR:

```bash
ECR_REGISTRY=$(aws ecr describe-repositories \
  --repository-names oficina-api \
  --region us-east-1 \
  --query 'repositories[0].repositoryUri' \
  --output text | sed 's|/oficina-api||')

ECR_PASSWORD=$(aws ecr get-login-password --region us-east-1)

kubectl create secret docker-registry aws-ecr-secret \
  --docker-server="$ECR_REGISTRY" \
  --docker-username=AWS \
  --docker-password="$ECR_PASSWORD" \
  --namespace=default \
  --dry-run=client -o yaml | kubectl apply -f -
```

---

## 3. Recuperar endpoint e senha do RDS

```bash
# Endpoint
RDS_ENDPOINT=$(aws rds describe-db-instances \
  --db-instance-identifier oficina-rds \
  --query 'DBInstances[0].Endpoint.Address' \
  --output text)

# Senha do Secrets Manager
SECRET_ARN=$(aws rds describe-db-instances \
  --db-instance-identifier oficina-rds \
  --query 'DBInstances[0].MasterUserSecret.SecretArn' \
  --output text)

DB_PASSWORD=$(aws secretsmanager get-secret-value \
  --secret-id "$SECRET_ARN" \
  --query 'SecretString' \
  --output text | python3 -c "import sys,json; print(json.load(sys.stdin)['password'])")

echo "RDS: $RDS_ENDPOINT"
```

---

## 4. Deploy com Helm

```bash
# URL do ECR
ECR_REGISTRY=$(aws ecr describe-repositories \
  --repository-names oficina-api --region us-east-1 \
  --query 'repositories[0].repositoryUri' --output text | sed 's|/oficina-api||')

helm upgrade --install oficina ./k8s/oficina \
  --namespace default \
  --create-namespace \
  -f k8s/oficina/values-prod.yaml \
  --set image.repository="$ECR_REGISTRY/oficina-api" \
  --set image.tag=latest \
  --set secret.SPRING_DATASOURCE_URL="jdbc:mysql://${RDS_ENDPOINT}:3306/oficina" \
  --set secret.SPRING_DATASOURCE_USERNAME="admindb" \
  --set secret.SPRING_DATASOURCE_PASSWORD="$DB_PASSWORD" \
  --set secret.JWT_SECRET="sua-chave-jwt" \
  --set secret.SECURITY_USER_NAME="admin" \
  --set secret.SECURITY_USER_PASSWORD="secret123" \
  --set secret.NEW_RELIC_LICENSE_KEY="sua-license-key" \
  --rollback-on-failure \
  --timeout 10m \
  --wait
```

---

## 5. Validar o deploy

```bash
# Aguardar rollout completo
kubectl rollout status deployment/oficina-api --timeout=5m

# Ver pods
kubectl get pods -l app.kubernetes.io/name=oficina

# Ver HPA (escala automática)
kubectl get hpa

# Ver service (confirmar NodePort 30080)
kubectl get service oficina-api

# Logs da aplicação
kubectl logs -l app.kubernetes.io/name=oficina --tail=50

# Logs em tempo real
kubectl logs -l app.kubernetes.io/name=oficina -f
```

---

## 6. Obter URL e testar

```bash
# URL do API Gateway (ponto de entrada único)
API_URL=$(aws apigatewayv2 get-apis \
  --region us-east-1 \
  --query 'Items[?Name==`oficina-api-gateway`].ApiEndpoint' \
  --output text)

echo "API URL: $API_URL"

# Health check
curl -s "$API_URL/actuator/health" | python3 -m json.tool

# Swagger UI
echo "Swagger: $API_URL/swagger-ui/index.html"

# Login
curl -s -X POST "$API_URL/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"secret123"}' \
  | python3 -m json.tool
```

---

## Helm — comandos de gestão

```bash
# Ver releases instaladas
helm list

# Ver histórico de releases
helm history oficina

# Rollback para versão anterior
helm rollback oficina 1

# Ver valores em uso
helm get values oficina

# Desinstalar
helm uninstall oficina
```

---

## Configurações de produção (values-prod.yaml)

| Parâmetro | Valor produção |
|-----------|---------------|
| `replicaCount` | 2 réplicas iniciais |
| `service.type` | `NodePort` |
| `service.nodePort` | `30080` |
| `autoscaling.enabled` | `true` |
| `autoscaling.minReplicas` | `1` |
| `autoscaling.maxReplicas` | `4` |
| `autoscaling.targetCPU` | `70%` |
| `autoscaling.targetMemory` | `80%` |
| `resources.requests.memory` | `512Mi` |
| `resources.limits.memory` | `2Gi` |
| `resources.requests.cpu` | `250m` |
| `resources.limits.cpu` | `1` |
| `livenessProbe` | `GET /actuator/health/liveness`, delay 30s |
| `readinessProbe` | `GET /actuator/health/readiness`, delay 20s |
| `ingress.enabled` | `false` |
| `httpRoute.enabled` | `false` |

---

## Troubleshooting

```bash
# Pod não sobe — ver eventos
kubectl describe pod <nome-do-pod>

# Erro de imagem (ImagePullBackOff)
# → Recriar o imagePullSecret (passo 2)

# Erro de banco (app crashloop)
# → Verificar se o RDS está disponível
aws rds describe-db-instances \
  --db-instance-identifier oficina-rds \
  --query 'DBInstances[0].DBInstanceStatus' --output text

# Liveness probe falha (unhealthy)
# → initContainer do New Relic pode ter demorado — verificar logs do init
kubectl logs <nome-do-pod> -c newrelic-java-init

# HPA sem métricas (Unknown)
# → Metrics Server precisa estar ativo no EKS
kubectl get pods -n kube-system | grep metrics-server
```
