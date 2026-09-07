# ADR-019: Uso do Lombok para Redução de Código Boilerplate

**Número do ADR:** 019  
**Título:** Uso do Lombok para Redução de Código Boilerplate  
**Data:** 2026-05-02  
**Responsável:** Arquiteto do Projeto  
**Status:** Aceito

## Contexto
O projeto usa Java 21 com Spring Boot 4.x e aplica Clean Architecture com múltiplos bounded contexts. Isso resulta em um grande número de classes de domínio, DTOs, comandos, responses, entidades JPA e gateways — cada uma necessitando de getters, setters, construtores, `equals`, `hashCode` e `toString`.

Escrever e manter esse código repetitivo manualmente aumenta a carga cognitiva da equipe, introduz risco de inconsistências (ex.: `equals`/`hashCode` esquecidos em Value Objects) e polui a leitura do código de negócio com detalhes de infraestrutura.

## Decisão
Foi decidido adotar o **Project Lombok 1.18.44** com as seguintes restrições de uso:

### Anotações permitidas e contextos de uso

| Anotação | Uso Permitido | Contexto |
|----------|--------------|---------|
| `@Getter` / `@Setter` | Geração seletiva de accessors | Entidades JPA, DTOs, Commands |
| `@RequiredArgsConstructor` | Construtor com campos `final` | Use Cases (injeção de dependências Spring) |
| `@NoArgsConstructor` | Construtor sem argumentos | Entidades JPA (exigido pelo Hibernate) |
| `@AllArgsConstructor` | Construtor com todos os campos | Classes imutáveis simples |
| `@Builder` | Builder pattern | Commands, Responses, DTOs complexos |
| `@Slf4j` | Injeção do logger SLF4J como campo estático `log` | Qualquer classe com necessidade de log |
| `@Data` | Getter + Setter + equals + hashCode + toString | **Apenas DTOs e classes de transferência — NUNCA em entidades JPA ou Value Objects de domínio** |
| `@EqualsAndHashCode` | Customização de equals/hashCode | Entidades com identidade própria |
| `@ToString` | Customização do toString | Quando excluir campos sensíveis |

### Restrições obrigatórias

1. **Nunca usar `@Data` em entidades JPA** — gera `equals`/`hashCode` baseado em todos os campos, causando problemas com Hibernate lazy loading e coleções
2. **Nunca usar `@EqualsAndHashCode` sem especificar `callSuper` explicitamente** em classes que herdam de outra
3. **Nunca usar `@ToString` sem `@ToString.Exclude`** em campos de coleções JPA (risco de stack overflow por lazy initialization)
4. **Em Value Objects de domínio** — implementar `equals`/`hashCode` manualmente para garantir semântica correta por valor
5. **Excluir Lombok do JAR de runtime** — configurado no `spring-boot-maven-plugin`:
```xml
<configuration>
    <excludes>
        <exclude>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </exclude>
    </excludes>
</configuration>
```

### Configuração no pom.xml
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.44</version>
    <scope>compile</scope>  <!-- processador de anotações em tempo de compilação -->
</dependency>
```

O Lombok é configurado como `annotationProcessorPath` no `maven-compiler-plugin` para garantir correto processamento com Java 21.

## Justificativa
- **Redução de ruído:** Use Cases e Domain Services focam em lógica de negócio; getters/setters não poluem a leitura do código
- **`@RequiredArgsConstructor` com Spring:** Elimina construtores verbosos para injeção de dependências — particularmente útil em Use Cases com múltiplos gateways injetados
- **`@Slf4j`:** Elimina a declaração manual do logger em cada classe (`private static final Logger log = LoggerFactory.getLogger(...)`); reduz ~2 linhas por classe de logging
- **Versão 1.18.44:** Compatível com Java 21 e processamento de anotações do Maven; versão estável e madura
- **Exclusão do runtime:** Lombok é exclusivamente uma ferramenta de geração de código em tempo de compilação — não pertence ao classpath de runtime, reduzindo o tamanho do JAR
- **Consenso de mercado:** Lombok é amplamente adotado em projetos Spring Boot Java — habilidade transferível

## Alternativas Consideradas

### Records Java (nativo no Java 16+)
- **Vantagem:** Nativo à linguagem, imutáveis por padrão, `equals`/`hashCode`/`toString` automáticos, sem dependência externa
- **Contextos de uso no projeto:** Usados para Commands e Responses onde imutabilidade é adequada. **Não substitui Lombok completamente** pois:
  - Records não podem ser extendidos — inadequados para hierarquias de exceções de domínio
  - Records não funcionam como entidades JPA (exigem construtor sem argumentos e campos mutáveis)
  - Records não oferecem `@Builder` ou `@Slf4j`
- **Decisão:** Usados em conjunto com Lombok onde adequado (Commands como records, entidades com Lombok)

### MapStruct para mapeamento
- **Vantagem:** Geração de código em tempo de compilação para mapeamento entre objetos — mais explícito que `@Builder`
- **Desvantagem:** Dependência adicional; para o volume atual de mapeamentos, `toCommand()` nos próprios DTOs (padrão já adotado) é suficiente e mais legível
- **Decisão:** Não adotado; o projeto usa método `toCommand()` nos DTOs e `from()` nas Responses

### Implementação manual de todos os métodos
- **Vantagem:** Zero dependências externas; controle total
- **Desvantagem:** Volume enorme de código boilerplate; risco de inconsistências em `equals`/`hashCode`; código difícil de ler
- **Decisão:** Descartada

## Consequências

### Benefícios
- Redução significativa de linhas de código boilerplate nas camadas web, application e infrastructure
- `@Slf4j` garante que todos os loggers são declarados consistentemente
- `@RequiredArgsConstructor` elimina construtores verbosos de injeção de dependências
- `@Builder` facilita a criação de objetos imutáveis em testes

### Desafios
- IDE precisa do plugin Lombok instalado para resolver os métodos gerados
- Desenvolvedores precisam conhecer as restrições de uso (especialmente `@Data` em entidades JPA)
- Debugging pode ser ligeiramente mais difícil — métodos gerados não aparecem no código-fonte
- Atualizações de versão do Lombok podem requerer ajustes ao mudar versão do Java

### Impacto no Desenvolvimento
- **Entidades JPA:** usar `@Getter` + `@Setter` seletivamente + `@NoArgsConstructor` + `@AllArgsConstructor(access = AccessLevel.PRIVATE)` — nunca `@Data`
- **Use Cases:** usar `@RequiredArgsConstructor` + `@Slf4j`
- **DTOs e Commands:** usar `@Data` (quando mutáveis) ou Records Java (quando imutáveis)
- **Value Objects de domínio:** implementar `equals`/`hashCode` manualmente para garantir semântica por valor

## Referências
- Project Lombok: https://projectlombok.org/
- Lombok com Spring Boot: https://projectlombok.org/setup/maven
- Problemas de `@Data` com JPA: https://thorben-janssen.com/lombok-hibernate-how-to-avoid-common-pitfalls/
- Java Records (JEP 395): https://openjdk.org/jeps/395
- ADR-001: Adoção de Clean Architecture com DDD
- ADR-006: Estrutura de Pacotes da Aplicação
