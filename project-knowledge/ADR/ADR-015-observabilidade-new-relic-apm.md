# ADR-015: Observabilidade com New Relic APM e Synthetic Monitor

**Número do ADR:** 015  
**Título:** Observabilidade com New Relic APM e Synthetic Monitor  
**Data:** 2026-04-20  
**Responsável:** Arquiteto do Projeto  
**Status:** Aceito

## Contexto
Com a migração da aplicação para o Amazon EKS (Fase 2), tornou-se necessário instrumentar a aplicação para observabilidade em produção. As necessidades identificadas foram:

- Monitoramento de latência, throughput e taxa de erros por endpoint
- Rastreamento distribuído de requisições (API Gateway → Spring Boot → MySQL)
- Correlação de logs com traces para investigação de incidentes
- Health check externo independente da infraestrutura interna
- Dashboard de acompanhamento do estado da aplicação provisionado como IaC

O projeto já usa `spring-boot-starter-actuator` para health checks internos (liveness/readiness do Kubernetes), mas isso não cobre APM, traces distribuídos nem monitoramento sintético externo.

## Decisão
Foi decidido adotar o **New Relic APM** com as seguintes configurações:

- **New Relic Java Agent 9.4.0** como dependência Maven, injetado em runtime via `JAVA_TOOL_OPTIONS: -javaagent:/opt/newrelic/newrelic.jar`
- **InitContainer** `newrelic/newrelic-java-init:9.4.0` no Helm Deployment para copiar o agente para volume `emptyDir` compartilhado — desacoplando a versão do agente da imagem Docker da aplicação
- **New Relic Synthetic Monitor HTTP** (ping a cada 2 minutos) apontando para `GET /actuator/health/liveness` via API Gateway, provisionado por Terraform
- **Dashboard** provisionado via `newrelic_one_dashboard` no Terraform do repositório `k8s-infra`
- **Distributed Tracing** habilitado via variável de ambiente `NEW_RELIC_DISTRIBUTED_TRACING_ENABLED=true`
- **Logs In Context** via `NEW_RELIC_LOGS_IN_CONTEXT_ENABLED=true` para correlacionar logs SLF4J/Logback com trace IDs

As variáveis de configuração do agente ficam no ConfigMap (não-sensíveis: `NEW_RELIC_APP_NAME`, flags de feature) e no Secret Kubernetes (`NEW_RELIC_LICENSE_KEY`).

## Justificativa
- **Requisito de observabilidade:** Fase 2 exige monitoramento em ambiente Kubernetes com múltiplos pods e autoescalonamento — sem APM é impossível rastrear problemas entre réplicas
- **Instrumentação automática:** New Relic Java Agent instrumenta Spring MVC, Hibernate, JDBC, HikariCP e Spring Security sem nenhum código adicional na aplicação
- **Free Tier adequado:** 100 GB/mês de ingestão e 1 usuário completo cobrem o volume acadêmico sem custo
- **Terraform Provider oficial:** `newrelic/newrelic` permite provisionar monitores e dashboards como IaC, mantendo observabilidade reproduzível a cada recriação do ambiente AWS Academy
- **Iniciativa InitContainer:** Desacopla a versão do agente da imagem Docker — atualizar o New Relic não exige rebuild da imagem da aplicação
- **Synthetic Monitor independente:** Valida a cadeia completa (DNS → API Gateway → VPC Link → NLB → EKS → Spring Boot) de forma externa e independente, detectando falhas que os probes internos do Kubernetes não detectariam

## Alternativas Consideradas

### Datadog APM
- **Vantagem:** Gold standard do mercado, Kubernetes Integration nativa, correlação APM + infra
- **Desvantagem:** Sem free tier para APM; a partir de ~$15/host/mês. Sem créditos disponíveis no ambiente acadêmico
- **Decisão:** Descartada pelo custo

### Prometheus + Grafana (self-hosted)
- **Vantagem:** Open source, amplamente adotado em Kubernetes, Spring Actuator expõe `/actuator/prometheus` nativamente
- **Desvantagem:** Requer deploy de Prometheus, Grafana e AlertManager no cluster — overhead significativo para nodes `t3.medium`. Sem APM nativo (apenas métricas de sistema, sem traces de código ou análise de queries SQL)
- **Decisão:** Descartada pelo overhead operacional e ausência de APM real

### Amazon CloudWatch + Container Insights
- **Vantagem:** Nativo à AWS, sem configuração adicional para logs de pods via stdout
- **Desvantagem:** Sem instrumentação automática da aplicação Java; Container Insights requer DaemonSet e permissões IAM não disponíveis na `LabRole`; sem Distributed Tracing sem X-Ray (instrumentação adicional)
- **Decisão:** Descartada como solução principal; mantida como fallback passivo para logs via stdout

### OpenTelemetry Collector
- **Vantagem:** Vendor-neutral, padrão CNCF
- **Desvantagem:** Requer sidecar ou DaemonSet; instrumentação automática de Spring Security e Hibernate via OTEL Java Agent ainda experimental no Spring Boot 4.x
- **Decisão:** Descartada para o momento; identificada como evolução futura

## Consequências

### Benefícios
- Visibilidade completa da aplicação em produção sem código de instrumentação manual
- Correlação automática de logs com trace IDs via Logs In Context
- Synthetic Monitor detecta indisponibilidade em ≤2 minutos com alerta automático
- Dashboard e monitor provisionados como IaC — reproduzíveis a cada recriação do ambiente Academy
- Atualização da versão do agente New Relic sem rebuild de imagem Docker

### Desafios
- `NEW_RELIC_LICENSE_KEY` é segredo sensível que precisa ser gerenciado com cuidado (GitHub Secret → Kubernetes Secret)
- Free Tier retém dados por apenas 8 dias — dados históricos são perdidos após esse período
- O Java Agent adiciona ~50–80ms no startup da JVM e ~20–30MB de heap overhead
- Se o New Relic estiver instável, logs contextualizados podem não chegar corretamente

### Impacto no Desenvolvimento
- Adicionar `NEW_RELIC_APP_NAME`, `NEW_RELIC_LICENSE_KEY` e flags de configuração no Helm `values-prod.yaml`
- O endpoint `/actuator/health/liveness` deve permanecer público (sem autenticação) para que o Synthetic Monitor funcione
- Manter `LOG_LEVEL: INFO` em produção para reduzir volume de logs e custo de ingestão

## Referências
- New Relic Java Agent: https://docs.newrelic.com/docs/apm/agents/java-agent/
- New Relic Terraform Provider: https://registry.terraform.io/providers/newrelic/newrelic/latest/docs
- New Relic Synthetic Monitors: https://docs.newrelic.com/docs/synthetics/
- ADR-005: Implementação de Logging com SLF4J
- ADR-013: Implementação de SonarQube para Análise Estática de Código
- RFC-005: Estratégia de Observabilidade — New Relic APM
