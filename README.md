# Tech Challenge SOAT FIAP - Sistema de Gestão de Oficina

Este projeto é o MVP do back-end de um Sistema Integrado de Atendimento e Execução de Serviços para uma oficina mecânica de médio porte. O sistema foi desenvolvido para resolver problemas de desorganização no processo de atendimento, diagnóstico, execução de serviços e entrega de veículos, que anteriormente eram feitos de forma manual usando anotações e planilhas.

A solução permite gestão eficiente de ordens de serviço, clientes, veículos, serviços e peças, com acompanhamento em tempo real do andamento dos serviços, autorização de reparos adicionais e gestão interna eficiente e segura.

## 🚀 Tecnologias Principais

- **Java 21** - Linguagem de programação
- **Spring Boot 4.0.3** - Framework principal
- **Spring Security** - Segurança e autenticação JWT
- **Spring Data JPA** - Persistência de dados
- **Flyway** - Migrations de banco de dados
- **MySQL 8.0** - Banco de dados relacional (produção)
- **H2** - Banco de dados em memória (testes)
- **SpringDoc OpenAPI 2.7.0** - Documentação de API (Swagger)
- **Maven** - Gerenciamento de dependências e build
- **Docker** - Containerização
- **JUnit 5** - Framework de testes
- **Mockito** - Mocking para testes
- **Testcontainers** - Testes com containers
- **JaCoCo** - Cobertura de código
- **SonarQube** - Análise estática de código
- **OWASP Dependency Check** - Verificação de vulnerabilidades

## 🐳 Como Subir o Projeto via Docker Compose

O projeto inclui um arquivo `docker-compose.yml` que orquestra todos os serviços necessários:

1. **Clone o repositório:**
```bash
git clone <repo-url>
cd tech-challenge-soat-fiap
```

2. **Suba os serviços com Docker Compose:**
```bash
docker-compose up -d
```

3. **Serviços disponíveis:**
- **Aplicação:** http://localhost:8080
- **MySQL:** localhost:3306
- **SonarQube:** http://localhost:9000
- **PostgreSQL (Sonar):** localhost:5432

4. **Parar os serviços:**
```bash
docker-compose down
```

5. **Parar os serviços e remover volumes:**
```bash
docker-compose down -v
```

## 💻 Como Executar o Projeto (Sem Docker Compose)

Se você já possui um banco de dados MySQL configurado:

### Pré-requisitos
- Java 21 instalado
- MySQL 8.0+ configurado
- Maven 3.6+ instalado

### Configuração do Banco de Dados

1. **Crie o banco de dados:**
```sql
CREATE DATABASE oficina CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. **Configure as credenciais no arquivo `oficina/src/main/resources/application.yaml`:**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/oficina?useSSL=false&allowPublicKeyRetrieval=true
    username: seu_usuario
    password: sua_senha
```

### Execução

1. **Navegue até o diretório do projeto:**
```bash
cd tech-challenge-soat-fiap/oficina
```

2. **Compile o projeto:**
```bash
./mvnw clean install
```

3. **Execute a aplicação:**
```bash
./mvnw spring-boot:run
```

Ou execute o JAR diretamente:
```bash
java -jar target/oficina-0.0.1-SNAPSHOT.jar
```

A aplicação estará disponível em: http://localhost:8080

## 🧪 Como Rodar Testes

### Executar todos os testes:
```bash
cd oficina
./mvnw test
```

### Executar testes com cobertura:
```bash
./mvnw clean test jacoco:report
```

### Executar testes específicos:
```bash
./mvnw test -Dtest=NomeDoTeste
```

### Ver relatório de cobertura:
Após executar os testes com JaCoCo, o relatório será gerado em:
- `oficina/target/site/jacoco/index.html`

**Metas de cobertura:**
- Cobertura geral: 80%
- Domain layer: 90%
- Application layer: 85%
- Web layer: 80%
- Infrastructure layer: 80%

## 📚 Como Acessar o Swagger

A documentação da API está disponível através do Swagger/OpenAPI:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

O Swagger inclui:
- Documentação interativa de todos os endpoints
- Exemplos de requisições e respostas
- Interface para testar os endpoints diretamente
- Configuração de autenticação JWT Bearer

## 🔐 Autenticação

### Como Funciona a Autenticação

O sistema utiliza **JWT (JSON Web Token)** para autenticação, implementado com Spring Security.

### Credenciais Padrão

- **Usuário:** `admin`
- **Senha:** `secret123`
- **Role:** `ADMIN`

### Como Autenticar

1. **Faça login para obter o token:**
```bash
POST http://localhost:8080/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "secret123"
}
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "admin"
}
```

2. **Use o token nas requisições subsequentes:**
```bash
GET http://localhost:8080/v1/admin/clientes
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Endpoints Públicos (Sem Autenticação)
- `/v1/auth/**` - Autenticação
- `/v1/admin/health` - Health check
- `/v1/os/**` - Endpoints de Ordem de Serviço
- `/actuator/**` - Actuator
- `/swagger-ui/**` - Swagger UI
- `/v3/api-docs/**` - OpenAPI docs

### Endpoints Protegidos (Requer Autenticação)
- `/v1/admin/**` - Endpoints administrativos (exceto health)

### Configuração JWT

- **Secret:** `chavesecreta` (configurável em `application.yaml`)
- **Expiration:** 86400 segundos (24 horas)
- **Algorithm:** HS256

## 🔍 SonarQube

### Como Subir o SonarQube

O SonarQube já está configurado no `docker-compose.yml`:

```bash
docker-compose up -d sonar sonar_db
```

Acesse: http://localhost:9000

**Credenciais padrão do SonarQube:**
- **Usuário:** `admin`
- **Senha:** `admin` (será solicitado a alterar no primeiro acesso)

### Como Executar Análise Sonar

Configure as variáveis de ambiente e execute:
```bash
export SONAR_TOKEN=seu_token_sonar
export SONAR_HOST_URL=http://localhost:9000

cd oficina
mvn clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
  -Dsonar.projectKey=oficina \
  -Dsonar.projectName='oficina' \
  -Dsonar.host.url=$SONAR_HOST_URL \
  -Dsonar.token=$SONAR_TOKEN
```

## 🛡️ OWASP Dependency Check

### Como Executar Verificação de Vulnerabilidades

O OWASP Dependency Check verifica vulnerabilidades nas dependências do projeto.

1. **Execute a verificação:**
```bash
cd oficina
./mvnw org.owasp:dependency-check-maven:check
```

2. **O relatório será gerado em:**
- `oficina/target/dependency-check-report.html` (formato visual)
- `oficina/target/dependency-check-report.xml` (formato XML)
- `oficina/target/dependency-check-report.json` (formato JSON)

### Configuração

- **Fail Build on CVSS:** 7 (build falha se encontrar vulnerabilidades com CVSS >= 7)
- **Skip:** true (configurado como true no `pom.xml`, altere para false para ativar)
- **Suppression file:** `owasp-suppressions.xml`

Para ativar a verificação no build, altere a propriedade no `pom.xml`:
```xml
<owasp.skip>false</owasp.skip>
```

## 🏗️ Arquitetura

O projeto segue os princípios de **Domain-Driven Design (DDD)** e **Clean Architecture**, organizado em bounded contexts:

### Estrutura de Packages

```
com.techchallenge.oficina/
├── administrativo/          # Bounded Context Administrativo
│   ├── application/         # Camada de Aplicação (Use Cases)
│   │   └── usecases/       # Casos de uso
│   ├── domain/             # Camada de Domínio
│   │   ├── events/         # Eventos de Domínio
│   │   ├── exceptions/     # Exceções de Domínio
│   │   ├── model/          # Modelos de Domínio
│   │   ├── repositories/   # Interfaces de Repositórios
│   │   └── services/       # Serviços de Domínio
│   ├── infrastructure/     # Camada de Infraestrutura
│   └── web/                # Camada Web
│       ├── controllers/    # Controllers REST
│       ├── dto/           # Data Transfer Objects
│       └── mappers/       # Mappers
├── os/                     # Bounded Context de Ordem de Serviço
│   └── web/
│       └── controllers/
├── sharedkernel/           # Kernel Compartilhado
│   ├── application/ports/ # Ports de Aplicação
│   ├── common/            # Classes Comuns
│   ├── domain/            # Domínios Compartilhados
│   ├── infrastructure/
│   │   └── security/      # JWT (compartilhado)
│   └── web/               # Web compartilhado
└── infrastructure/config/  # Configurações Globais
    ├── security/          # SecurityConfig
    └── swagger/           # SwaggerConfig
```

### Camadas da Arquitetura

1. **Domain Layer:** Contém a lógica de negócio, entidades, value objects e regras de domínio
2. **Application Layer:** Contém os use cases e orquestração de fluxos de negócio
3. **Infrastructure Layer:** Implementações técnicas (repositories, gateways, persistência)
4. **Web Layer:** Controllers, DTOs e mappers para expor a API REST

### Bounded Contexts

- **Administrativo:** Gestão de clientes e veículos
- **OS (Ordem de Serviço):** Gestão de ordens de serviço
- **Shared Kernel:** Componentes compartilhados entre contexts (autenticação, segurança)

## 📦 Dependências Principais

### Spring Boot Starters
- `spring-boot-starter-data-jpa` - Persistência com JPA
- `spring-boot-starter-flyway` - Migrations de banco
- `spring-boot-starter-security` - Segurança e autenticação
- `spring-boot-starter-validation` - Validação de dados
- `spring-boot-starter-web` - API REST
- `spring-boot-starter-test` - Testes
- `spring-boot-starter-logging` - Logging

### Banco de Dados
- `mysql-connector-j` - Driver MySQL
- `flyway-mysql` - Suporte Flyway para MySQL (migrations em `src/main/resources/db/migration/`)
- `h2` - Banco em memória para testes

### Segurança
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson` (0.11.5) - JWT tokens

### Documentação
- `springdoc-openapi-starter-webmvc-ui` (2.7.0) - Swagger/OpenAPI

### Testes
- `junit-jupiter` - JUnit 5
- `mockito-core`, `mockito-junit-jupiter` - Mocking
- `rest-assured` (5.4.0) - Testes de API REST
- `testcontainers` (1.19.7) - Containers para testes
- `spring-security-test` - Testes de segurança

### Qualidade
- `jacoco-maven-plugin` (0.8.12) - Cobertura de código
- `sonar-maven-plugin` (3.9.1.2184) - Análise SonarQube
- `dependency-check-maven` (12.2.0) - Verificação OWASP

### Outros
- `lombok` (1.18.44) - Redução de boilerplate

## ⚙️ Arquivos de Configuração

### Arquivos Principais

- **`pom.xml`** - Configuração Maven, dependências e plugins
- **`docker-compose.yml`** - Orquestração de serviços Docker
- **`Dockerfile`** - Build da imagem Docker da aplicação
- **`mvnw` / `mvnw.cmd`** - Maven Wrapper

### Configurações da Aplicação

- **`src/main/resources/application.yaml`** - Configurações principais
  - Porta do servidor (8080)
  - Configurações do Swagger/OpenAPI
  - Configurações do datasource MySQL
  - Configurações do JPA/Hibernate
  - Configurações do Flyway
  - Configurações de segurança/JWT
  - Configurações de logging

- **`src/test/resources/application-test.properties`** - Configurações de teste
  - DataSource H2 em memória
  - Configurações de segurança para testes
  - Logging em DEBUG

### Segurança

- **`owasp-suppressions.xml`** - Supressões de vulnerabilidades OWASP

### ADRs (Architecture Decision Records)

O projeto referencia os seguintes ADRs:
- **ADR-012:** Implementação de JaCoCo para Cobertura de Testes
- **ADR-014:** Implementação de Swagger para Documentação de APIs

##  Comandos Úteis

```bash
# Limpar e compilar
./mvnw clean install

# Executar aplicação
./mvnw spring-boot:run

# Executar testes
./mvnw test

# Executar testes com cobertura
./mvnw clean test jacoco:report

# Executar SonarQube
mvn clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
  -Dsonar.projectKey=oficina \
  -Dsonar.projectName='oficina' \
  -Dsonar.host.url=$SONAR_HOST_URL \
  -Dsonar.token=$SONAR_TOKEN

# Executar OWASP Dependency Check
./mvnw org.owasp:dependency-check-maven:check

# Build Docker
docker build -f oficina/Dockerfile -t oficina-app ./oficina

# Subir todos os serviços
docker-compose up -d

# Ver logs dos serviços
docker-compose logs -f

# Parar serviços
docker-compose down
```

## 📄 Licença

Este projeto está sob licença Apache 2.0.