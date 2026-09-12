# 🔧 Sistema de Gestão de Oficina — SOAT FIAP

MVP do back-end de um **Sistema Integrado de Atendimento e Execução de Serviços** para oficina mecânica, desenvolvido com Java 21, Spring Boot 4.x, Clean Architecture e DDD. Orquestrado em AWS EKS via Helm, com infraestrutura provisionada por Terraform em repositórios dedicados.

---

## ⚡ Início Rápido (local com Docker Compose)

```bash
git clone <repo-url>
cd tech-challenge-soat-fiap
docker compose up --build -d
```

| Serviço | URL |
|---------|-----|
| API REST | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| SonarQube | http://localhost:9000 (admin / admin) |
| MySQL | localhost:3306 |

---

## 🗺️ Mapa da Documentação

### 📋 Requisitos e Negócio

| Documento | Descrição |
|-----------|-----------|
| [Requisitos Fase 1](project-knowledge/tech-challenge.md) | Escopo do MVP, fluxos obrigatórios e entregas |
| [Requisitos Fase 2](project-knowledge/tech-challange-parte-2.md) | Evolução para K8s, IaC e CI/CD |
| [Dicionário de Linguagem Ubíqua](documentos/Dicionario-linguagem-ubiqua.pdf) | Terminologia de negócio (DDD) |
| [Domain Storytelling](documentos/DomainStorytelling.svg) | Jornada do usuário e interações de domínio |

### 🏗️ Arquitetura

| Documento | Descrição |
|-----------|-----------|
| [Arquitetura do Projeto](project-knowledge/architecture.md) | Clean Architecture, pacotes e Bounded Contexts |
| [Diagrama de Componentes](documentos/diagrama-componentes.svg) | Visão de nuvem, APIs, banco e monitoramento |
| [Diagrama de Sequência](documentos/diagrama-sequencia.svg) | Fluxo de autenticação e abertura de OS |
| [Diagrama de Arquitetura](documentos/diagrama-arquitetura.svg) | Visão estrutural da solução |
| [Diagrama de Infraestrutura](documentos/diagrama-infra.svg) | Visão dos serviços e deploy AWS |

### 🛡️ Segurança e APIs

| Documento | Descrição |
|-----------|-----------|
| [Autenticação JWT](project-knowledge/api-authentication.md) | Login, Bearer Token, endpoints públicos vs protegidos |
| [Relatório Sonar (PDF)](documentos/Analise-vulnerabilidade-Sonar.pdf) | Análise estática de código |
| [Relatório OWASP (PDF)](documentos/Dependency-Check-Report.pdf) | Análise de vulnerabilidades de dependências |

### ⚙️ Execução e DevOps

| Documento | Descrição |
|-----------|-----------|
| [Configuração Local e Testes](project-knowledge/local-setup-testing.md) | Docker Compose, Maven, JaCoCo, SonarQube, OWASP |
| [Pipeline CI/CD](project-knowledge/ci-cd.md) | GitHub Actions — Build → Push ECR → Deploy Helm/EKS |
| [Deploy Kubernetes (Helm)](project-knowledge/kubernetes-deployment.md) | Deploy manual no EKS com Helm |
| [Provisionamento Terraform](project-knowledge/terraform-provisioning.md) | IaC — visão geral e ordem dos repositórios |

### 📐 Decisões Técnicas

| Documento | Descrição |
|-----------|-----------|
| [ADRs — Índice](project-knowledge/adr.md) | 20 Architecture Decision Records (ADR-001 a ADR-020) |
| [ADRs — Pasta completa](project-knowledge/ADR/) | Arquivos individuais de cada ADR |
| [RFCs — Índice](project-knowledge/RFC/README.md) | 5 Request for Comments (escolha de nuvem, banco, auth...) |

---

## 🔄 Repositórios da Plataforma

Este é o repositório da aplicação principal. A infraestrutura está distribuída em repositórios separados com a seguinte ordem de provisionamento:

```
[1] k8s-infra   →  VPC + EKS + ECR + API Gateway + New Relic
[2] db-infra    →  RDS MySQL
[3] oficina-api →  Pipeline Helm → EKS  (este repositório)
[4] auth-lambda →  Lambda + rota POST /v1/auth/login
```

| Repositório | Propósito |
|-------------|-----------|
| `tech-challenge-soat-fiap-k8s-infra` | VPC, EKS, ECR, API Gateway HTTP v2, New Relic |
| `tech-challenge-soat-fiap-db-infra` | RDS MySQL 8.0 em subnets privadas |
| `tech-challenge-soat-fiap-auth-lambda` | Lambda Authorizer — geração de JWT |

---

## 🛠️ Tecnologias

| Tecnologia | Versão | Uso |
|-----------|--------|-----|
| Java | 21 (Corretto) | Runtime |
| Spring Boot | 4.0.3 | Framework principal |
| Spring Security | 6.x | Autenticação JWT stateless |
| Spring Data JPA | 3.x | ORM / persistência |
| Flyway | 10.x | Migrations de banco |
| MySQL | 8.0 | Banco de dados produção |
| H2 | — | Banco em memória (testes) |
| Docker / Docker Compose | 24+ | Containerização local |
| Helm | 3.14+ | Empacotamento Kubernetes |
| JUnit 5 + Mockito | — | Testes unitários |
| JaCoCo | 0.8.12 | Cobertura de testes (mín. 80%) |
| SonarQube | Community | Análise estática de código |
| OWASP Dependency Check | 12.2.0 | Vulnerabilidades de dependências |
| New Relic Java Agent | 9.4.0 | APM e observabilidade |
| Terraform | >= 1.6.0 | IaC (nos repos de infra) |
| AWS EKS | 1.36 | Kubernetes gerenciado |

---

## 🏛️ Arquitetura em Resumo

```
com.techchallenge.oficina/
├── administrativo/   →  CRUD: Clientes, Veículos, Serviços, Peças, Estoque
├── os/               →  Core: Ordem de Serviço (RECEBIDA → ENTREGUE)
├── sharedkernel/     →  JWT Filter, Security Config, Value Objects comuns
└── infrastructure/   →  Config global Spring (Security, Swagger)
```

Cada bounded context segue **Clean Architecture** com 4 camadas: `domain` → `application` → `infrastructure` → `web`.

---

## 🔐 Autenticação Rápida

```bash
# Local (Spring Security diretamente)
curl -s -X POST http://localhost:8080/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"secret123"}'

# Produção (via API Gateway → Lambda Authorizer)
curl -s -X POST https://<api-gateway-url>/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"secret123"}'
```

O token retornado deve ser usado no header `Authorization: Bearer <token>` em todas as chamadas a `/v1/admin/**`.

Documentação completa: [api-authentication.md](project-knowledge/api-authentication.md)
