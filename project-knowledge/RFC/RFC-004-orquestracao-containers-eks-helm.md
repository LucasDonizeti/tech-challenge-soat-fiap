# RFC-004 — Orquestração de Containers: Amazon EKS + Helm

| Campo | Valor |
|-------|-------|
| **ID** | RFC-004 |
| **Título** | Orquestração de Containers — Amazon EKS + Helm |
| **Status** | Aceito |
| **Data** | 2026-03-25 |
| **Autores** | Time de Arquitetura |
| **ADRs relacionados** | ADR-004 (Docker), ADR-005 (Logging) |
| **RFCs relacionados** | RFC-001 (AWS), RFC-003 (RDS), RFC-005 (New Relic) |

---

## 1. Contexto

O Tech Challenge exige explicitamente o uso de **Kubernetes** para orquestração dos containers da aplicação. Além de cumprir o requisito obrigatório, o projeto precisava definir:

- Qual distribuição de Kubernetes (gerenciada vs self-managed)
- Como empacotar e versionar os manifestos de deploy
- Como expor a aplicação externamente de forma segura
- Como integrar o deploy automatizado ao pipeline CI/CD

A aplicação `oficina-api` é uma API Spring Boot Java 21 com New Relic Java Agent, que requer estratégias específicas de injeção do agente e configuração de health checks.

---

## 2. Problema

Precisamos definir:

1. **Orquestrador:** Kubernetes gerenciado (EKS) vs auto-gerenciado vs alternativas
2. **Empacotamento:** manifests YAML puros vs Helm Charts vs Kustomize
3. **Exposição externa:** Ingress vs Service LoadBalancer vs API Gateway + NLB
4. **Escalonamento:** réplicas fixas vs HPA
5. **Injeção do New Relic Agent:** runtime vs build-time vs init container
6. **Gerenciamento de segredos:** ConfigMap vs Secret vs External Secrets Operator

---

## 3. Alternativas consideradas

### 3.1 Amazon EKS com Helm Charts ✅ Escolhida

**Configuração adotada:**
- EKS v1.36, managed node group com instâncias `t3.medium` (1–2 nós, private subnets)
- Helm Chart próprio (`k8s/oficina`) com `values.yaml` (local) e `values-prod.yaml` (produção)
- Add-ons: `vpc-cni`, `kube-proxy`, `coredns`
- HPA com CPU 70% e Memória 80%, de 1 a 4 réplicas
- New Relic Agent injetado via initContainer

**Prós:**
- EKS é o Kubernetes gerenciado mais utilizado em ambientes AWS corporativos
- `LabRole` do AWS Academy suporta criação de EKS — confirmado e funcional
- Helm permite reutilizar o mesmo chart para ambientes local e produção via substituição de values
- HPA escala a aplicação automaticamente com base em métricas reais de CPU/Memória
- Managed Node Group gerencia o cycle de vida dos nós (patches, substituição automática)

**Contras:**
- EKS tem custo fixo de $0.10/hora pelo cluster control plane (~$72/mês) — relevante para créditos Academy
- Provisionar EKS via Terraform leva ~12–15 minutos do zero
- Managed Node Group com `t3.medium` tem créditos de burst limitados para cargas sustentadas

---

### 3.2 Kubernetes self-managed (kubeadm no EC2) ✗ Descartada

**Prós:**
- Sem custo de cluster control plane (apenas instâncias EC2)
- Total controle sobre a versão e configuração do cluster

**Por que foi descartado:**
- Responsabilidade total de upgrades, patches de segurança e recuperação de falhas do control plane
- kubeadm em EC2 sem alta disponibilidade é ponto único de falha na master node
- Não é uso padrão de mercado — contradiz o objetivo de aprendizado com ferramentas de produção reais

---

### 3.3 Amazon ECS (Elastic Container Service) ✗ Descartada

**Prós:**
- Mais simples de operar que Kubernetes
- Integração nativa com ALB, ECR, Secrets Manager e CloudWatch
- Sem overhead de control plane (gerenciado pela AWS)
- Fargate elimina gerenciamento de instâncias EC2

**Por que foi descartado:**
- O Tech Challenge exige explicitamente **Kubernetes** — ECS não atende ao requisito
- ECS não é transferível para ambientes não-AWS (lock-in mais severo)

---

### 3.4 Manifests YAML puros (sem Helm) ✗ Descartada

**Prós:**
- Sem dependência de ferramenta adicional
- Mais simples de entender para iniciantes em Kubernetes

**Por que foi descartado:**
- Sem mecanismo de substituição de valores entre ambientes — `values-prod.yaml` vs `values.yaml` não seria possível sem envsubst ou kustomize
- Helm `upgrade --install` com `--rollback-on-failure` oferece rollback automático em falha de deploy — impossível com kubectl apply simples
- Versioning e histórico de releases via `helm history` é funcionalidade crítica para rastreabilidade

---

### 3.5 Kustomize (com kubectl apply -k) ✗ Descartada

**Prós:**
- Nativo ao kubectl — sem dependência extra
- Modelo de patch/overlay mais explícito que values do Helm

**Por que foi descartado:**
- Menos familiar ao time que Helm
- Ausência de funcionalidades de release management (history, rollback, upgrade)
- Helm é o padrão de mercado mais adotado para empacotamento de aplicações Kubernetes

---

## 4. Decisão

**Adotar Amazon EKS v1.36 com Helm Charts** para orquestração da aplicação `oficina-api`, com exposição externa via API Gateway HTTP v2 + VPC Link + NLB interno (sem Ingress Controller).

---

## 5. Arquitetura de exposição da aplicação

A decisão de **não usar Ingress Controller** merece justificativa explícita:

```
[Internet]
    │
    ▼
[API Gateway HTTP v2]          ← ponto de entrada único e gerenciado
    │
    │  VPC Link (privado)
    ▼
[NLB Interno]                  ← Layer 4, dentro da VPC
    │
    │  Target Group: port 30080 (NodePort)
    │  ASG attachment automático
    ▼
[EKS Node — EC2]
    │
    │  NodePort 30080
    ▼
[K8s Service (NodePort)]       ← mapeia 30080 → 8080
    │
    ▼
[oficina-api Pod — port 8080]
```

**Por que NodePort em vez de LoadBalancer Service:**
- `LoadBalancer` type criaria um NLB separado gerenciado pelo AWS Load Balancer Controller — requer permissões IAM que não estão na `LabRole`
- `NodePort` com NLB gerenciado pelo Terraform oferece controle total sobre o target group e health check
- O ASG attachment automático (`aws_autoscaling_attachment`) garante que novas instâncias do node group sejam registradas automaticamente no target group

**Por que não usar Ingress Controller (nginx/ALB):**
- AWS Load Balancer Controller requer Service Account com IRSA (IAM Roles for Service Accounts) — não disponível na `LabRole`
- NGINX Ingress Controller criaria um LoadBalancer Service — mesmo problema de permissões
- API Gateway + VPC Link cobre 100% dos casos de uso necessários sem dependência de permissões adicionais

---

## 6. Estrutura do Helm Chart

```
k8s/oficina/
├── Chart.yaml              (versão 2.0.5, appVersion)
├── values.yaml             (desenvolvimento local)
├── values-prod.yaml        (sobrescreve para produção EKS)
└── templates/
    ├── deployment.yaml     (inclui initContainer New Relic)
    ├── service.yaml        (NodePort 30080 → 8080)
    ├── configmap.yaml      (variáveis não-sensíveis do Spring)
    ├── secret.yaml         (datasource, JWT, admin creds, NR license)
    ├── hpa.yaml            (CPU 70%, Mem 80%, min 1, max 4)
    ├── serviceaccount.yaml
    ├── ingress.yaml        (disabled em prod)
    ├── httproute.yaml      (disabled em prod)
    └── _helpers.tpl
```

**Principais configurações de produção (`values-prod.yaml`):**

| Parâmetro | Valor |
|-----------|-------|
| `replicaCount` | 2 |
| `image.repository` | `<account>.dkr.ecr.us-east-1.amazonaws.com/oficina-api` |
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

---

## 7. Injeção do New Relic Java Agent

A estratégia de injeção do agente New Relic via **initContainer** foi escolhida sobre as alternativas:

| Estratégia | Prós | Contras | Adotada? |
|------------|------|---------|----------|
| **initContainer + emptyDir** | Não altera a imagem Docker; agente atualizável sem rebuild | Depende de volume compartilhado; leve overhead de startup | ✅ Sim |
| Incluir jar na imagem Docker | Simples; sem volume | Rebuild obrigatório para atualizar versão do agente | ✗ Não |
| JAVA_TOOL_OPTIONS no Dockerfile | Sem mudança no K8s | Vínculo forte entre imagem e versão do agente | ✗ Não |
| APM Operator (Kubernetes) | Automatizado para todo o cluster | Requer CRDs e permissões adicionais; complexidade alta | ✗ Não |

**Implementação no Deployment:**
```yaml
initContainers:
  - name: newrelic-java-init
    image: newrelic/newrelic-java-init:9.4.0
    command: ['sh', '-c', 'cp -r /fig/* /opt/newrelic/ || cp -r /newrelic/* /opt/newrelic/ || cp /newrelic-agent.jar /opt/newrelic/newrelic.jar']
    volumeMounts:
      - name: newrelic-agent
        mountPath: /opt/newrelic

containers:
  - name: oficina
    env:
      - name: JAVA_TOOL_OPTIONS
        value: "-javaagent:/opt/newrelic/newrelic.jar"
    volumeMounts:
      - name: newrelic-agent
        mountPath: /opt/newrelic

volumes:
  - name: newrelic-agent
    emptyDir: {}
```

---

## 8. Health Checks

| Probe | Path | Porta | Delay | Período |
|-------|------|-------|-------|---------|
| Liveness | `/actuator/health/liveness` | 8080 | 30s | 15s |
| Readiness | `/actuator/health/readiness` | 8080 | 20s | 10s |

O NLB Target Group também faz health check em `/actuator/health` porta `30080` com matcher `200`.

O `initialDelaySeconds: 30` no liveness probe é necessário para dar tempo à JVM e ao Spring Boot (com Flyway migrations) de inicializar completamente antes que o kubelet comece a verificar.

---

## 9. Estratégia de deploy no pipeline

O pipeline usa `helm upgrade --install` com as flags:
- `--rollback-on-failure` — reverte automaticamente se o deploy falhar
- `--timeout 10m` — aguarda até 10 minutos pelo rollout
- `--wait` — aguarda todos os pods estarem prontos antes de considerar o deploy bem-sucedido

Após o Helm, o pipeline executa `kubectl rollout status` para confirmar que os pods estão saudáveis.

**Tags de imagem:**
- `SHORT_SHA` (7 chars do commit hash) para rastreabilidade
- `latest` como tag adicional para facilitar rollback manual

---

## 10. Consequências

**Positivas:**
- Deploy completamente automatizado e reproduzível via pipeline
- Rollback automático em falha preserva disponibilidade
- HPA garante que a aplicação escala conforme carga sem intervenção manual
- initContainer desacopla o agente de monitoramento da imagem da aplicação

**Negativas / Riscos:**
- EKS control plane custa $0.10/h — destruir e recriar o cluster ao fim de cada sessão do Academy é necessário para economizar créditos
- NodePort expõe uma porta fixa (30080) em todos os nós — em produção real, preferiria-se LoadBalancer com IRSA
- `t3.medium` tem apenas 4 GB de RAM — com 2 réplicas da aplicação (512Mi cada) + sistema, a headroom é limitada

---

## 11. Revisão futura

- Habilitar **IRSA** (IAM Roles for Service Accounts) para substituir `LabRole` por roles específicas por serviço
- Migrar exposição para **AWS Load Balancer Controller** com Ingress e ALB quando IRSA estiver disponível
- Avaliar **Karpenter** como substituto ao Managed Node Group para provisionamento de nós mais eficiente
- Considerar **Bottlerocket** como OS dos nós para melhor segurança e menor overhead
- Adicionar **PodDisruptionBudget** para garantir disponibilidade durante draining de nós
