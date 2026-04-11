# ADR-007: Estrutura de Migrations Flyway

**Número do ADR:** 007  
**Título:** Estrutura de Migrations Flyway  
**Data:** 2026-04-03  
**Responsável:** Arquiteto do Projeto  
**Status:** Aceito

## Contexto
Com a adoção do MySQL como banco de dados e Flyway para gerenciamento de migrations (ADR-003), tornou-se necessário estabelecer padrões claros para criação e organização dos scripts de migração, garantindo consistência, rastreabilidade e segurança nas evoluções do schema do banco de dados.

## Decisão
Foi definido um padrão estruturado para criação de migrations Flyway com as seguintes regras:

1. **Nomenclatura:** Formato `V<YYYY_MM_DD_HHMMSS>__<Descrição>.sql`
2. **Idioma:** Nomes em português descritivos da ação
3. **Diretório:** `src/main/resources/db/migration/`
4. **Idempotência:** Scripts devem ser executáveis múltiplas vezes sem erro
5. **Sintaxe:** Utilizar sintaxe específica do MySQL 8.0
6. **Sem Regras de Negócio:** **NÃO implementar regras de negócio no banco de dados** (CHECK constraints, triggers complexos, stored procedures para validação)

### 6.1 - Princípio: Responsabilidade Única
- **Banco de Dados:** Responsável apenas por persistência e integridade referencial
- **Aplicação:** Responsável por todas as regras de negócio e validações

### 6.2 - O que EVITAR nas migrations:
- **CHECK constraints** para validar CPF/CNPJ
- **Triggers** para validar formatos de dados
- **Stored procedures** para lógica de negócio
- **Constraints complexas** que duplicam validações da aplicação

### 6.3 - O que INCLUIR nas migrations:
- **NOT NULL** para campos obrigatórios (apenas estrutura)
- **UNIQUE constraints** para identificação única (performance e integridade básica)
- **FOREIGN KEY constraints** para integridade referencial
- **Índices** para otimização de consultas
- **Tipos de dados** apropriados para os campos

## Justificativa
A decisão baseia-se nos seguintes motivos:

- **Timestamp Único:** `V<YYYY_MM_DD_HHMMSS>` garante ordem cronológica única e evita conflitos em equipes distribuídas
- **Clareza:** Nomes em português facilitam compreensão da equipe local
- **Organização:** Diretório padrão Spring Boot para migrations
- **Segurança:** Scripts idempotentes permitem reexecução segura em ambientes de desenvolvimento e testes
- **Compatibilidade:** Uso de sintaxe MySQL específica aproveita recursos otimizados do banco escolhido
- **Performance:** **Evitar regras de negócio no banco elimina overhead de validação em cada INSERT/UPDATE**
- **Manutenibilidade:** **Regras de negócio centralizadas na aplicação facilitam evolução e testes**
- **Flexibilidade:** **Permite alteração de regras sem necessidade de migrations complexas**

## Alternativas Consideradas

- **Nomenclatura Sequencial (V001, V002):** Mais simples, mas propenso a conflitos em equipes paralelas
- **Nomes em Inglês:** Padrão internacional, mas português melhora comunicação da equipe local
- **Implementar regras de negócio no banco:** **Rejeitada por impacto em performance e complexidade de manutenção**

## Consequências

**Benefícios:**
- Ordem cronológica garantida evitando conflitos de merge
- Clareza na identificação do propósito de cada migration
- Segurança na reexecução de scripts
- Aproveitamento de recursos específicos do MySQL
- Padrão reconhecível pela equipe
- **Melhor performance** ao evitar validações desnecessárias no banco
- **Manutenibilidade simplificada** com regras centralizadas na aplicação
- **Flexibilidade** para evoluir regras de negócio sem migrations

**Desafios:**
- Requer disciplina na criação de scripts idempotentes
- Necessidade de conhecimento específico de sintaxe MySQL
- Timestamps precisam ser gerenciados cuidadosamente em equipes distribuídas
- **Maior responsabilidade na camada de aplicação** para garantir validações completas

**Impacto no Desenvolvimento:**
- Scripts de migration mais robustos e seguros
- Melhor rastreabilidade das evoluções do schema
- Redução de conflitos em branches paralelas
- **Foco das migrations em estrutura e performance, não em validação de negócio**
- **Validações concentradas nos Value Objects e Use Cases da aplicação**
- **Testes mais simples e rápidos sem complexidade de regras no banco**

## Exemplo Prático

### ❌ INCORRETO (Com regras de negócio):
```sql
-- V2026_04_03_203000__criar_tabela_clientes.sql
CREATE TABLE clientes (
    id BINARY(16) PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(11) NOT NULL,
    email VARCHAR(100) NOT NULL,
    -- REGRA DE NEGÓCIO INCORRETA NO BANCO
    CONSTRAINT chk_cpf_formato CHECK (REGEXP_LIKE(cpf, '^[0-9]{11}$')),
    CONSTRAINT chk_email_formato CHECK (email LIKE '%@%.%')
);
```

### ✅ CORRETO (Apenas estrutura):
```sql
-- V2026_04_03_203000__criar_tabela_clientes.sql
CREATE TABLE clientes (
    id BINARY(16) PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(11) NOT NULL,
    email VARCHAR(100) NOT NULL,
    UNIQUE KEY uk_clientes_cpf (cpf),
    UNIQUE KEY uk_clientes_email (email)
);
```

## Referências
- Flyway Documentation: https://flywaydb.org/documentation/
- MySQL 8.0 Reference Manual: https://dev.mysql.com/doc/refman/8.0/en/
- Spring Boot Flyway Integration: https://spring.io/guides/gs/accessing-data-mysql/
