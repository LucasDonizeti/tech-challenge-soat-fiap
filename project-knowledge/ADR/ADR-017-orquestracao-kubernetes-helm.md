# ADR-017: Orquestração de Containers com Kubernetes (EKS) e Helm

**Número do ADR:** 017  
**Título:** Orquestração de Containers com Kubernetes (EKS) e Helm  
**Data:** 2026-04-25  
**Responsável:** Arquiteto do Projeto  
**Status:** Aceito

## Contexto
O Tech Challenge Fase 2 estabelece como requisito obrigatório o deploy da aplicação em **Kubernetes**, com manifestos para Deployment, Service, ConfigMap, Secret e HPA. A escolha envolve três decisões encadeadas:

1. **Qual distribuição de Kubernetes** usar (gerenciada na AWS vs auto-gerenciada)
2. **Como empacotar os manifestos** (YAML puro vs Helm vs Kustomize)
3. **Como expor a aplicação** externamente de forma segura (Ingress, LoadBalancer ou outra estratégia)

O projeto já estava 100% na AWS (ADR-003, ADR-004) com containers no ECR, o que condicionou a escolha do Kubernetes gerenciado.

## Decisão
Foi decidido adotar **Amazon EKS v1.36** com **Helm Charts próprios** (`k8s/oficina`), expondo a aplicação via **NodePort + NLB interno + API Gateway** (sem Ingress Controller):

### Cluster EKS
- Versão: 1.36
- Node Group: `t3.medium`, 1–2 nós, private subnets
- Add-ons: `vpc-cni`, `kube-proxy`, `coredns`
- IAM Role: `LabRole` (restrição do AWS Academy)

### Helm Chart (`k8s/oficina`)
- `values.yaml` para desenvolvimento local
- `values-prod.yaml` para produção EKS (sobrescrita via `helm upgrade --install -f`)
- Templates: `deployment.yaml`, `service.yaml`, `configmap.yaml`, `secret.yaml`, `hpa.yaml`, `serviceaccount.yaml`

### Configurações de produção
| Recurso | Configuração |
|---------|-------------|
| Réplicas iniciais | 2 |
| HPA min / max | 1 / 4 |
| HPA CPU trigger | 70% |
| HPA Memória trigger | 80% |
| Resources requests | 250m CPU, 512Mi RAM |
| Resources limits | 1 CPU, 2Gi RAM |
| Service type | NodePort (porta 30080 → 8080) |
| Liveness probe | `GET /actuator/health/liveness` (delay 30s, período 15s) |
| Readiness probe | `GET /actuator/health/readiness` (delay 20s, período 10s) |

### Exposição externa
Fluxo: `API Gateway HTTP v2 → VPC Link → NLB Interno → Target Group (NodePort 30080) → Pod :8080`

O NLB é provisionado pelo Terraform (não pelo AWS Load Balancer Controller), com `aws_autoscaling_attachment` para registrar automaticamente novos nós no Target Group quando o HPA escalar.

### New Relic Agent
Injetado via `initContainer` (imagem `newrelic/newrelic-java-init:9.4.0`) copiando o JAR para volume `emptyDir`, montado no container da aplicação com `JAVA_TOOL_OPTIONS: -javaagent:/opt/newrelic/newrelic.jar`.

## Justificativa
- **EKS é obrigatório:** O Tech Challenge Fase 2 exige Kubernetes explicitamente. EKS é a distribuição gerenciada disponível no AWS Academy
- **Helm vs YAML puro:** Helm permite reutilizar o mesmo chart entre ambientes via substituição de values, habilita `--rollback-on-failure` automático e mantém histórico de releases (`helm history`)
- **Helm vs Kustomize:** Helm é o padrão de mercado mais adotado para empacotamento de aplicações Kubernetes; Kustomize tem vantagens em patches declarativos mas não oferece release management
- **NodePort em vez de LoadBalancer Service:** A `LabRole` do AWS Academy não tem permissão para criar NLBs via anotações de Service Kubernetes (AWS Load Balancer Controller exige IRSA). NodePort + NLB gerenciado pelo Terraform resolve a limitação sem perda de funcionalidade
- **Sem Ingress Controller:** ALB Ingress Controller e NGINX Ingress também criam LoadBalancer Services — mesma restrição de IAM. API Gateway + VPC Link cobre todos os casos de uso necessários
- **InitContainer para New Relic:** Desacopla a versão do agente da imagem Docker — atualizar o agente não requer rebuild da imagem da aplicação (ver ADR-015)
- **HPA baseado em CPU+Memória:** Escalamento reativo mais adequado para workload web com cargas variáveis de requisições; métricas disponíveis sem configuração adicional de Metrics Server

## Alternativas Consideradas

### Amazon ECS (Elastic Container Service)
- **Vantagem:** Mais simples, sem overhead de control plane, integração nativa com ALB/ECR/Secrets Manager
- **Desvantagem:** **Não atende ao requisito** de Kubernetes do Tech Challenge Fase 2
- **Decisão:** Descartada por não cumprir requisito obrigatório

### Kubernetes self-managed (kubeadm no EC2)
- **Vantagem:** Sem custo de cluster control plane EKS ($0.10/h)
- **Desvantagem:** Responsabilidade total de upgrades, patches e recuperação de falhas do control plane; ponto único de falha; não reflete uso de mercado
- **Decisão:** Descartada pelo overhead operacional

### Manifests YAML puros (sem Helm)
- **Vantagem:** Sem dependência de ferramenta adicional
- **Desvantagem:** Sem mecanismo de substituição de valores entre ambientes; sem rollback automático; sem histórico de releases
- **Decisão:** Descartada em favor de Helm

### Ingress NGINX Controller
- **Vantagem:** Padrão de mercado para exposição HTTP/HTTPS em Kubernetes
- **Desvantagem:** Cria um LoadBalancer Service que requer permissão IAM não disponível na `LabRole`
- **Decisão:** Descartada pela restrição de IAM do AWS Academy

## Consequências

### Benefícios
- Deploy completamente automatizado e reproduzível via pipeline GitHub Actions
- Rollback automático em falha de deploy via `--rollback-on-failure`
- HPA garante que a aplicação escala conforme carga sem intervenção manual
- Chart Helm versionado junto com o código-fonte — infraestrutura como código
- Separação clara de configurações sensíveis (Secret) e não-sensíveis (ConfigMap)

### Desafios
- EKS control plane custa $0.10/h (~$72/mês) — necessário destruir e recriar o cluster no AWS Academy para economizar créditos
- `t3.medium` tem 4 GB RAM — com 2 réplicas (512Mi cada) + sistema + New Relic, a margem é limitada
- NodePort expõe porta fixa (30080) em todos os nós — em produção real, preferiria-se LoadBalancer com IRSA
- O `initialDelaySeconds: 30` do liveness probe adiciona 30s à primeira verificação de saúde — necessário pela inicialização da JVM + Flyway migrations

### Impacto no Desenvolvimento
- Deploy manual local via `helm upgrade --install` com `values.yaml` (sem NodePort, sem ECR)
- Deploy de produção via pipeline GitHub Actions com `values-prod.yaml`
- Segredos injetados via `--set secret.*` na pipeline, nunca commitados no repositório
- Novos recursos Kubernetes devem ser adicionados como templates Helm, não como YAML avulso

## Referências
- Amazon EKS Documentation: https://docs.aws.amazon.com/eks/
- Helm Documentation: https://helm.sh/docs/
- Kubernetes HPA: https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale/
- Spring Boot Actuator Health: https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html
- ADR-004: Containerização com Docker
- ADR-015: Observabilidade com New Relic APM
- RFC-004: Orquestração de Containers — Amazon EKS + Helm
