# ADR-008: Tipos de Dados Padrão no Banco MySQL

**Número do ADR:** 008  
**Título:** Tipos de Dados Padrão no Banco MySQL  
**Data:** 2026-04-03  
**Responsável:** Backend  
**Status:** Aceito

## Contexto
Este projeto utiliza **Java + Spring Boot + Spring Data JPA + Hibernate + MySQL**. Para garantir consistência, legibilidade, performance e facilidade de manutenção em todo o codebase, é necessário definir um padrão claro de mapeamento entre tipos Java e tipos de colunas no MySQL.

## Decisão
Adotar os seguintes tipos de dados padrão para todas as entidades JPA do projeto:

### Tipos de Dados Recomendados

| Tipo de Dado                          | Tipo Java                          | Tipo MySQL recomendado          | Anotações JPA recomendadas                                                                 | Observações |
|---------------------------------------|------------------------------------|----------------------------------|---------------------------------------------------------------------------------------------|-------------|
| **ID sequencial**                    | `Long`                             | `BIGINT` (ou `BIGINT UNSIGNED`) | `@Id`<br>`@GeneratedValue(strategy = GenerationType.IDENTITY)`                            | Padrão principal para a maioria das tabelas |
| **ID UUID**                          | `UUID`                             | `BINARY(16)`                    | `@Id`<br>`@Column(columnDefinition = "BINARY(16)")`                                        | Alternativa para sistemas distribuídos |
| **Strings curtas** (nome, título...) | `String`                           | `VARCHAR(255)`                  | `@Column(length = 255, nullable = false)`                                                  | Valor padrão recomendado |
| **CPF, CNPJ e RG**                   | `String`                           | `VARCHAR(14)` (CNPJ) / `VARCHAR(11)` (CPF) / `VARCHAR(20)` (RG) | `@Column(length = XX, nullable = false, unique = true)` quando aplicável | **Sempre armazenar sem máscara** (apenas números ou letras) |
| **Placa de veículo (Mercosul)**      | `String`                           | `VARCHAR(8)`                    | `@Column(length = 8, nullable = false, unique = true)`                                     | Formato: ABC1D23 |
| **Email**                            | `String`                           | `VARCHAR(255)`                  | `@Column(length = 255, nullable = false, unique = true)`                                   | Suficiente para a maioria dos casos |
| **Telefone / Celular**               | `String`                           | `VARCHAR(20)`                   | `@Column(length = 20)`                                                                     | Armazenar sem máscara |
| **Data (sem hora)**                  | `LocalDate`                        | `DATE`                          | `@Column`                                                                                  | Mapeamento nativo |
| **Data e hora**                      | `LocalDateTime`                    | `DATETIME(6)`                   | `@Column(columnDefinition = "DATETIME(6)")`                                                | Recomendado no MySQL 8+ |
| **Data com timezone**                | `Instant`                          | `TIMESTAMP(6)`                   | `@Column(columnDefinition = "TIMESTAMP(6)")`                                                | Quando for necessário armazenar timezone |
| **Enum**                             | Enum                               | `VARCHAR(30)` ou `VARCHAR(50)`  | `@Enumerated(EnumType.STRING)`<br>`@Column(length = 30)`                                    | **Sempre usar STRING** (nunca ORDINAL) |
| **Booleano**                         | `Boolean` / `boolean`              | `TINYINT(1)`                    | Mapeamento automático do Hibernate                                                         | Evitar `BIT(1)` |
| **Valor monetário**                  | `BigDecimal`                       | `DECIMAL(19,4)` ou `DECIMAL(19,2)` | `@Column(precision = 19, scale = 4)`                                                      | Nunca usar `DOUBLE` ou `FLOAT` para dinheiro |
| **Texto longo**                      | `String`                           | `TEXT` ou `LONGTEXT`            | `@Column(columnDefinition = "TEXT")` ou `@Lob`                                             | Usar `TEXT` por padrão |
| **JSON**                             | `String` ou classe específica      | `JSON`                          | `@Column(columnDefinition = "JSON")`                                                       | Suportado nativamente no MySQL 5.7+ |

### Regras Gerais Obrigatórias

- **Documentos (CPF, CNPJ, RG, Placa, Telefone, etc.):** Sempre armazenar **sem máscara/formatação** no banco de dados. A formatação deve ser aplicada apenas na camada de apresentação ou em DTOs de resposta.
- Todos os campos devem possuir a anotação `@Column` explícita, definindo `length`, `nullable`, `unique` e `columnDefinition` quando necessário.
- Enums devem sempre ser mapeados como `STRING` para evitar problemas de manutenção e legibilidade no banco.
- Utilizar preferencialmente os tipos do pacote `java.time` (`LocalDate`, `LocalDateTime`, `Instant`).
- IDs sequenciais (`BIGINT`) são o padrão principal. UUID deve ser usado apenas quando houver justificativa clara (ex.: sistemas distribuídos, geração de ID offline, etc.).

## Exemplo de Entidade Cliente

```java
@Entity
@Table(name = "clientes")
public class Cliente {
    
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
    
    @Column(name = "nome", length = 100, nullable = false)
    private String nome;
    
    @Column(name = "cpf", length = 11, unique = true)
    private String cpf;
    
    @Column(name = "cnpj", length = 14, unique = true)
    private String cnpj;
    
    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private StatusCliente status;
    
    @Column(name = "criado_em", columnDefinition = "DATETIME(6)", nullable = false)
    private LocalDateTime criadoEm;
    
    @Column(name = "atualizado_em", columnDefinition = "DATETIME(6)", nullable = false)
    private LocalDateTime atualizadoEm;
    
    // getters e setters...
}
```

## Justificativa
A decisão baseia-se nos seguintes motivos:

- **Consistência:** Padronização facilita manutenção e onboarding de novos desenvolvedores
- **Performance:** Tipos adequados otimizam uso de índices e storage
- **Legibilidade:** Código mais claro e auto-documentado
- **Segurança:** Armazenamento de documentos sem máscara facilita validações e buscas
- **Compatibilidade:** Aproveitamento de recursos específicos do MySQL 8.0
- **Manutenibilidade:** Enums como STRING evitam problemas em refatorações
- **Precisão:** `DECIMAL` para valores monetários evita problemas de arredondamento

## Alternativas Consideradas

- **Usar `INT` ao invés de `BIGINT`:** Rejeitado (limite pequeno para crescimento)
- **Usar `ORDINAL` para Enums:** Rejeitado (frágil em refatorações)
- **Armazenar CPF/CNPJ com máscara:** Rejeitado (dificulta buscas e validações)
- **Usar `CHAR(36)` para UUID:** Aceito apenas em casos específicos (prioridade para `BINARY(16)`)
- **Usar `DOUBLE` para valores monetários:** Rejeitado (problemas de precisão)

## Consequências

**Benefícios:**
- Alta consistência entre todas as entidades do projeto
- Melhor performance de índices (especialmente com `BIGINT` e `BINARY(16)`)
- Facilita queries manuais e debugging
- Reduz erros de mapeamento entre Java e MySQL
- Facilita a manutenção e onboarding de novos desenvolvedores
- Maior segurança no armazenamento de dados sensíveis

**Desafios:**
- Pouca flexibilidade caso surja uma necessidade muito específica (pode ser contornado com exceções documentadas)
- Necessidade de disciplina da equipe para seguir o padrão
- Curva de aprendizado inicial para novos desenvolvedores

**Impacto no Desenvolvimento:**
- Todas as novas entidades devem seguir esta tabela de mapeamento
- Entidades legadas devem ser gradualmente migradas para este padrão quando forem alteradas
- Revisão de pull requests deve incluir verificação do cumprimento deste ADR

## Referências
- MySQL 8.0 Reference Manual: https://dev.mysql.com/doc/refman/8.0/en/
- Hibernate ORM Documentation: https://docs.jboss.org/hibernate/orm/current/
- Spring Data JPA Documentation: https://spring.io/projects/spring-data-jpa
- Effective Java (Joshua Bloch) - Capítulo sobre tipos de dados
