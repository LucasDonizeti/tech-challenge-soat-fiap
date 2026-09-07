# RFC-005 — Estratégia de Observabilidade: New Relic APM

| Campo | Valor |
|-------|-------|
| **ID** | RFC-005 |
| **Título** | Estratégia de Observabilidade — New Relic APM |
| **Status** | Aceito |
| **Data** | 2026-04-01 |
| **Autores** | Time de Arquitetura |
| **ADRs relacionados** | ADR-005 (Logging SLF4J), ADR-013 (SonarQube) |
| **RFCs relacionados** | RFC-001 (AWS), RFC-004 (EKS) |

---

## 1. Contexto

Uma aplicação em produção no Kubernetes sem instrumentação de observabilidade é operacionalmente cega: não há como saber se um endpoint está lento, se uma query está causando N+1, se há aumento de erros 5xx, ou se um pod está consumindo memória excessiva antes de ser OOM-killed.

O projeto precisava definir uma estratégia de observabilidade que cobrisse três pilares fundamentais:

- **Métricas** — CPU, memória, latência, throughput, taxa de erros
- **Traces distribuídos** — rastreamento de requisições ponta a ponta (API Gateway → Spring Boot → MySQL)
- **Logs** — correlação de logs com traces para investigação de incidentes

Adicionalmente, o ambiente AWS Academy tem restrições sobre quais serviços da AWS estão disponíveis, o que condicionou parcialmente a escolha.

---

## 2. Problema

Precisamos definir:

1. **Plataforma de APM** (Application Performance Monitoring): New Relic vs Datadog vs CloudWatch vs Prometheus/Grafana
2. **Estratégia de injeção** do agente de monitoramento sem alterar a imagem Docker
3. **Cobertura de monitoramento sintético** para health check externo
4. **Integração com dashboards** para visualização do estado da aplicação
5. **Alertas** para degradação de performance ou indisponibilidade

---

## 3. Alternativas consideradas

### 3.1 New Relic APM (Java Agent + Synthetic Monitor) ✅ Escolhida

**Componentes utilizados:**
- **New Relic Java Agent 9.4.0** — instrumentação automática de Spring Boot, Hibernate, JDBC e HTTP
- **New Relic Synthetic Monitor** — HTTP monitor externo que pinga `/actuator/health/liveness` a cada 2 minutos
- **New Relic Dashboard** — provisionado via Terraform com `newrelic_one_dashboard` e JSON pré-definido
- **New Relic Terraform Provider** — integração declarativa para criar monitores, dashboards e alertas

**Prós:**
- Agente Java instrumenta automaticamente Spring Boot, JPA/Hibernate, MySQL JDBC e Spring Security — zero código de instrumentação manual
- Free Tier generoso: 100 GB/mês de ingestão, 1 usuário full, retenção de dados por 8 dias
- Distributed Tracing habilitado por padrão — rastreia requisições do API Gateway ao banco
- Terraform Provider oficial (`newrelic/newrelic`) permite provisionar monitores e dashboards como IaC
- Logs In Context: correlaciona automaticamente logs do Logback com o trace ID do New Relic
- `NEW_RELIC_DISTRIBUTED_TRACING_ENABLED=true` no ConfigMap sem código adicional

**Contras:**
- License key (ingestion key) precisa ser gerenciada como segredo (`NEW_RELIC_LICENSE_KEY`)
- Free Tier tem limite de retenção de 8 dias — dados históricos são descartados após esse período
- Agente Java adiciona ~50–80ms ao startup da JVM e ~20–30MB de heap overhead

---

### 3.2 Amazon CloudWatch + Container Insights ✗ Não adotado como solução principal

**Prós:**
- Nativo à AWS — zero configuração adicional
- CloudWatch Container Insights tem dashboards pré-prontos para EKS (CPU, memória, rede por pod/node)
- Sem custo adicional para métricas básicas de EC2 e EKS

**Por que não foi adotado como solução principal:**
- **Sem APM**: CloudWatch não instrumenta automaticamente a aplicação Java — sem traces de código, sem análise de queries lentas, sem breakdown por método
- Container Insights requer DaemonSet adicional (`CloudWatch Agent`) e permissões IAM para `PutMetricData` — não disponíveis na `LabRole` sem configuração adicional
- Custo de CloudWatch Logs e métricas customizadas pode ser significativo em produção real
- Ausência de Distributed Tracing nativo (X-Ray seria necessário, mas requer instrumentação adicional)

**Papel no projeto:** CloudWatch é usado passivamente para logs de contêineres via `stdout` dos pods (comportamento padrão do EKS), mas não é a solução primária de observabilidade.

---

### 3.3 Datadog APM ✗ Descartada

**Prós:**
- Plataforma de observabilidade considerada gold-standard no mercado
- Integração nativa com EKS via Datadog Operator
- Dashboards, APM, logs e traces unificados em uma plataforma

**Por que foi descartada:**
- **Custo**: Datadog não tem free tier para APM — planos a partir de ~$15/host/mês para Infrastructure + APM
- Sem créditos Datadog no AWS Academy
- Datadog Operator requer instalação via Helm e API key permanente — não adequado para ambiente temporário do Academy

---

### 3.4 Prometheus + Grafana (self-hosted no EKS) ✗ Descartada

**Prós:**
- Open source — sem custo de licença
- Amplamente adotado no ecossistema Kubernetes
- Spring Boot Actuator expõe endpoint `/actuator/prometheus` nativamente (com `micrometer-registry-prometheus`)

**Por que foi descartada:**
- Exige deploy de Prometheus, Grafana, AlertManager e kube-state-metrics no cluster — overhead significativo para um cluster `t3.medium` com 2 nós
- Spring Boot 4.x (Milestone) ainda não tem suporte estável para `micrometer-registry-prometheus` no contexto do projeto
- Sem dados de APM (traces de métodos Java, análise de queries SQL) — apenas métricas de sistema
- Configuração e manutenção de dashboards Grafana requer esforço adicional que não contribui para os objetivos do Tech Challenge

---

### 3.5 OpenTelemetry (OTEL) Collector + backend externo ✗ Descartada

**Prós:**
- Padrão aberto e vendor-neutral — sem lock-in
- Suportado por múltiplos backends (Jaeger, Zipkin, New Relic, Datadog)

**Por que foi descartada:**
- Configuração do OTEL Collector como DaemonSet ou sidecar adiciona complexidade operacional
- Spring Boot 4.x com OTEL Java Agent ainda tem aspectos experimentais na instrumentação automática de Spring Security e Hibernate
- New Relic suporta OTEL como protocolo de entrada (OTLP) — poderia ser uma evolução futura, mas não justifica a complexidade adicional agora

---

## 4. Decisão

**Adotar New Relic APM** com Java Agent 9.4.0 injetado via initContainer no EKS, complementado por Synthetic Monitor externo e Dashboard provisionado por Terraform.

---

## 5. Arquitetura de observabilidade

```
┌─────────────────────────────────────────────────────────┐
│  EKS Pod: oficina-api                                   │
│                                                          │
│  [New Relic initContainer]                               │
│    newrelic/newrelic-java-init:9.4.0                    │
│    └── copia newrelic.jar → /opt/newrelic/ (emptyDir)   │
│                                                          │
│  [oficina-api Container]                                 │
│    JAVA_TOOL_OPTIONS: -javaagent:/opt/newrelic/newrelic.jar
│    NEW_RELIC_APP_NAME: oficina-api-prod                  │
│    NEW_RELIC_LICENSE_KEY: <secret>                       │
│    NEW_RELIC_DISTRIBUTED_TRACING_ENABLED: true           │
│    NEW_RELIC_LOGS_IN_CONTEXT_ENABLED: true               │
│                                                          │
│    Instrumentação automática:                            │
│    ├── Spring MVC (latência por endpoint)                │
│    ├── Spring Security (tempo de autenticação)           │
│    ├── Hibernate/JPA (queries SQL, N+1 detection)        │
│    ├── HikariCP (pool de conexões)                       │
│    └── HTTP Client (chamadas externas)                   │
└────────────────────┬────────────────────────────────────┘
                     │  HTTPS (ingestão de dados)
                     ▼
        ┌────────────────────────┐
        │  New Relic Cloud       │
        │  (collector endpoint)  │
        │  metric/trace/log      │
        └────────────┬───────────┘
                     │
              ┌──────┴──────┐
              │             │
     ┌────────▼──┐   ┌──────▼──────────────┐
     │ Dashboard │   │ Synthetic Monitor    │
     │ (Terraform│   │ (HTTP ping a cada 2min)
     │  provisionado) │ GET /actuator/health/liveness
     └───────────┘   └─────────────────────┘
```

---

## 6. Provisionamento via Terraform

A observabilidade é provisionada junto com a infraestrutura no repositório `k8s-infra`:

```hcl
# modules/new_relic/main.tf
module "new_relic" {
  source = "./modules/new_relic"

  newrelic_license_key = var.newrelic_license_key
  api_endpoint         = module.api_gateway.api_gateway_endpoint
}
```

O módulo `new_relic` cria:
1. **Synthetic Monitor HTTP** — monitora `GET {api_endpoint}/actuator/health/liveness` a cada 2 minutos
2. **New Relic Dashboard** — via `newrelic_one_dashboard` com JSON do dashboard (throughput, latência, erros, JVM)
3. **Alert Policy + Condition** — alerta quando o Synthetic Monitor falha por 3+ períodos consecutivos

---

## 7. Configuração no Helm Chart

**ConfigMap (variáveis não-sensíveis):**
```yaml
NEW_RELIC_APP_NAME: "oficina-api-prod"
NEW_RELIC_LOGS_IN_CONTEXT_ENABLED: "true"
NEW_RELIC_DISTRIBUTED_TRACING_ENABLED: "true"
LOG_LEVEL: "INFO"
JWT_LOG_LEVEL: "ERROR"   # reduz ruído de logs JWT em produção
```

**Secret (variável sensível):**
```yaml
NEW_RELIC_LICENSE_KEY: ""   # injetado pela pipeline via --set secret.NEW_RELIC_LICENSE_KEY
```

---

## 8. Cobertura de observabilidade por pilar

### 8.1 Métricas (What)
| Métrica | Origem |
|---------|--------|
| Throughput (req/min por endpoint) | New Relic Java Agent |
| Latência P50/P95/P99 | New Relic Java Agent |
| Taxa de erros 4xx/5xx | New Relic Java Agent |
| JVM Heap / GC / Threads | New Relic Java Agent |
| HikariCP pool utilization | New Relic Java Agent |
| CPU / Memória do Pod | Kubernetes metrics (EKS) |
| CPU / Memória do Nó | CloudWatch Container Insights |

### 8.2 Traces (Where / How long)
| Trace | Cobertura |
|-------|-----------|
| HTTP Request → Controller | Automático (Spring MVC) |
| Controller → UseCase | Automático (Spring bean calls) |
| UseCase → JPA Repository → SQL | Automático (Hibernate + JDBC) |
| Distributed Trace (API GW → Pod) | W3C TraceContext via `traceresponse` header |

### 8.3 Logs (Why)
| Log | Estratégia |
|-----|-----------|
| Application logs | SLF4J + Logback → stdout → CloudWatch + New Relic Logs In Context |
| Correlation com traces | `NEW_RELIC_LOGS_IN_CONTEXT_ENABLED=true` injeta `trace.id` e `span.id` nos logs |
| Nível em produção | `LOG_LEVEL: INFO` (geral) — JWT e Security em `WARN/ERROR` para reduzir volume |

---

## 9. Health Check externo (Synthetic Monitor)

O Synthetic Monitor do New Relic realiza uma requisição `GET` ao endpoint de health da aplicação via API Gateway a cada 2 minutos:

```
GET https://{api-id}.execute-api.us-east-1.amazonaws.com/actuator/health/liveness
```

**Resposta esperada:**
```json
HTTP 200 OK
{ "status": "UP" }
```

Este endpoint é configurado como **público** no Spring Security (sem autenticação necessária), garantindo que o monitor não seja bloqueado por JWT.

O monitor confirma que toda a cadeia está funcionando: DNS → API Gateway → VPC Link → NLB → EKS NodePort → Spring Boot.

---

## 10. Decisão sobre Spring Boot Actuator

O Spring Boot Actuator (`spring-boot-starter-actuator`) expõe endpoints padrão de health e métricas que complementam o New Relic Agent:

| Endpoint | Visibilidade | Uso |
|----------|-------------|-----|
| `/actuator/health/liveness` | Público | Kubernetes liveness probe + New Relic Synthetic |
| `/actuator/health/readiness` | Público | Kubernetes readiness probe |
| `/actuator/health` | Público | NLB Target Group health check |
| `/actuator/metrics` | Restrito | Diagnóstico interno |
| `/actuator/info` | Público | Informações de versão da aplicação |

---

## 11. Consequências

**Positivas:**
- Visibilidade total da aplicação sem código de instrumentação manual
- Correlação automática de logs com traces facilita investigação de incidentes
- Synthetic Monitor detecta indisponibilidade em até 2 minutos, com alerta automático
- Dashboard provisionado como IaC — reproduzível a cada recriação do ambiente Academy

**Negativas / Riscos:**
- `NEW_RELIC_LICENSE_KEY` é um segredo sensível — precisa ser rotacionado se comprometido
- New Relic Free Tier retém dados por apenas 8 dias — dados históricos são perdidos
- O Java Agent adiciona ~50–80ms no startup e ~20–30MB de heap — em instâncias com pouca RAM pode ser perceptível
- Se o New Relic estiver com instabilidade, logs contextualizados podem não chegar — impacto na observabilidade de incidentes

---

## 12. Revisão futura

- Avaliar migração para **OpenTelemetry** (OTEL) como camada de instrumentação vendor-neutral, mantendo New Relic como backend via OTLP
- Adicionar **New Relic Alerts** mais granulares: latência P95 > 2s, taxa de erros > 1%, HikariCP pool > 80%
- Habilitar **New Relic Kubernetes Integration** para correlação entre métricas de APM e métricas de infraestrutura do EKS
- Avaliar **New Relic Logs** como destino centralizado de logs em substituição ao CloudWatch, reduzindo custo de ingestão no CloudWatch
