# Tech Challenge SOAT FIAP - Sistema de Gestão de Oficina

Este projeto consiste no MVP do back-end de um Sistema Integrado de Atendimento e Execução de Serviços para uma oficina mecânica de médio porte. A solução visa eliminar a desorganização de processos manuais (anotações e planilhas) no fluxo de atendimento, diagnóstico, orçamentos, estoque e execução de reparos.

---

## 🚀 Tecnologias Principais

- **Java 21** & **Spring Boot 3.x**
- **Spring Security** (Autenticação JWT stateless)
- **Spring Data JPA** & **Flyway** (Migrations de banco)
- **MySQL 8.0** & **H2** (Banco em memória para testes)
- **Docker** & **Docker Compose**
- **JUnit 5**, **Mockito**, **Testcontainers** & **JaCoCo**
- **SonarQube** & **OWASP Dependency Check**
- **Terraform** & **Helm** (Deploy em nuvem AWS EKS)

---

## 🐳 Como Subir o Projeto via Docker Compose (Rápido)

O projeto inclui um arquivo `docker-compose.yml` que orquestra a aplicação e todos os serviços de apoio necessários localmente:

1. **Clone o repositório:**
   ```bash
   git clone <repo-url>
   cd tech-challenge-soat-fiap
   ```

2. **Inicie os serviços:**
   ```bash
   docker-compose up -d
   ```

3. **Serviços locais disponíveis:**
   - **Aplicação (API):** [http://localhost:8080](http://localhost:8080)
   - **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
   - **SonarQube:** [http://localhost:9000](http://localhost:9000) (admin / admin)
   - **MySQL:** `localhost:3306`

4. **Para parar os serviços:**
   ```bash
   docker-compose down
   ```

---

## 📚 Mapa da Documentação (Base de Conhecimento)

A documentação detalhada foi descentralizada para facilitar a manutenção e leitura. Explore os tópicos abaixo:

### 📝 Requisitos & Negócio
- [Requisitos da Fase 1](project-knowledge/tech-challenge.md) - Escopo inicial do MVP, regras obrigatórias e entregas.
- [Requisitos da Fase 2](project-knowledge/tech-challange-parte-2.md) - Escopo de evolução (K8s, IaC, CI/CD) e novas APIs requeridas.
- [Dicionário de Linguagem Ubíqua](documentos/Dicionario-linguagem-ubiqua.pdf) - Terminologia de negócio acordada (DDD).
- [Domain Storytelling Diagram](documentos/DomainStorytelling.svg) - Diagrama visual da jornada do usuário e interações.

### 🏗️ Arquitetura & Código
- [Arquitetura do Projeto](project-knowledge/architecture.md) - Clean Architecture, estrutura de pacotes e Bounded Contexts.
- [Diagrama de Arquitetura](documentos/diagrama-arquitetura.svg) - Visão estrutural da solução e seus componentes.
- [Diagrama de Infraestrutura](documentos/diagrama-infra.svg) - Visão da infraestrutura, serviços e deploy da aplicação.
- [Decisões Arquiteturais (ADRs)](project-knowledge/adr.md) - Histórico de escolhas tecnológicas fundamentadas.

### 🛡️ Segurança & APIs
- [Segurança e Autenticação JWT](project-knowledge/api-authentication.md) - Login de Admin, uso de Bearer Tokens e divisão de endpoints públicos/protegidos.
- [Relatório de Vulnerabilidades Sonar (PDF)](documentos/Analise-vulnerabilidade-Sonar.pdf) - Relatório oficial extraído do scanner estático.
- [Relatório de Dependências OWASP (PDF)](documentos/Dependency-Check-Report.pdf) - Análise de dependências e bibliotecas vulneráveis.

### ⚙️ Execução Local, Qualidade & DevOps
- [Configuração Local & Testes](project-knowledge/local-setup-testing.md) - Como rodar sem Docker, executar testes unitários/integração, analisar cobertura (JaCoCo) e rodar scans (SonarQube/OWASP).
- [Fluxo de CI/CD (GitHub Actions & AWS EKS)](project-knowledge/ci-cd.md) - Detalhamento da pipeline automática, variáveis de ambiente e deploy Helm no EKS.
- [Provisionamento com Terraform](project-knowledge/terraform-provisioning.md) - Como provisionar a infraestrutura AWS (VPC, EKS, RDS) utilizando Terraform.
- [Deploy em Kubernetes](project-knowledge/kubernetes-deployment.md) - Como realizar o deploy da aplicação no cluster Kubernetes utilizando Helm charts.