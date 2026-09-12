# RFCs — Request for Comments

Esta pasta reúne os documentos de **Request for Comments (RFC)** do projeto **Sistema de Gestão de Oficina**.

---

## O que é um RFC?

Um RFC é um documento que propõe e registra **decisões técnicas de alto nível**, com análise comparativa de alternativas, justificativa da escolha adotada e consequências esperadas. Diferente dos ADRs (que registram decisões já tomadas no nível de código), os RFCs cobrem escolhas estratégicas de plataforma, arquitetura de solução e padrões transversais, funcionando como registro vivo do raciocínio por trás das grandes apostas do projeto.

---

## Relação com os ADRs

| Documento | Escopo | Quando usar |
|-----------|--------|-------------|
| **RFC** | Decisão estratégica / plataforma / cross-cutting | Escolha de nuvem, banco, estratégia de auth, pipeline |
| **ADR** | Decisão de design no código | Estrutura de pacotes, tipo de dado, padrão de exceção |

Os ADRs do projeto estão em [`../ADR/`](../ADR/).

---

## Índice de RFCs

| ID | Título | Status | Data |
|----|--------|--------|------|
| [RFC-001](RFC-001-escolha-provedor-nuvem-aws.md) | Escolha do Provedor de Nuvem — AWS | Aceito | 2026-03-20 |
| [RFC-002](RFC-002-estrategia-autenticacao-lambda-jwt.md) | Estratégia de Autenticação — Lambda Authorizer + JWT | Aceito | 2026-03-22 |
| [RFC-003](RFC-003-banco-de-dados-rds-mysql.md) | Banco de Dados Relacional — Amazon RDS MySQL | Aceito | 2026-03-22 |
| [RFC-004](RFC-004-orquestracao-containers-eks-helm.md) | Orquestração de Containers — Amazon EKS + Helm | Aceito | 2026-03-25 |
| [RFC-005](RFC-005-estrategia-observabilidade-new-relic.md) | Estratégia de Observabilidade — New Relic APM | Aceito | 2026-04-01 |

---

## Status possíveis

- **Rascunho** — em discussão, sem decisão final
- **Aceito** — decisão aprovada e em vigor
- **Substituído** — substituído por outro RFC (indica qual)
- **Rejeitado** — proposta avaliada e descartada com justificativa
