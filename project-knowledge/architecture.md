# Arquitetura do Projeto (Clean Architecture & DDD)

Este documento detalha a estrutura de arquitetura adotada no **Sistema de Gestão de Oficina**, fundamentada nos princípios de **Clean Architecture** (Arquitetura Limpa) e **Domain-Driven Design (DDD)**.

---

## 🏗️ Princípios Gerais

A arquitetura do sistema foi projetada com foco na separação de preocupações, testabilidade e independência de frameworks externos ou banco de dados.

### Direção das Dependências

As dependências de código apontam estritamente para o centro (para o Domínio). As camadas externas dependem das internas, mas as internas desconhecem totalmente as externas.

```
Web Layer ──> Application Layer ──> Domain Layer <── Infrastructure Layer
```

- **Domain Layer (Domínio):** O núcleo da aplicação. Não possui dependências de nenhuma outra camada ou de bibliotecas externas (exceto anotações essenciais).
- **Application Layer (Aplicação):** Contém os casos de uso que orquestram as regras de negócio de domínio. Depende apenas do Domínio.
- **Infrastructure Layer (Infraestrutura):** Contém detalhes técnicos como acesso a banco de dados (JPA/Hibernate), configuração de mensageria, etc. Implementa as portas (interfaces) definidas no Domínio e Aplicação.
- **Web Layer (REST API):** Expeõe os endpoints HTTP e DTOs, comunicando-se diretamente com a Camada de Aplicação.

---

## 📂 Estrutura de Pacotes (Package Tree)

O projeto está organizado em **Bounded Contexts** (Contextos Delimitados), garantindo limites claros de responsabilidades para cada área de negócio:

```
com.techchallenge.oficina/
├── administrativo/          # Bounded Context Administrativo
│   ├── application/         # Camada de Aplicação (Use Cases)
│   │   └── usecases/        # Casos de uso específicos
│   ├── domain/              # Camada de Domínio (Lógica pura de negócio)
│   │   ├── events/          # Eventos de Domínio
│   │   ├── exceptions/      # Exceções de Domínio
│   │   ├── model/           # Modelos de Domínio (Entidades/Value Objects)
│   │   ├── repositories/    # Interfaces de Repositórios (Portas de Saída)
│   │   └── services/        # Serviços de Domínio
│   ├── infrastructure/      # Camada de Infraestrutura (JPA, Adaptadores)
│   └── web/                 # Camada Web (REST Controllers, DTOs, Mappers)
│       ├── controllers/     # Controladores REST
│       ├── dto/             # Data Transfer Objects (Requisições/Respostas)
│       └── mappers/         # Conversores entre DTOs e Domínio
├── os/                      # Bounded Context de Ordem de Serviço
│   └── web/
│       └── controllers/
├── sharedkernel/            # Kernel Compartilhado (Módulos reutilizáveis)
│   ├── application/ports/   # Ports de Aplicação Compartilhados
│   ├── common/              # Classes Comuns e Utilitários
│   ├── domain/              # Domínios Compartilhados
│   ├── infrastructure/
│   │   └── security/        # Filtros de Segurança JWT Compartilhados
│   └── web/                 # Componentes Web Compartilhados
└── infrastructure/config/   # Configurações Globais do Spring Boot
    ├── security/            # SecurityConfig
    └── swagger/             # SwaggerConfig
```

---

## 🎯 Camadas da Arquitetura

### 1. Domain Layer (Domínio)
É onde residem as regras de negócio essenciais e os modelos de dados.
- **Aggregates & Entities:** Objetos com identidade própria que agrupam comportamentos e mantêm consistência transacional (ex: `OrdemServico`, `Cliente`).
- **Value Objects:** Objetos sem identidade própria definidos apenas por seus atributos (ex: `CPF`, `Placa`, `Email`).
- **Domain Services:** Lógicas de negócio que envolvem mais de um Aggregate ou que não se encaixam perfeitamente em uma única Entidade.
- **Domain Events:** Eventos que sinalizam mudanças de estado importantes no negócio.
- **Repositories (Interfaces):** Definição dos contratos de persistência (Portas) que a infraestrutura deve implementar.

### 2. Application Layer (Aplicação)
Responsável por coordenar a execução das regras de negócio.
- **Use Cases:** Classes que executam fluxos de negócio específicos (ex: `CriarOrdemServicoUseCase`, `ValidarOrcamentoUseCase`).
- **Command / Query:** Estruturas de dados que representam a intenção de alteração (Command) ou leitura (Query).
- **Application Ports:** Interfaces para serviços de infraestrutura geral (ex: envio de e-mails, gateways de integração).

### 3. Infrastructure Layer (Infraestrutura)
Implementa os detalhes técnicos e as integrações externas necessários para a execução do sistema.
- **Repositories (Implementações):** Classes JPA/Hibernate que implementam as interfaces do domínio e interagem com o banco de dados.
- **Gateways:** Adaptadores para APIs externas (ex: gateways de pagamento, envio de notificações).
- **Database Migrations:** Scripts do Flyway para controle de versão do banco de dados MySQL/PostgreSQL.

### 4. Web Layer (Apresentação REST)
A porta de entrada HTTP para o sistema.
- **Controllers:** Controllers REST do Spring Boot que interceptam as requisições HTTP, executam validações simples e invocam os Use Cases.
- **DTOs:** Objetos simples que expõem os dados da API de forma controlada, sem expor as entidades de domínio diretamente.
- **Mappers:** Classes que convertem DTOs em objetos de comando da aplicação e vice-versa.

---

## 🧩 Bounded Contexts (Contextos Delimitados)

- **Administrativo:** Responsável por toda a gestão cadastral básica do sistema (CRUD de Clientes, CRUD de Veículos, CRUD de Serviços, CRUD de Peças e Insumos com controle de estoque).
- **Ordem de Serviço (OS):** Core business da aplicação. Controla o fluxo de trabalho desde a recepção do veículo, diagnóstico do mecânico, envio do orçamento para o cliente, aprovação de reparos, execução e entrega final.
- **Shared Kernel:** Contém utilitários, exceções genéricas, filtros de segurança JWT e estruturas de autenticação que precisam ser compartilhados entre todos os outros contextos.

---

## 📚 Decisões Arquiteturais Registradas (ADRs)

Para compreender o histórico de decisões e escolhas técnicas da equipe, consulte o arquivo detalhado de **Architectural Decision Records (ADRs)**:

- [Guia de ADRs (Architecture Decision Records)](adr.md)
  - **ADR-001:** Adoção de Clean Architecture com Bounded Contexts.
  - **ADR-012:** Implementação do JaCoCo para cobertura de testes automatizados.
  - **ADR-014:** Adoção do Swagger/SpringDoc para documentação interativa de API.
