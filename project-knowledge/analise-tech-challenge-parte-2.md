# Análise Tech Challenge Parte 2 - Requisitos Faltantes e Melhorias de Arquitetura

## Data da Análise
2026-07-09

---

## 1. Requisitos Faltantes do Tech Challenge Parte 2

### 1.1 Evolução da Aplicação

#### ✅ IMPLEMENTADO
- **Clean Code**: O código segue boas práticas com nomes claros, uso de Lombok, separação de responsabilidades
- **Clean Architecture**: Estrutura baseada em camadas (domain, application, infrastructure, web)
- **Testes automatizados**: Existem testes unitários e de integração usando JUnit 5, Mockito e Testcontainers
- **Abertura de Ordem de Serviço**: Endpoint `POST /v1/os` implementado em `OrdemServicoController`
- **Consulta de status da OS**: Endpoint `GET /v1/os/{id}` implementado
- **Aprovação de orçamento**: Endpoint `POST /api/cliente/os/{osId}/aprovar-orcamento` implementado em `ClienteOSController`

#### ❌ FALTANDO OU INCOMPLETO

**1. Listagem de ordens de serviço com ordenação específica**
- **Requisito**: Ordenação por status: `Em Execução` > `Aguardando Aprovação` > `Diagnóstico` > `Recebida`, mais antigas primeiro, excluindo (lógica não física) OS finalizadas e entregues
- **Status atual**: O endpoint `GET /v1/os` existe mas não implementa a ordenação específica requerida
- **Como implementar**:
  ```java
  // Em ListarOrdensServicoUseCase ou no repository
  // Adicionar ordenação customizada por status prioritário
  // Excluir OS com status FINALIZADA e ENTREGUE da listagem padrão
  // Ordenar por dataCriacao ASC (mais antigas primeiro)
  ```

**2. Atualização de status da OS via e-mail**
- **Requisito**: Atualização de status da OS via alguma ferramenta como e-mail
- **Status atual**: NÃO IMPLEMENTADO
- **Como implementar**:
  - Criar um endpoint para receber webhooks de e-mail (ex: SendGrid, Mailgun)
  - Implementar parser de e-mails para extrair comandos de atualização de status
  - Criar use case `ProcessarComandoEmailUseCase`
  - Configurar serviço de e-mail para escutar mensagens
  - Exemplo de implementação:
    ```java
    @PostMapping("/webhooks/email")
    public ResponseEntity<Void> processarEmail(@RequestBody EmailWebhookRequest request) {
        // Extrair comando do e-mail (ex: "APROVAR OS-123")
        // Executar use case correspondente
    }
    ```

### 1.2 Infraestrutura

#### ✅ IMPLEMENTADO
- **Conteinerização**: Dockerfile atualizado em `oficina/Dockerfile`
- **Docker Compose**: Arquivo `docker-compose.yml` na raiz do projeto
- **Kubernetes**: Manifestos Helm em `k8s/oficina/` com:
  - Deployment (`templates/deployment.yaml`)
  - Service (`templates/service.yaml`)
  - ConfigMap (`templates/configmap.yaml`)
  - Secret (`templates/secret.yaml`)
  - HPA (`templates/hpa.yaml`)
- **Terraform**: Scripts em `terraform/` para provisionamento de EKS, RDS, VPC
- **CI/CD**: Pipeline GitHub Actions em `.github/workflows/pipeline.yml`

#### ❌ FALTANDO OU INCOMPLETO

**Nenhum requisito de infraestrutura faltando**

### 1.3 Entregáveis

#### ✅ IMPLEMENTADO
- Código-fonte atualizado e refatorado
- Dockerfile e docker-compose revisados
- Manifestos Kubernetes em `/k8s`
- Scripts Terraform em `/terraform`
- Arquivos de configuração da pipeline CI/CD

#### ❌ FALTANDO OU INCOMPLETO

**1. README.md atualizado com desenho da arquitetura**
- **Status atual**: README.md existe mas não contém o desenho da arquitetura proposta para Fase 2
- **Como implementar**:
  - Adicionar diagrama de arquitetura mostrando:
    - Componentes da aplicação (API, Database, Kubernetes)
    - Infraestrutura provisionada (EKS, RDS, VPC, ECR)
    - Fluxo de deploy (CI/CD → ECR → EKS)
  - Pode usar Mermaid, PlantUML ou imagem

**2. Link para collection completa das APIs**
- **Status atual**: Swagger UI disponível em `/swagger-ui.html` mas não há link documentado no README
- **Como implementar**:
  - Adicionar no README.md: Link para Swagger UI ou collection Postman exportada
  - Exportar collection do Swagger: `curl http://localhost:8080/v3/api-docs -o api-collection.json`

**3. Vídeo demonstrativo (até 15 minutos)**
- **Status atual**: NÃO IMPLEMENTADO
- **Como implementar**:
  - Gravar vídeo demonstrando:
    - Deploy da aplicação (local ou Kubernetes)
    - Execução do CI/CD (GitHub Actions)
    - Consumo das APIs (Swagger UI ou Postman)
    - Escalabilidade automática (simular carga)
  - Publicar no YouTube ou Vimeo (público ou não listado)
  - Adicionar link no README.md

---

## 2. Análise de Clean Architecture - Comparação com Repositório de Referência

### 2.1 Repositório de Referência
**Fonte**: https://github.com/proferickmuller/soat-cleanarch-java/tree/main/Limpa

**Estrutura do repositório de referência**:
```
cobrancacore/src/main/java/br/dev/erm/
├── common/           # Classes comuns e utilitários
├── controllers/      # Controladores REST (camada de apresentação)
├── entities/        # Entidades de domínio
├── gateways/         # Adaptadores externos (banco, APIs)
├── presenters/       # Presenters/DTOs para resposta
└── usecases/         # Casos de uso (camada de aplicação)
```

**Características da arquitetura de referência**:
- Estrutura simples e direta
- Separação clara por responsabilidade
- Dependências apontam para dentro (usecases ← entities)
- Controllers dependem de usecases
- Gateways implementam interfaces definidas em usecases ou entities
- Presenters convertem entidades para DTOs

### 2.2 Arquitetura Atual do Projeto

**Estrutura atual**:
```
com.techchallenge.oficina/
├── administrativo/          # Bounded Context Administrativo
│   ├── application/         # Camada de Aplicação
│   │   └── usecases/
│   ├── domain/              # Camada de Domínio
│   │   ├── events/
│   │   ├── exceptions/
│   │   ├── model/
│   │   ├── repositories/
│   │   └── services/
│   ├── infrastructure/      # Camada de Infraestrutura
│   └── web/                 # Camada Web
├── os/                      # Bounded Context Ordem de Serviço
│   ├── application/
│   ├── domain/
│   ├── infrastructure/
│   └── web/
└── sharedkernel/            # Kernel Compartilhado
```

### 2.3 Diferenças e Oportunidades de Melhoria

#### 🔴 PROBLEMAS IDENTIFICADOS

**1. Complexidade excessiva de pacotes**
- **Problema**: Muitos níveis de aninhamento (ex: `administrativo/domain/model/aggregates`)
- **Referência**: Estrutura mais plana com poucos níveis
- **Impacto**: Dificulta navegação e compreensão do código
- **Solução**: Simplificar estrutura, reduzir níveis de aninhamento

**2. Mistura de responsabilidades no domínio**
- **Problema**: Domain contém `events`, `exceptions`, `services` além de `model` e `repositories`
- **Referência**: Entities são puras, sem subpacotes complexos
- **Impacto**: Domain não é tão "puro" quanto deveria ser
- **Solução**: Mover exceptions para pacote comum, simplificar domain

**3. Bounded Contexts podem ser excessivos**
- **Problema**: Separação em `administrativo` e `os` pode ser desnecessária para o tamanho atual
- **Referência**: Estrutura monolítica simples por camada
- **Impacto**: Adiciona complexidade sem benefício claro
- **Solução**: Considerar simplificar para estrutura por camada pura

**4. Ausência de camada "Presenters"**
- **Problema**: Conversão para DTOs acontece diretamente em web/mappers
- **Referência**: Camada `presenters` dedicada para conversão
- **Impacto**: Controllers ficam com responsabilidade de conversão
- **Solução**: Extrair lógica de conversão para presenters

**5. Infrastructure mistura implementações**
- **Problema**: Infrastructure contém `config`, `external`, `gateways`, `persistence`
- **Referência**: `gateways` é o único ponto de integração externa
- **Impacto**: Menos clareza sobre o que é adaptação externa
- **Solução**: Consolidar adaptadores externos em gateways

#### 🟢 PONTOS FORTES

**1. Separação clara de camadas**
- Domain, Application, Infrastructure e Web estão bem separados
- Dependências seguem direção correta (para o centro)

**2. Uso de Bounded Contexts**
- Alinhado com DDD, facilita evolução futura
- Separação de domínios de negócio (Administrativo vs OS)

**3. Testes automatizados**
- Cobertura de testes implementada
- Uso de Testcontainers para testes de integração

### 2.4 Recomendações para Melhorar o "Purismo" da Clean Architecture

#### PRIORIDADE ALTA

**1. Simplificar estrutura de pacotes**
```
ANTES:
com.techchallenge.oficina.administrativo.domain.model.aggregates
com.techchallenge.oficina.administrativo.domain.model.entities
com.techchallenge.oficina.administrativo.domain.model.valueobjects

DEPOIS:
com.techchallenge.oficina.domain.aggregates
com.techchallenge.oficina.domain.entities
com.techchallenge.oficina.domain.valueobjects
```

**2. Consolidar exceptions em pacote comum**
```
ANTES:
com.techchallenge.oficina.administrativo.domain.exceptions
com.techchallenge.oficina.os.domain.exceptions

DEPOIS:
com.techchallenge.oficina.common.exceptions
```

**3. Extrair Presenters da camada Web**
```
ANTES:
com.techchallenge.oficina.os.web.mappers.OrdemServicoWebMapper

DEPOIS:
com.techchallenge.oficina.presenters.OrdemServicoPresenter
```

#### PRIORIDADE MÉDIA

**4. Simplificar Infrastructure**
- Consolidar `external` e `gateways` em um único pacote `adapters`
- Mover `config` para nível superior ou eliminar se desnecessário

**5. Remover aninhamento excessivo em Domain**
- Mover `events` para nível de domain (não dentro de model)
- Simplificar `repositories` para interfaces diretas

#### PRIORIDADE BAIXA

**6. Considerar remover Bounded Contexts**
- Se o projeto não crescer significativamente, estrutura por camada pura pode ser suficiente
- Manter apenas se houver planos claros de evolução para microserviços

### 2.5 Exemplo de Estrutura Sugerida

```
com.techchallenge.oficina/
├── common/                   # Classes comuns
│   ├── exceptions/
│   ├── validators/
│   └── utils/
├── domain/                   # Camada de Domínio (pura)
│   ├── aggregates/
│   ├── entities/
│   ├── valueobjects/
│   ├── events/
│   ├── repositories/         # Interfaces apenas
│   └── services/
├── usecases/                 # Casos de uso
│   ├── commands/
│   ├── responses/
│   └── implementations/
├── adapters/                 # Adaptadores externos
│   ├── persistence/          # JPA/Hibernate
│   ├── email/                # Envio de e-mails
│   └── notification/         # Notificações
├── presenters/              # Conversão para DTOs
│   └── mappers/
├── controllers/              # Controladores REST
└── config/                   # Configurações Spring
```

---

## 3. Plano de Ação - Priorização

### FASE 1 - CRÍTICO (Para entrega do Tech Challenge Parte 2)

1. **Implementar ordenação específica na listagem de OS** (2-3 horas)
   - Modificar `ListarOrdensServicoUseCase`
   - Adicionar lógica de ordenação por status prioritário
   - Excluir OS finalizadas/entregues da listagem padrão

2. **Implementar atualização de status via e-mail** (4-6 horas)
   - Criar endpoint webhook para e-mail
   - Implementar parser de comandos de e-mail
   - Criar use case para processar comandos

3. **Atualizar README.md com arquitetura** (1-2 horas)
   - Adicionar diagrama de arquitetura
   - Documentar componentes e fluxo de deploy

4. **Adicionar link para collection de APIs** (30 minutos)
   - Exportar collection do Swagger
   - Adicionar link no README

5. **Gravar vídeo demonstrativo** (2-3 horas)
   - Demonstrar deploy, CI/CD, APIs e escalabilidade
   - Publicar e adicionar link no README

### FASE 2 - MELHORIAS DE ARQUITETURA (Pós-entrega)

1. **Simplificar estrutura de pacotes** (1-2 dias)
   - Reduzir níveis de aninhamento
   - Consolidar exceptions em common
   - Extrair presenters

2. **Refatorar Infrastructure** (1 dia)
   - Consolidar adapters
   - Simplificar config

3. **Avaliar necessidade de Bounded Contexts** (meio dia)
   - Decidir se mantém ou simplifica

---

## 4. Conclusão

O projeto está **bem avançado** em relação aos requisitos do Tech Challenge Parte 2, com infraestrutura completa (Docker, Kubernetes, Terraform, CI/CD) e arquitetura baseada em Clean Architecture.

**Principais gaps para entrega**:
- Ordenação específica na listagem de OS
- Atualização de status via e-mail
- Documentação de arquitetura no README
- Collection de APIs documentada
- Vídeo demonstrativo

**Oportunidades de melhoria de arquitetura**:
- Simplificar estrutura de pacotes para seguir mais de perto o padrão de referência
- Reduzir complexidade de aninhamento
- Extrair camada de presenters
- Consolidar adaptadores externos

Estas melhorias podem ser implementadas gradualmente após a entrega do Tech Challenge Parte 2.
