# ADR-006: Estrutura de Pacotes da Aplicação com Clean Architecture e Bounded Contexts

**Número do ADR:** 006  
**Título:** Estrutura de Pacotes da Aplicação com Clean Architecture e Bounded Contexts  
**Data:** 2026-03-28  
**Responsável:** Arquiteto do Projeto  
**Status:** Aceito

## Contexto
O projeto possui múltiplos contextos delimitados com regras de negócio complexas e independentes (ex: gerenciamento de clientes, ordens de serviço, estoque, administrativo). Foi necessário definir uma estrutura de pacotes clara, consistente e escalável que respeite os princípios da Clean Architecture, evite violações de dependências e facilite a manutenção e evolução independente dos contextos.

## Decisão
Foi decidido adotar a seguinte estrutura de pacotes para o projeto, combinando **Clean Architecture** (camadas com regra de dependência) e **Domain-Driven Design** (Bounded Contexts + Shared Kernel). Todos os nomes de pacotes evitam palavras reservadas da linguagem Java.

```text
com.techchallenge.oficina
├── administrativo/                               
│   ├── domain/
│   │   ├── model/
│   │   │   ├── aggregates/
│   │   │   ├── entities/
│   │   │   └── valueobjects/
│   │   ├── events/
│   │   ├── exceptions/
│   │   └── services/
│   ├── application/
│   │   └── usecases/
│   ├── web/
│   │   ├── controllers/
│   │   ├── dtos/
│   │   └── mappers/
│   └── infrastructure/
│       ├── persistence/
│       ├── gateways/
│       ├── external/
│       └── config/

├── os/                             
│   ├── domain/
│   │   ├── model/
│   │   │   ├── aggregates/
│   │   │   ├── entities/
│   │   │   └── valueobjects/
│   │   ├── events/
│   │   ├── exceptions/
│   │   └── services/
│   ├── application/
│   │   └── usecases/
│   ├── web/
│   │   ├── controllers/
│   │   ├── dtos/
│   │   └── mappers/
│   └── infrastructure/
│       ├── persistence/
│       ├── gateways/
│       ├── external/
│       └── config/

├── sharedkernel/
│   ├── domain/
│   │   ├── valueobjects/
│   │   ├── events/
│   │   └── exceptions/
│   ├── application/
│   │   └── ports/
│   ├── infrastructure/
│   │   └── security/
│   └── common/
│       ├── annotations/
│       └── exceptions/

└── infrastructure/                         
    └── config/
        ├── security/
        └── spring/
```

### Descrição dos Pacotes

#### 1. administrativo/ e os/ (Bounded Contexts)
Cada bounded context representa um subdomínio com seu próprio modelo de negócio e regras independentes.

**domain/**
- **Propósito (Clean Architecture):** Camada central. Contém apenas regras de negócio puras, sem dependências externas.
- **Descrição:** Armazena entidades, agregados, value objects, eventos, exceções e serviços do domínio.
- **Subpacotes:**
  - `model/` - Entidades, agregados e value objects
    - `aggregates/` - Agregados raiz (consistência de transações)
    - `entities/` - Entidades do domínio
    - `valueobjects/` - Value objects imutáveis
  - `events/` - Eventos de domínio
  - `exceptions/` - Exceções específicas do domínio
  - `services/` - Serviços de domínio para lógica que não pertence a entidades específicas
- **Exemplos de código:**
```java
// domain/model/aggregates/Cliente.java
@Entity
public class Cliente extends AbstractAggregateRoot<Cliente> {
    @Id
    private ClienteId id;
    private Nome nome;
    private CPF cpf;
    private Email email;
    
    protected Cliente() {} // JPA
    
    public static Cliente criar(Nome nome, CPF cpf, Email email) {
        Cliente cliente = new Cliente();
        cliente.id = ClienteId.generate();
        cliente.nome = nome;
        cliente.cpf = cpf;
        cliente.email = email;
        
        // Domain Event (padrão Spring Data)
        cliente.registerEvent(new ClienteCriadoEvent(cliente.id));
        return cliente;
    }
    
    @DomainEvents
    public Collection<DomainEvent> domainEvents() {
        return super.domainEvents();
    }
    
    @AfterDomainEventPublication
    public void clearDomainEvents() {
        super.clearDomainEvents();
    }
}
```

```java
// domain/model/entities/Veiculo.java
@Entity
public class Veiculo {
    @Id
    private VeiculoId id;
    private Placa placa;
    private Modelo modelo;
    private Integer ano;
    
    // Entidade com identidade própria, mas não agregado raiz
    public void atualizarModelo(Modelo novoModelo) {
        if (novoModelo == null) {
            throw new IllegalArgumentException("Modelo não pode ser nulo");
        }
        this.modelo = novoModelo;
    }
}
```

```java
// domain/model/valueobjects/CPF.java
public final class CPF {
    private final String valor;
    
    private CPF(String valor) {
        this.valor = validar(valor);
    }
    
    public static CPF of(String valor) {
        return new CPF(valor);
    }
    
    private String validar(String valor) {
        if (!isValidCPF(valor)) {
            throw new IllegalArgumentException("CPF inválido");
        }
        return valor.replaceAll("[^0-9]", "");
    }
    
    private boolean isValidCPF(String cpf) {
        // Lógica de validação de CPF
        return cpf != null && cpf.matches("\\d{11}");
    }
    
    // equals, hashCode, toString...
}
```

```java
// domain/events/ClienteCriadoEvent.java
public class ClienteCriadoEvent extends DomainEvent {
    private final ClienteId clienteId;
    
    public ClienteCriadoEvent(ClienteId clienteId) {
        super(Instant.now());
        this.clienteId = clienteId;
    }
    
    public ClienteId getClienteId() {
        return clienteId;
    }
}
```

```java
// domain/exceptions/CpfJaCadastradoException.java
public class CpfJaCadastradoException extends DomainException {
    private final CPF cpf;
    
    public CpfJaCadastradoException(CPF cpf) {
        super("CPF já cadastrado: " + cpf.getValor());
        this.cpf = cpf;
    }
    
    public CPF getCpf() {
        return cpf;
    }
}
```

```java
// domain/services/ClienteDomainService.java
@Service
public class ClienteDomainService {
    private final ClienteRepository repository;
    
    public ClienteDomainService(ClienteRepository repository) {
        this.repository = repository;
    }
    
    public void validarCPFUnico(CPF cpf) {
        if (repository.existsByCPF(cpf)) {
            throw new CpfJaCadastradoException(cpf);
        }
    }
    
    public boolean isClienteAtivo(Cliente cliente) {
        return cliente.getStatus() == StatusCliente.ATIVO 
               && !cliente.possuiPendenciasFinanceiras();
    }
}
```

**application/**
- **Propósito:** Orquestra casos de uso e coordena o domínio.
- **Descrição:** Contém a lógica de aplicação (use cases).
- **Subpacotes:**
  - `usecases/` - Casos de uso específicos do bounded context
- **Exemplo de código:**
```java
// application/usecases/CriarClienteUseCase.java
@Service
@Transactional
public class CriarClienteUseCase {
    private final ClienteRepository repository;
    private final ClienteDomainService domainService;
    private final EventPublisher eventPublisher;
    
    public CriarClienteUseCase(ClienteRepository repository, 
                              ClienteDomainService domainService,
                              EventPublisher eventPublisher) {
        this.repository = repository;
        this.domainService = domainService;
        this.eventPublisher = eventPublisher;
    }

    public ClienteResponse execute(CriarClienteCommand command) {
        // Validações de aplicação
        command.validate();
        
        // Validações de domínio via Domain Service
        domainService.validarCPFUnico(command.getCpf());
        
        // Criação do aggregate (factory method)
        Cliente cliente = Cliente.criar(
            command.getNome(), 
            command.getCpf(), 
            command.getEmail()
        );
        
        // Persistência
        repository.save(cliente);
        
        // Publicação de eventos adicionais se necessário
        eventPublisher.publish(new ClienteCriadoEvent(cliente.getId()));
        
        return ClienteResponse.from(cliente);
    }
}
```

```java
// application/usecases/CriarClienteCommand.java
public record CriarClienteCommand(
    String nome,
    String cpf,
    String email
) {
    public CriarClienteCommand {
        validate();
    }
    
    private void validate() {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF é obrigatório");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
    }
    
    public Nome getNome() {
        return Nome.of(nome);
    }
    
    public CPF getCpf() {
        return CPF.of(cpf);
    }
    
    public Email getEmail() {
        return Email.of(email);
    }
}
```

```java
// application/usecases/ClienteResponse.java
public record ClienteResponse(
    String id,
    String nome,
    String cpf,
    String email,
    String status,
    LocalDateTime criadoEm
) {
    public static ClienteResponse from(Cliente cliente) {
        return new ClienteResponse(
            cliente.getId().getValue(),
            cliente.getNome().getValor(),
            cliente.getCpf().getValor(),
            cliente.getEmail().getValor(),
            cliente.getStatus().name(),
            cliente.getCriadoEm()
        );
    }
}
```

**web/**
- **Propósito:** Camada de entrada (Input Adapters - API REST).
- **Descrição:** Controllers, DTOs e mappers.
- **Subpacotes:**
  - `controllers/` - Endpoints REST/HTTP
  - `dtos/` - Data Transfer Objects para API
  - `mappers/` - Conversores entre DTOs e objetos de domínio
- **Exemplos de código:**
```java
// web/dtos/CriarClienteRequest.java
public record CriarClienteRequest(
    @NotBlank(message = "Nome é obrigatório")
    String nome,
    
    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 dígitos")
    String cpf,
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    String email
) {
    public CriarClienteCommand toCommand() {
        return new CriarClienteCommand(nome, cpf, email);
    }
}
```

```java
// web/controllers/ClienteController.java
@RestController
@RequestMapping("/v1/admin/clientes")
@Validated
public class ClienteController {
    private final CriarClienteUseCase criarClienteUseCase;
    private final BuscarClienteUseCase buscarClienteUseCase;
    private final ListarClientesUseCase listarClientesUseCase;
    
    public ClienteController(CriarClienteUseCase criarClienteUseCase,
                           BuscarClienteUseCase buscarClienteUseCase,
                           ListarClientesUseCase listarClientesUseCase) {
        this.criarClienteUseCase = criarClienteUseCase;
        this.buscarClienteUseCase = buscarClienteUseCase;
        this.listarClientesUseCase = listarClientesUseCase;
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> criar(
            @Valid @RequestBody CriarClienteRequest request) {
        ClienteResponse response = criarClienteUseCase.execute(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscar(@PathVariable UUID id) {
        ClienteId clienteId = ClienteId.of(id);
        ClienteResponse response = buscarClienteUseCase.execute(clienteId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<ClienteResponse> response = listarClientesUseCase.execute(pageable);
        return ResponseEntity.ok(response);
    }
}
```

```java
// web/mappers/ClienteMapper.java
@Component
public class ClienteMapper {
    
    public static ClienteResponse toResponse(Cliente cliente) {
        return ClienteResponse.from(cliente);
    }
    
    public static List<ClienteResponse> toResponseList(List<Cliente> clientes) {
        return clientes.stream()
                .map(ClienteMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    public static Page<ClienteResponse> toResponsePage(Page<Cliente> clientes) {
        return clientes.map(ClienteMapper::toResponse);
    }
}
```

**infrastructure/**
- **Propósito:** Camada externa (Output Adapters).
- **Descrição:** Implementações técnicas (banco de dados, gateways, configurações).
- **Subpacotes:**
  - `persistence/` - Implementações JPA/Hibernate e repositórios
  - `gateways/` - Implementações de interfaces externas (APIs, serviços)
  - `external/` - Integrações com sistemas externos
  - `config/` - Configurações específicas do bounded context
- **Exemplo de código:**
```java
// infrastructure/persistence/ClienteJpaRepository.java
@Repository
public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, UUID> {
    Optional<ClienteEntity> findByCpfValor(String cpf);
    boolean existsByCpfValor(String cpf);
    Page<ClienteEntity> findByStatus(StatusCliente status, Pageable pageable);
}
```

```java
// infrastructure/persistence/ClienteJpaMapper.java
@Component
public class ClienteJpaMapper {
    
    public ClienteEntity toEntity(Cliente cliente) {
        return ClienteEntity.builder()
                .id(cliente.getId().getValue())
                .nome(cliente.getNome().getValor())
                .cpf(cliente.getCpf().getValor())
                .email(cliente.getEmail().getValor())
                .status(cliente.getStatus())
                .criadoEm(cliente.getCriadoEm())
                .build();
    }
    
    public Cliente toDomain(ClienteEntity entity) {
        return Cliente.builder()
                .id(ClienteId.of(entity.getId()))
                .nome(Nome.of(entity.getNome()))
                .cpf(CPF.of(entity.getCpf()))
                .email(Email.of(entity.getEmail()))
                .status(entity.getStatus())
                .criadoEm(entity.getCriadoEm())
                .build();
    }
}
```

```java
// infrastructure/persistence/ClienteRepositoryImpl.java
@Repository
public class ClienteRepositoryImpl implements ClienteRepository {
    private final ClienteJpaRepository jpaRepository;
    private final ClienteJpaMapper mapper;
    
    public ClienteRepositoryImpl(ClienteJpaRepository jpaRepository, 
                                ClienteJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    public Cliente save(Cliente cliente) {
        ClienteEntity entity = mapper.toEntity(cliente);
        ClienteEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<Cliente> findById(ClienteId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }
    
    @Override
    public boolean existsByCPF(CPF cpf) {
        return jpaRepository.existsByCpfValor(cpf.getValor());
    }
}
```

```java
// infrastructure/gateways/EmailGateway.java
@Component
public class EmailGatewayImpl implements EmailGateway {
    private final JavaMailSender mailSender;
    
    public EmailGatewayImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    @Override
    public void enviarEmailBoasVindas(Email email, String nome) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email.getEndereco());
        message.setSubject("Bem-vindo ao Sistema");
        message.setText("Olá " + nome + ", bem-vindo ao nosso sistema!");
        
        mailSender.send(message);
    }
}
```

```java
// infrastructure/external/CnpjApiGateway.java
@Component
public class CnpjApiGatewayImpl implements CnpjApiGateway {
    private final RestTemplate restTemplate;
    private final String apiUrl;
    
    public CnpjApiGatewayImpl(RestTemplate restTemplate, 
                             @Value("${api.cnpj.url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
    }
    
    @Override
    public EmpresaData buscarPorCnpj(String cnpj) {
        try {
            String url = apiUrl + "/" + cnpj;
            return restTemplate.getForObject(url, EmpresaData.class);
        } catch (Exception e) {
            throw new ExternalApiException("Erro ao consultar CNPJ", e);
        }
    }
}
```

#### 2. sharedkernel/ (Shared Kernel)
Código realmente compartilhado entre os bounded contexts.

**domain/**
- **Propósito:** Value objects e exceções comuns a múltiplos contextos.
- **Subpacotes:**
  - `valueobjects/` - Value objects compartilhados (Email, CPF, etc.)
  - `events/` - Eventos de domínio compartilhados entre contextos
  - `exceptions/` - Exceções de domínio compartilhadas
- **Exemplo:**
```java
// sharedkernel/domain/valueobjects/Email.java
public final class Email {
    private final String endereco;

    private Email(String endereco) {
        this.endereco = validarEmail(endereco);
    }

    public static Email of(String endereco) {
        return new Email(endereco);
    }
}
```

```java
// sharedkernel/domain/events/DomainEvent.java
public abstract class DomainEvent {
    private final LocalDateTime occurredOn;
    
    protected DomainEvent() {
        this.occurredOn = LocalDateTime.now();
    }
    
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}
```

**application/ports/**
- **Propósito:** Interfaces (ports) compartilhadas entre bounded contexts.
- **Exemplo:**
```java
// sharedkernel/application/ports/AuthController.java
@RestController
@RequestMapping("/v1/auth")
public class AuthController {
    // Controller de autenticação compartilhado
}
```

**infrastructure/security/**
- **Propósito:** Componentes de segurança reutilizáveis.
- **Exemplos:** SecurityConfig.java, JwtTokenUtil.java, JwtAuthenticationFilter.java

**common/**
- **Propósito:** Utilitários e anotações genéricas (usar com moderação).
- **Subpacotes:**
  - `annotations/` - Anotações customizadas para auditoria, validação, etc.
  - `exceptions/` - Exceções de negócio compartilhadas entre contextos
- **Exemplo:**
```java
// common/annotations/Auditoria.java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditoria {
    String value() default "";
}
```

```java
// common/exceptions/BusinessException.java
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
```

#### 3. infrastructure/config/ (Opcional)
Configurações globais do Spring Boot.

**security/**
- **Propósito:** Configurações de segurança globais para toda a aplicação.
- **Exemplos:** SecurityConfig.java, configuração de JWT, autenticação.

**spring/**
- **Propósito:** Configurações globais do Spring Framework e beans compartilhados.
- **Exemplo:**
```java
// config/spring/ApplicationConfig.java
@Configuration
public class ApplicationConfig {
    
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
```

### Justificativa
A estrutura escolhida foi baseada nas seguintes razões:

- **Respeito à Regra de Dependência da Clean Architecture.**
- **Isolamento claro entre bounded contexts.**
- **Clareza, consistência e escalabilidade futura (fácil virar multi-module).**
- **Alta testabilidade do domínio.**

### Alternativas Consideradas

- **Estrutura por feature (Vertical Slice Architecture)**
- **Pacotes achatados ou por camada única**
- **Uso de pacotes com nomes reservados (ex: interface)**

### Consequências

**Benefícios:**

- Alta manutenibilidade e testabilidade
- Separação clara de responsabilidades
- Facilita evolução independente dos contextos
- Alinhamento com boas práticas de Clean Architecture + DDD

**Desafios:**

- Maior número de diretórios inicialmente
- Curva de aprendizado para a equipe
- Necessidade de disciplina para manter as camadas isoladas

**Impacto no Desenvolvimento:**
Requer atenção na definição dos limites dos contextos e revisão periódica da estrutura.

### Referências

- Clean Architecture – Robert C. Martin
- Domain-Driven Design – Eric Evans
