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

---

*Os ADRs detalhados estão disponíveis na pasta `ADR/` com arquivos individuais para cada decisão.*
