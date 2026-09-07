# Architecture Decision Records (ADR)

## Resumo dos ADRs

### ADR-001: Adoção de Clean Architecture com Domain-Driven Design
- **Número:** 001
- **Título:** Adoção de Clean Architecture com Domain-Driven Design
- **Data:** 2026-03-28
- **Responsável:** Arquiteto do Projeto
- **Contexto:** Definição de arquitetura para sistema com múltiplos contextos delimitados
- **Decisão:** Adoção de Clean Architecture combinada com DDD em camadas distintas

### ADR-002: Implementação de Autenticação JWT
- **Número:** 002
- **Título:** Implementação de Autenticação JWT
- **Data:** 2026-03-28
- **Responsável:** Arquiteto do Projeto
- **Contexto:** Requisito obrigatório de autenticação JWT para APIs administrativas
- **Decisão:** Implementação de JWT com assinatura HMAC-SHA256 e expiração de 24 horas

### ADR-003: Escolha do Banco de Dados MySQL
- **Número:** 003
- **Título:** Escolha do Banco de Dados MySQL
- **Data:** 2026-03-28
- **Responsável:** Arquiteto do Projeto
- **Contexto:** Necessidade de banco de dados para MVP com persistência e transações
- **Decisão:** Utilização de MySQL 8.0 com Flyway, JPA/Hibernate e HikariCP

### ADR-004: Containerização com Docker
- **Número:** 004
- **Título:** Containerização com Docker
- **Data:** 2026-03-28
- **Responsável:** Arquiteto do Projeto
- **Contexto:** Requisitos obrigatórios de Dockerfile e docker-compose.yml
- **Decisão:** Uso de Docker + Docker Compose com multi-stage builds

### ADR-005: Implementação de Logging com SLF4J
- **Número:** 005
- **Título:** Implementação de Logging com SLF4J
- **Data:** 2026-03-28
- **Responsável:** Arquiteto do Projeto
- **Contexto:** Necessidade de solução de logging estruturado para observabilidade
- **Decisão:** Uso de SLF4J com Logback, formato JSON em produção

### ADR-006: Estrutura de Pacotes da Aplicação com Clean Architecture e Bounded Contexts
- **Número:** 006
- **Título:** Estrutura de Pacotes da Aplicação com Clean Architecture e Bounded Contexts
- **Data:** 2026-03-28
- **Responsável:** Arquiteto do Projeto
- **Contexto:** Definição de estrutura clara para múltiplos contextos delimitados
- **Decisão:** Estrutura combinando Clean Architecture e DDD com bounded contexts separados

### ADR-007: Estrutura de Migrations Flyway
- **Número:** 007
- **Título:** Estrutura de Migrations Flyway
- **Data:** 2026-04-03
- **Responsável:** Arquiteto do Projeto
- **Contexto:** Necessidade de padrões claros para criação e organização de scripts de migração do banco de dados
- **Decisão:** Padrão estruturado com nomenclatura timestamp, nomes em português, scripts idempotentes, sintaxe MySQL específica e sem regras de negócio

### ADR-008: Tipos de Dados Padrão no Banco MySQL
- **Número:** 008
- **Título:** Tipos de Dados Padrão no Banco MySQL
- **Data:** 2026-04-03
- **Responsável:** Backend
- **Contexto:** Necessidade de padronizar mapeamento entre tipos Java e tipos de colunas no MySQL para garantir consistência, performance e manutenibilidade
- **Decisão:** Adoção de tabela de mapeamento padrão com tipos específicos para cada categoria de dado, regras obrigatórias e exemplo de entidade

### ADR-009: Estratégia de Testes - Unitários, Integração e E2E
- **Número:** 009
- **Título:** Estratégia de Testes - Unitários, Integração e E2E
- **Data:** 2026-04-04
- **Responsável:** Arquiteto do Projeto
- **Contexto:** Necessidade de definir estratégia clara de testes para projeto com Clean Architecture e múltiplos bounded contexts
- **Decisão:** Adoção de estratégia em três camadas (unitários, integração, E2E) com estrutura de diretórios específica e Test Pyramid

### ADR-010: Padronização de Exceções no Domínio e Respostas de Erro da API
- **Número:** 010
- **Título:** Padronização de Exceções no Domínio e Respostas de Erro da API
- **Data:** 2026-04-04
- **Responsável:** Arquiteto do Projeto
- **Contexto:** Necessidade de padronizar exceções no domínio com linguagem ubíqua e respostas de erro consistentes na API
- **Decisão:** Criação de hierarquia DomainException e ErrorResponse padronizado com ControllerAdvice global

### ADR-011: Padronização de Respostas de Erro da API
- **Número:** 011
- **Título:** Padronização de Respostas de Erro da API
- **Data:** 2026-04-04
- **Responsável:** Arquiteto do Projeto
- **Contexto:** Necessidade de retornar mensagens de erro consistentes, amigáveis e úteis para os clientes da API
- **Decisão:** Criação de ErrorResponse padronizado na camada web com tratamento global via ControllerAdvice

### ADR-012: Requisito de Cobertura de Testes 80%
- **Número:** 012
- **Título:** Requisito de Cobertura de Testes 80%
- **Data:** 2026-04-07
- **Responsável:** Arquiteto do Projeto
- **Contexto:** Requisito obrigatório do Tech Challenge estabelecendo cobertura mínima de 80% para garantir qualidade e confiabilidade do sistema
- **Decisão:** Implementação de política de cobertura obrigatória com JaCoCo, build falha se mínimos não atingidos (80% geral, 90% domain, 85% application, 80% web/infrastructure)

### ADR-013: Implementação de SonarQube para Análise Estática de Código
- **Número:** 013
- **Título:** Implementação de SonarQube para Análise Estática de Código
- **Data:** 2026-04-11
- **Responsável:** Arquiteto do Projeto
- **Contexto:** Necessidade de ferramenta sistemática para análise estática identificando code smells, vulnerabilidades, bugs e garantindo qualidade contínua do código
- **Decisão:** Implementação do SonarQube com integração Maven

### ADR-014: Implementação de Swagger para Documentação de APIs
- **Número:** 014
- **Título:** Implementação de Swagger para Documentação de APIs
- **Data:** 2026-04-17
- **Responsável:** Arquiteto do Projeto
- **Contexto:** Ausência de documentação formal e padronizada das APIs REST, dificultando integração entre bounded contexts e onboarding de novos desenvolvedores
- **Decisão:** Implementação do Swagger (OpenAPI Specification 3.0) com SpringDoc OpenAPI para documentação automática e interativa das APIs

### ADR-015: Observabilidade com New Relic APM e Synthetic Monitor
- **Número:** 015
- **Título:** Observabilidade com New Relic APM e Synthetic Monitor
- **Data:** 2026-04-20
- **Responsável:** Arquiteto do Projeto
- **Contexto:** Necessidade de instrumentação de APM, traces distribuídos e health check externo para a aplicação rodando no Amazon EKS
- **Decisão:** Adoção do New Relic Java Agent 9.4.0 injetado via initContainer, Synthetic Monitor HTTP e Dashboard provisionados por Terraform

### ADR-016: Análise de Segurança de Dependências com OWASP Dependency Check
- **Número:** 016
- **Título:** Análise de Segurança de Dependências com OWASP Dependency Check
- **Data:** 2026-04-22
- **Responsável:** Arquiteto do Projeto
- **Contexto:** Requisito obrigatório do Tech Challenge de relatório de análise de vulnerabilidades das dependências do projeto
- **Decisão:** Integração do plugin OWASP Dependency Check ao Maven com threshold CVSS ≥ 7.0, supressões documentadas e geração de relatórios em HTML/XML/JSON

### ADR-017: Orquestração de Containers com Kubernetes (EKS) e Helm
- **Número:** 017
- **Título:** Orquestração de Containers com Kubernetes (EKS) e Helm
- **Data:** 2026-04-25
- **Responsável:** Arquiteto do Projeto
- **Contexto:** Requisito obrigatório do Tech Challenge Fase 2 de deploy em Kubernetes com manifestos de Deployment, Service, ConfigMap, Secret e HPA
- **Decisão:** Amazon EKS v1.36 com Helm Charts próprios, NodePort + NLB + API Gateway para exposição externa, HPA por CPU/Memória, New Relic via initContainer

### ADR-018: Pipeline de CI/CD com GitHub Actions
- **Número:** 018
- **Título:** Pipeline de CI/CD com GitHub Actions
- **Data:** 2026-04-28
- **Responsável:** Arquiteto do Projeto
- **Contexto:** Requisito obrigatório do Tech Challenge Fase 2 de pipeline de CI/CD com build, testes, build de imagem Docker e deploy no Kubernetes
- **Decisão:** GitHub Actions com três jobs encadeados (Build & Test → Push ECR → Deploy Helm/EKS), senha do RDS recuperada do Secrets Manager, rollback automático via Helm

### ADR-019: Uso do Lombok para Redução de Código Boilerplate
- **Número:** 019
- **Título:** Uso do Lombok para Redução de Código Boilerplate
- **Data:** 2026-05-02
- **Responsável:** Arquiteto do Projeto
- **Contexto:** Grande volume de código boilerplate (getters, setters, construtores, loggers) em projeto com múltiplos bounded contexts e muitas classes de domínio, DTOs e Use Cases
- **Decisão:** Adoção do Lombok 1.18.44 com restrições de uso definidas (proibido `@Data` em entidades JPA e Value Objects; uso de Records Java onde adequado)

### ADR-020: Máquina de Estados da Ordem de Serviço
- **Número:** 020
- **Título:** Máquina de Estados da Ordem de Serviço (OS)
- **Data:** 2026-05-05
- **Responsável:** Arquiteto do Projeto
- **Contexto:** A OS possui seis status com transições complexas, pré-condições e ações associadas (débito de estoque, notificações ao cliente) que precisam ser formalizadas e protegidas por invariantes do domínio
- **Decisão:** Máquina de estados implementada no aggregate `OrdemServico` com métodos de negócio encapsulando validações, Domain Events para notificações e exceções específicas do domínio para transições inválidas

---

## Nota sobre sobreposição: ADR-010 e ADR-011

Os ADRs 010 e 011 possuem sobreposição intencional: o ADR-010 documenta a hierarquia de exceções de domínio (`DomainException`) em conjunto com o `ErrorResponse`, enquanto o ADR-011 detalha exclusivamente o padrão `ErrorResponse` e o `GlobalExceptionHandler`. Ambos foram mantidos por rastreabilidade histórica — o ADR-010 representa a decisão original que incluía os dois temas, e o ADR-011 foi criado posteriormente para detalhar o aspecto de respostas de erro de forma isolada.

---

*Os ADRs detalhados estão disponíveis na pasta `ADR/` com arquivos individuais para cada decisão.*
