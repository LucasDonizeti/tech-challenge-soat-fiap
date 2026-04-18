# ADR-009: Estratégia de Testes - Unitários, Integração e E2E

**Número do ADR:** 009  
**Título:** Estratégia de Testes - Unitários, Integração e E2E  
**Data:** 2026-04-04  
**Responsável:** Arquiteto do Projeto  
**Status:** Aceito

## Contexto
Com a adoção da Clean Architecture e Domain-Driven Design, o projeto possui camadas bem definidas e múltiplos bounded contexts. Era necessário definir uma estratégia clara de testes que garantisse:

- Alta qualidade e cobertura do domínio de negócio
- Proteção contra regressões em regras complexas
- Testes confiáveis de integração com banco de dados e componentes externos
- Validação dos fluxos completos da aplicação (E2E)
- Manutenibilidade e velocidade de execução dos testes no CI/CD

## Decisão
Foi decidido adotar uma estratégia de testes baseada em **três camadas principais**:

- **Testes Unitários**
- **Testes de Integração**
- **Testes End-to-End (E2E)**

A estrutura de diretórios seguirá o padrão abaixo:

```text
src/
└── test/
    └── java/
        ├── administrativo/                          ← Testes Unitários (espelhados)
        │   ├── domain/
        │   ├── application/
        │   ├── web/
        │   └── infrastructure/
        │
        ├── integration/                        ← Testes de Integração
        │   ├── administrativo/
        │   │   ├── persistence/
        │   │   ├── gateways/
        │   │   └── web/
        │   └── sharedkernel/
        │
        └── e2e/                                ← Testes End-to-End
            └── ClienteFlowE2ETest.java
```

### Descrição dos Tipos de Testes

#### 1. Testes Unitários
**Localização:** `src/test/java/administrativo/domain/...`, `administrativo/application/...`, etc. (espelhando a estrutura do main)

**Propósito:** Testar unidades isoladas de código, especialmente as regras de negócio puras.

**Características:**
- Rápidos e determinísticos
- Poucos ou nenhum mock externo
- Foco principal no domínio (domain/)
- Usam JUnit 5 + Mockito

**Exemplos:**
- `ClienteTest.java` (regras de agregados e value objects)
- `CriarClienteUseCaseTest.java` (use cases com mocks de ports)
- `CPFTest.java` (value objects do sharedkernel)

**Exemplo Implementado:**
```java
@ExtendWith(MockitoExtension.class)
class CriarClienteUseCaseTest {
    @Mock
    private ClienteRepository repository;
    
    @InjectMocks
    private CriarClienteUseCase useCase;
    
    @Test
    void deveCriarClienteComDadosValidos() {
        // Arrange
        CriarClienteCommand command = new CriarClienteCommand(
            Nome.of("João Silva"), 
            CPF.of("12345678909"), 
            Email.of("joao@teste.com")
        );
        
        when(repository.existsByCPF(any())).thenReturn(false);
        when(repository.existsByEmail(any())).thenReturn(false);
        when(repository.save(any())).thenReturn(Cliente.criar(...));
        
        // Act
        Cliente result = useCase.execute(command);
        
        // Assert
        assertNotNull(result);
        assertEquals("João Silva", result.getNome().getValor());
        assertEquals("123.456.789-09", result.getCpf().getValor());
    }
}
```

#### 2. Testes de Integração
**Localização:** `src/test/java/integration/administrativo/...`

**Propósito:** Validar a integração entre componentes (banco de dados, gateways, serviços externos).

**Características:**
- Usam banco de dados H2 em memória com isolamento completo
- Testam repositórios, mappers, gateways e controllers com Spring
- Mais lentos que unitários
- Usam @SpringBootTest + @DirtiesContext para isolamento
- Configuração específica em application-test.properties

**Exemplos:**
- `ClienteRepositoryIntegrationTest.java`
- `ClienteGatewayIntegrationTest.java`
- `integration/administrativo/web/ClienteControllerIntegrationTest.java`

**Exemplo Implementado:**
```java
@SpringBootTest(classes = OficinaApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext
@DisplayName("Testes de Integração - ClienteController")
class ClienteControllerIntegrationTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    @Test
    @DisplayName("Deve criar cliente pessoa física com sucesso")
    void deveCriarClientePessoaFisica() throws Exception {
        // Arrange - CPF válido
        String requestBody = """
            {
                "nome": "Carlos Alberto Silva",
                "cpf": "98765432100",
                "email": "carlos.silva@exemplo.com"
            }
            """;
        
        // Act & Assert
        mockMvc.perform(post("/v1/admin/clientes")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Carlos Alberto Silva"))
                .andExpect(jsonPath("$.cpf").value("987.654.321-00"))
                .andExpect(jsonPath("$.email").value("carlos.silva@exemplo.com"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }
}
```

#### 3. Testes End-to-End (E2E)
**Localização:** `src/test/java/e2e/`

**Propósito:** Validar fluxos completos da aplicação do ponto de vista do usuário.

**Características:**
- Testam a aplicação completa (API + Banco)
- Usam H2 em memória com @DirtiesContext para isolamento total
- MockMvc para simulação de requisições HTTP
- Executados menos frequentemente
- Validação de contratos HTTP completos
- Sem dependências externas (Testcontainers removido)

**Exemplos:**
- `ClienteFlowE2ETest.java`
- `OrdemServicoFlowE2ETest.java`
- `AutenticacaoE2ETest.java`

**Exemplo Implementado:**
```java
@SpringBootTest(classes = com.techchallenge.oficina.OficinaApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext
@DisplayName("Testes End-to-End - Fluxos Completos de Cliente")
class ClienteFlowE2ETest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    @Test
    @DisplayName("Deve criar cliente pessoa física com sucesso")
    void deveCriarClientePessoaFisicaComSucesso() throws Exception {
        mockMvc.perform(post("/v1/admin/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "nome": "João Silva",
                        "cpf": "12345678909",
                        "email": "joao.silva@exemplo.com"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.cpf").value("123.456.789-09"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }
}
```

### Configurações Essenciais:

1. **H2 Database Configuration:**
   ```properties
   # application-test.properties
   spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL
   spring.jpa.hibernate.ddl-auto=create-drop
   spring.flyway.enabled=false  # Hibernate cria as tabelas
   spring.jpa.defer-datasource-initialization=true
   ```

2. **MockMvc Manual Setup:**
   ```java
   @BeforeEach
   void setUp() {
       this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
   }
   ```

3. **Isolamento com @DirtiesContext:**
   ```java
   @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
   @ActiveProfiles("test")
   @DirtiesContext  // Limpa contexto após cada teste
   class ClienteFlowE2ETest {
   }
   ```

4. **Dados de Teste Válidos:**
   - CPF: `"12345678909"` → formatado `"123.456.789-09"`
   - CNPJ: `"11222333000181"` → formatado `"11.222.333/0001-81"`
   - Nome: `"Cliente Um"` (sem números)
   - Email: `"teste@exemplo.com"`

5. **Tratamento de Exceções do Controller:**
   ```java
   // Sistema lança IllegalArgumentException para cliente não encontrado
   mockMvc.perform(get("/v1/admin/clientes/{id}", idInexistente))
           .andExpect(status().isBadRequest()); // Comportamento atual
   ```

6. **Princípios de Isolamento Aplicados:**
   - Cada teste tem seu próprio contexto Spring
   - Banco H2 recriado a cada teste (@DirtiesContext)
   - CPFs/CNPJs únicos por teste para evitar duplicação
   - MockMvc setup manual para evitar dependências externas

7. **Impacto no Desenvolvimento:**
   - Testes unitários devem ser escritos junto com o código (TDD recomendado no domínio)
   - Testes de integração e E2E priorizados para fluxos críticos de negócio
   - Configuração de profiles específicos para cada tipo de teste
   - Uso de dados válidos é fundamental para evitar falsos negativos

8. **Padrões e Princípios Aplicados:**

   **AAA Pattern (Arrange, Act, Assert):**
   ```java
   @Test
   void deveCriarClienteComSucesso() {
       // Arrange
       String requestBody = "{...}";
       
       // Act
       ResultActions result = mockMvc.perform(post(...));
       
       // Assert
       result.andExpect(status().isCreated());
   }
   ```

   **Test Independence:**
   - Cada teste não depende de outros
   - Dados limpos a cada execução
   - Ordem de execução não importa

   **Single Responsibility:**
   - Cada teste valida um cenário específico
   - Testes focados em um fluxo de negócio
   - Nomeclatura descritiva dos testes

   **Fast Feedback Loop:**
   - Testes executam em ~9 segundos
   - Feedback rápido para desenvolvedor
   - Execução paralela possível no CI

## Justificativa
A estratégia escolhida baseia-se nas seguintes razões:

- **Test Pyramid:** Maior quantidade de testes unitários (rápidos), menor quantidade de testes de integração e E2E (mais lentos).
- **Clean Architecture:** Permite testar o domain de forma pura e isolada, sem dependências de frameworks.
- **Visibilidade de Falhas:** Unitários pegam erros cedo, integração pegam problemas de persistência/integração, E2E garantem que o sistema funciona como um todo.
- **Manutenibilidade:** Estrutura clara facilita encontrar e manter os testes.
- **Performance no CI/CD:** H2 em memória + @DirtiesContext garante execução rápida e isolada.
- **Simplicidade:** Remoção de Testcontainers simplifica setup e reduz dependências.

## Alternativas Consideradas

- **Testcontainers para E2E:** Descartado por complexidade e lentidão no setup
- **RestAssured:** Descartado por problemas de compatibilidade com Spring Boot 4.0.3
- **Estrutura de testes espelhada para todos os tipos:** Gera confusão entre unitários e integração.
- **Testes E2E dentro de cada contexto:** Dificulta organização de fluxos que cruzam múltiplos bounded contexts.

## Consequências

### Benefícios:
- Alta cobertura e confiança no domínio de negócio
- Testes rápidos no dia a dia do desenvolvedor
- Validação robusta de integração e fluxos completos
- Facilita onboarding e manutenção da suíte de testes
- Isolamento completo entre testes (sem interferência)
- Execução rápida no CI/CD (sem dependências externas)

### Desafios:
- Maior número de diretórios e configurações de teste
- Necessidade de disciplina para manter os testes no nível correto
- Testes de integração e E2E exigem mais tempo de manutenção

## Referências
- Test Pyramid – Mike Cohn
- Clean Architecture – Robert C. Martin
- Domain-Driven Design – Eric Evans
- Spring Boot Testing Documentation
- MockMvc Documentation
- H2 Database Documentation
