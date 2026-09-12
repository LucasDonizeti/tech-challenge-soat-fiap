# RFC-003 — Banco de Dados Relacional: Amazon RDS MySQL

| Campo | Valor |
|-------|-------|
| **ID** | RFC-003 |
| **Título** | Banco de Dados Relacional — Amazon RDS MySQL |
| **Status** | Aceito |
| **Data** | 2026-03-22 |
| **Autores** | Time de Arquitetura |
| **ADRs relacionados** | ADR-003 (MySQL), ADR-007 (Flyway), ADR-008 (Tipos de dados) |
| **RFCs relacionados** | RFC-001 (AWS), RFC-004 (EKS) |

---

## 1. Contexto

O sistema de gestão de oficina possui dados fortemente relacionais: clientes possuem veículos, ordens de serviço referenciam clientes e veículos, itens de serviço referenciam serviços e MROs (materiais, peças e insumos), e todo o fluxo de OS tem transições de status com regras de integridade estritas. Esse modelo de domínio favorece um banco de dados relacional com suporte robusto a transações ACID.

A segunda decisão — **gerenciado vs auto-gerenciado** — é igualmente importante: operar um banco de dados em contêineres dentro do EKS é tecnicamente possível, mas transfere para o time toda a responsabilidade de backup, alta disponibilidade, patching e recuperação de desastres, o que é inapropriado para o escopo deste projeto.

---

## 2. Problema

Precisamos definir:

1. **Tipo de banco:** relacional vs não-relacional
2. **Engine específica:** MySQL vs PostgreSQL vs outros
3. **Modelo de hospedagem:** gerenciado (RDS) vs self-hosted (Docker/EKS) vs serverless (Aurora)
4. **Configuração de rede e segurança:** subnet isolada, security groups, gerenciamento de senhas
5. **Estratégia de versionamento de schema:** migrações automatizadas ou DDL manual

---

## 3. Alternativas consideradas

### 3.1 Amazon RDS MySQL 8.0 ✅ Escolhida

**Prós:**
- MySQL 8.0 oferece suporte a `CHAR(36)` para UUIDs, `JSON`, window functions, CTEs e `utf8mb4` nativo
- RDS gerencia automaticamente: backups diários, snapshots, patching do SO, failover Multi-AZ
- `manage_master_user_password = true` delega o gerenciamento da senha ao AWS Secrets Manager, eliminando senhas em variáveis de ambiente ou código
- Integração direta com o security group do EKS — nenhuma porta exposta à internet
- Módulo `terraform-aws-modules/rds/aws` maduro e amplamente testado na comunidade
- Flyway 9+ tem suporte nativo a `flyway-mysql` com estratégia de migração específica para MySQL

**Contras:**
- MySQL tem algumas idiossincrasias históricas (charset padrão, comportamento de `NULL` em índices únicos)
- `db.t3.micro` é a menor instância — sem alta disponibilidade nativa (Multi-AZ desativado por custo)
- Custo de ~$15–25/mês para `db.t3.micro` com 20GB (aceitável com créditos Academy)

---

### 3.2 Amazon RDS PostgreSQL ✗ Descartada

**Prós:**
- PostgreSQL tem suporte nativo a `UUID` como tipo (`uuid`), arrays, JSONB e operadores avançados
- Considerado mais correto em conformidade com o padrão SQL
- Dialeto Hibernate `PostgreSQLDialect` suporta mais features nativas

**Por que foi descartado:**
- O Tech Challenge fase 1 especificou MySQL como banco a ser utilizado
- Flyway e JPA/Hibernate foram configurados desde o início para MySQL (`MySQLDialect`, `flyway-mysql`)
- Migração para PostgreSQL exigiria revisão de todas as migrations e mapeamentos JPA — custo sem benefício claro no escopo atual

---

### 3.3 Amazon Aurora Serverless v2 (MySQL-compatible) ✗ Descartada

**Prós:**
- Escalonamento automático de ACUs (Aurora Capacity Units) de 0.5 até 128
- Compatível com MySQL 8.0 — sem mudanças no código da aplicação
- Potencialmente mais barato em cargas irregulares (escala a zero quando idle)

**Por que foi descartado:**
- Aurora **não está disponível** na role `LabRole` do AWS Academy
- Mesmo fora do Academy, custo mínimo de Aurora é mais alto que `db.t3.micro` para uso esporádico
- A simplicidade de configuração do RDS MySQL supera os benefícios de escalonamento automático para o volume acadêmico

---

### 3.4 MySQL self-hosted no EKS (StatefulSet + PVC) ✗ Descartada

**Prós:**
- Custo zero de serviço gerenciado
- Total controle sobre versão e configuração

**Por que foi descartado:**
- **Sem backup automático** — perda de dados em caso de falha de node
- **Sem HA nativa** — StatefulSet com 1 réplica é ponto único de falha
- **Complexidade operacional desproporcionalmente alta** para o escopo acadêmico
- Gerenciamento de PersistentVolumeClaims no EKS exige compreensão profunda de StorageClass e CSI drivers
- Conflita com o objetivo de focar na arquitetura da aplicação, não na operação de infraestrutura

---

### 3.5 DynamoDB (NoSQL) ✗ Descartada

**Prós:**
- Totalmente serverless e disponível no AWS Academy
- Escalabilidade horizontal automática
- Sem custo de provisionamento de instância

**Por que foi descartado:**
- O modelo de domínio do sistema é **fortemente relacional** — OrdemServico referencia Cliente, Veiculo, ItemServico, ItemMRO com integridade referencial
- DynamoDB exigiria desnormalização agressiva e duplicação de dados, aumentando complexidade de consistência
- Spring Data JPA/Hibernate não funciona com DynamoDB — seria necessário migrar para DynamoDB Enhanced Client ou Spring Data DynamoDB (biblioteca de terceiros com menor suporte)
- Operações transacionais do DynamoDB têm limitações e custo adicional

---

## 4. Decisão

**Adotar Amazon RDS MySQL 8.0 (`db.t3.micro`, 20 GB, single-AZ)** como banco de dados primário, hospedado em database subnets privadas isoladas, com senha gerenciada pelo AWS Secrets Manager.

---

## 5. Configuração adotada

```hcl
# Terraform — módulo terraform-aws-modules/rds/aws
engine               = "mysql"
engine_version       = "8.0"
instance_class       = "db.t3.micro"
allocated_storage    = 20
db_name              = "oficina"
port                 = 3306
manage_master_user_password = true   # senha no Secrets Manager
multi_az             = false
backup_retention_period = 1
backup_window        = "03:00-06:00"
maintenance_window   = "Mon:00:00-Mon:03:00"
deletion_protection  = false         # ambiente acadêmico
family               = "mysql8.0"
parameters = [
  { name = "character_set_client", value = "utf8mb4" },
  { name = "character_set_server", value = "utf8mb4" }
]
```

---

## 6. Topologia de rede e segurança

```
VPC 10.0.0.0/16
│
├── Private Subnets (EKS nodes + Lambda)
│   └── Security Group: sg-eks-nodes
│
└── Database Subnets (10.0.201.0/24, 10.0.202.0/24)
    └── RDS MySQL (oficina-rds)
        └── Security Group: sg-rds
            └── Ingress: 3306/TCP from VPC CIDR (10.0.0.0/16) ONLY
            └── Egress:  all traffic
```

**Acessos ao banco:**
| Componente | Como acessa | Credenciais |
|------------|-------------|-------------|
| `oficina-api` (EKS) | Spring DataSource via JDBC | Username: `DB_USERNAME` (GitHub Secret) / Password: Secrets Manager |
| `auth-lambda` | JDBC direto (sem pool) | Env vars injetadas no deploy da Lambda via Terraform |
| Desenvolvimento local | Docker Compose (MySQL local container) | Credenciais fixas no `docker-compose.yml` |

---

## 7. Estratégia de versionamento de schema

O schema é versionado exclusivamente via **Flyway**, com as seguintes regras (detalhadas no ADR-007):

- Scripts nomeados com timestamp: `V{timestamp}__{descricao_em_snake_case}.sql`
- Scripts idempotentes onde possível (`CREATE TABLE IF NOT EXISTS`, `ADD COLUMN IF NOT EXISTS`)
- Sem regras de negócio em migrations — apenas DDL
- Charset explícito `utf8mb4` em todas as tabelas e colunas de texto
- Flyway executa automaticamente no startup da aplicação via `spring-boot-starter-flyway`

**Exemplo de migration:**
```sql
-- V20260328120000__criar_tabela_clientes.sql
CREATE TABLE IF NOT EXISTS clientes (
    id         CHAR(36)     NOT NULL,
    nome       VARCHAR(150) NOT NULL,
    cpf        CHAR(11)              UNIQUE,
    cnpj       CHAR(14)              UNIQUE,
    email      VARCHAR(150) NOT NULL UNIQUE,
    created_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 8. Acesso da Lambda ao RDS

A Lambda Authorizer conecta diretamente ao RDS via JDBC sem pool de conexões gerenciado. Isso é aceitável para o perfil de uso atual (frequência baixa de logins), mas exige cuidado:

- A conexão é estabelecida a cada invocação — sem `HikariCP` ou equivalente
- Em paralelo, o `AutenticarUsuarioUseCase` consulta o banco apenas para usuários CPF/CNPJ; logins de admin não tocam o banco
- O security group do RDS permite acesso de todo o CIDR da VPC, o que inclui os ENIs da Lambda nas private subnets

**Mitigação para alta carga futura:**
- Ativar **RDS Proxy** como camada de pool de conexões entre Lambda e RDS
- Configurar `max_connections` no parameter group do MySQL conforme instância

---

## 9. Recuperação de senha no pipeline

A pipeline GitHub Actions recupera a senha do RDS de forma segura, sem expô-la como variável de ambiente permanente:

```bash
# Na pipeline (job: deploy)
SECRET_ARN=$(aws rds describe-db-instances \
  --db-instance-identifier oficina-rds \
  --query 'DBInstances[0].MasterUserSecret.SecretArn' \
  --output text)

DB_PASSWORD=$(aws secretsmanager get-secret-value \
  --secret-id "$SECRET_ARN" \
  --query 'SecretString' \
  --output text | python3 -c "import sys,json; print(json.load(sys.stdin)['password'])")
```

A senha é mascarada nos logs com `::add-mask::` antes de ser passada ao Helm.

---

## 10. Consequências

**Positivas:**
- Zero preocupação com backup, patching ou failover — responsabilidade da AWS
- Senha nunca exposta em código ou repositório
- Schema sempre sincronizado automaticamente no startup da aplicação
- Charset `utf8mb4` garante suporte correto a caracteres especiais (acentuação, emojis)

**Negativas / Riscos:**
- Single-AZ: se o nó do RDS falhar, haverá downtime até o failover automático da AWS (~1–2min)
- `db.t3.micro` tem burst de créditos de CPU — em carga sustentada, performance degrada
- Sem Multi-AZ ativo, o backup diário causa pequena janela de I/O elevado (03:00–06:00 UTC)

---

## 11. Revisão futura

- Ativar **Multi-AZ** para eliminar ponto único de falha em ambiente de produção
- Avaliar **Read Replica** se consultas de leitura dominarem o workload
- Considerar **RDS Proxy** para gerenciar pool de conexões da Lambda em escala
- Avaliar upgrade para `db.t3.small` se burstable credits se esgotarem com frequência
