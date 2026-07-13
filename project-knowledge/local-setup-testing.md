# Guia de Configuração Local e Qualidade de Software

Este guia descreve as formas de configurar, compilar e executar o projeto localmente, com foco no cenário recomendado com Docker Compose, além das instruções de execução dos testes automatizados, análise de cobertura com JaCoCo, qualidade de código com SonarQube e análise de vulnerabilidades com OWASP Dependency Check.

---

## 🚀 Como Executar o Projeto Localmente com Docker Compose

O cenário recomendado para execução local do projeto é usar o arquivo [docker-compose.yml](../docker-compose.yml), que orquestra a API Spring Boot, o banco MySQL e os serviços auxiliares, como SonarQube.

### Pré-requisitos

- **Docker** instalado e em execução.
- **Docker Compose** habilitado no ambiente.
- Opcionalmente, **Git** para clonar o repositório e manter a estrutura do projeto.

### 1. Subir a aplicação e os serviços dependentes

A partir da raiz do repositório, execute:

```bash
docker compose up --build -d
```

Se o ambiente usar a versão clássica do Compose, o comando equivalente é:

```bash
docker-compose up --build -d
```

Esse comando irá construir a imagem da API e iniciar os containers do banco MySQL e dos serviços auxiliares definidos no arquivo de composição.

### 2. Verificar os containers ativos

Para confirmar que a aplicação e o banco foram iniciados corretamente:

```bash
docker compose ps
```

A API ficará disponível em: **http://localhost:8080**

### 3. Logs e troubleshooting

Se desejar acompanhar o comportamento da aplicação em tempo real:

```bash
docker compose logs -f app
```

Para encerrar todos os serviços e remover os containers criados:

```bash
docker compose down -v
```

> O ambiente do Docker Compose já configura automaticamente a conexão com o banco MySQL usando o serviço `mysql`, então não é necessário alterar o arquivo de configuração local para essa forma de execução.

---

## 💻 Como Executar o Projeto Localmente (Modo Nativo / Sem Docker Compose)

Caso prefira executar a aplicação Spring Boot e o banco de dados de forma nativa/local:

### Pré-requisitos

- **Java 21** instalado e configurado nas variáveis de ambiente.
- **MySQL 8.0+** instalado e em execução localmente.
- **Maven 3.6+** instalado (ou utilize o Maven Wrapper `./mvnw` incluso no projeto).

### 1. Configuração do Banco de Dados MySQL

Abra seu cliente SQL ou terminal de banco de dados e crie o schema:

```sql
CREATE DATABASE oficina CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Em seguida, configure as credenciais de acesso no arquivo [application.yaml](../oficina/src/main/resources/application.yaml):

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/oficina?useSSL=false&allowPublicKeyRetrieval=true
    username: seu_usuario
    password: sua_senha
```

### 2. Compilação e Inicialização

1. Navegue até o diretório interno do projeto da API:
   ```bash
   cd tech-challenge-soat-fiap/oficina
   ```

2. Compile e baixe as dependências com o Maven:
   ```bash
   ./mvnw clean install
   ```

3. Inicie a aplicação via Spring Boot plugin:
   ```bash
   ./mvnw spring-boot:run
   ```

   Ou execute diretamente o artefato `.jar` gerado na pasta target:
   ```bash
   java -jar target/oficina-0.0.1-SNAPSHOT.jar
   ```

A API estará disponível no endereço: **http://localhost:8080**

---

## 🧪 Como Executar Testes Automatizados

O projeto utiliza **JUnit 5**, **Mockito** e **Testcontainers** para testes unitários e de integração.

### Comandos de Teste

Para rodar todos os testes:
```bash
cd oficina
./mvnw test
```

Para rodar testes de cobertura com geração do relatório JaCoCo:
```bash
./mvnw clean test jacoco:report
```

Para rodar apenas um teste específico:
```bash
./mvnw test -Dtest=NomeDaClasseDeTeste
```

### 📊 Relatório de Cobertura JaCoCo

Após executar o comando com cobertura (`jacoco:report`), o relatório HTML estará disponível em:
- `oficina/target/site/jacoco/index.html`

#### Metas de Cobertura Definidas (Qualidade):

| Camada | Cobertura Mínima Exigida |
| :--- | :--- |
| **Geral do Projeto** | 80% |
| **Camada de Domínio (Domain)** | 90% |
| **Camada de Aplicação (Application)** | 85% |
| **Camada Web (REST Controllers)** | 80% |
| **Camada de Infraestrutura (Infrastructure)** | 80% |

---

## 🔍 Análise Estática de Código com SonarQube

O SonarQube analisa o código em busca de bugs, vulnerabilidades, code smells e duplicações.

### 1. Subir o SonarQube localmente

Você pode iniciar a instância local do SonarQube e o banco PostgreSQL correspondente usando o Docker Compose do projeto:

```bash
docker-compose up -d sonar sonar_db
```

Acesse o painel em: **http://localhost:9000**
- **Usuário padrão:** `admin`
- **Senha padrão:** `admin` *(o SonarQube exigirá a troca de senha no primeiro acesso)*

### 2. Rodar a Análise do Sonar

Gere um Token de Acesso (User Token) no painel do SonarQube e execute o comando Maven abaixo substituindo pelas suas variáveis:

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

---

## 🛡️ Verificação de Vulnerabilidades (OWASP Dependency Check)

O plugin do OWASP analisa as dependências do projeto contra bases de dados de vulnerabilidades conhecidas (CVEs).

### 1. Executar a Verificação

Execute o comando a partir do diretório `/oficina`:
```bash
cd oficina
./mvnw org.owasp:dependency-check-maven:check
```

### 2. Relatórios Gerados

Após a conclusão da análise, os relatórios são salvos em:
- `oficina/target/dependency-check-report.html` (Formato HTML visual)
- `oficina/target/dependency-check-report.json` (Formato JSON para integrações)

### ⚙️ Configurações Importantes

- **Skip Check:** Por padrão, a propriedade `<owasp.skip>` está configurada como `true` no `pom.xml`. Para ativá-lo no fluxo padrão de build, mude para `false` no seu arquivo `pom.xml`.
