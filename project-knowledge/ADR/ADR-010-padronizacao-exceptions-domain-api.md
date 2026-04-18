# ADR-010: Padronização de Exceções no Domínio e Respostas de Erro da API

**Número do ADR:** 010  
**Título:** Padronização de Exceções no Domínio e Respostas de Erro da API  
**Data:** 2026-04-04  
**Responsável:** Arquiteto do Projeto  
**Status:** Aceito

## Contexto
Com a adoção da Clean Architecture e DDD, as regras de negócio estão concentradas na camada `domain`. Era necessário definir uma forma consistente de lançar exceções quando regras de negócio são violadas, mantendo o domínio puro, rico em linguagem ubíqua e independente de frameworks. Adicionalmente, a API precisa retornar mensagens de erro consistentes, amigáveis e úteis para os clientes (frontend, mobile ou integrações), seguindo boas práticas de REST.

## Decisão
Foi decidido criar uma hierarquia de exceções baseada em `DomainException` e um `ErrorResponse` padronizado na camada `web`, tratado por um `@ControllerAdvice` global.

**Estrutura adotada:**

```text
sharedkernel/
└── domain/
    └── exceptions/
        ├── DomainException.java          ← Classe base abstrata
        ├── BusinessException.java
        └── ValorInvalidoException.java

contextoA/
└── domain/
    └── exceptions/
        ├── ClienteNaoEncontradoException.java
        └── VeiculoJaCadastradoException.java

contextoA/
└── web/
    ├── dtos/
    │   ├── ErrorResponse.java
    │   └── FieldError.java
    └── exception/
        └── GlobalExceptionHandler.java
```

**Exemplo da classe base:**
```java
// sharedkernel/domain/exceptions/DomainException.java
public abstract class DomainException extends RuntimeException {

    private final String errorCode;

    protected DomainException(String message) {
        this(message, null);
    }

    protected DomainException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode != null ? errorCode : this.getClass().getSimpleName();
    }

    public String getErrorCode() {
        return errorCode;
    }
}
```

**Estrutura do ErrorResponse:**
```java
// web/dtos/ErrorResponse.java
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String errorCode,
    String path,
    List<FieldError> errors
) {

    public static ErrorResponse of(HttpStatus status, String message, String errorCode, String path) {
        return new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            message,
            errorCode,
            path,
            null
        );
    }
}

public record FieldError(String field, String message) {}
```

**Tratamento Global:**
```java
// web/exception/GlobalExceptionHandler.java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex, WebRequest request) {
        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST,
            ex.getMessage(),
            ex.getErrorCode(),
            request.getDescription(false)
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException ex, WebRequest request) {
        // lógica para mapear field errors...
    }
}
```

## Justificativa
A decisão de padronizar exceções e respostas de erro baseia-se nos seguintes motivos:

**Para as exceções de domínio:**
- Permite usar linguagem ubíqua nas exceções (ex: ClienteNaoEncontradoException)
- Mantém o domínio limpo e independente de frameworks
- Facilita tratamento uniforme na camada superior
- Possibilita mapear exceções para códigos de erro padronizados na API
- Melhora legibilidade e manutenibilidade do código de domínio

**Para as respostas de erro da API:**
- Fornece respostas de erro consistentes em toda a API
- Separa claramente as responsabilidades (domínio lança exceção → web transforma em resposta HTTP)
- Melhora experiência do consumidor da API
- Facilita logging, monitoramento e tratamento de erros no frontend
- Segue padrões REST e boas práticas de API Design

## Alternativas Consideradas

**Para exceções de domínio:**
- Lançar diretamente RuntimeException ou IllegalArgumentException no domínio → Rejeitado (polui o domínio e perde expressividade)
- Usar apenas uma exceção genérica (BusinessException) → Rejeitado (perde intenção e linguagem ubíqua)
- Exceções checked (extends Exception) → Rejeitado (aumenta complexidade desnecessária no domínio)

**Para respostas de erro da API:**
- Retornar a exceção diretamente → Rejeitado (exposição de detalhes internos)
- Usar ProblemDetail (RFC 7807) → Considerado, mas adiado por complexidade adicional no início do projeto
- Criar handler por controller → Rejeitado (duplicação de código)

## Consequências

**Benefícios:**
- Domínio mais rico e expressivo
- Tratamento centralizado de erros na API
- Melhor rastreabilidade de erros de negócio
- Respostas de erro padronizadas e previsíveis
- Melhor integração com frontend e sistemas externos
- Facilita debug e monitoramento (com errorCode)

**Desafios:**
- Necessidade de criar exceções específicas para cada regra importante
- Disciplina para não lançar exceções genéricas no domínio
- Manter o GlobalExceptionHandler atualizado quando novas exceções forem criadas
- Garantir que todas as exceções do domínio sejam mapeadas corretamente

**Impacto no Desenvolvimento:**
- Use Cases e Domain Services devem deixar as exceções subirem (não capturar desnecessariamente)
- Todo novo DomainException deve ter tratamento adequado no GlobalExceptionHandler

## Referências
- Domain-Driven Design – Eric Evans
- Clean Architecture – Robert C. Martin
- REST API Error Handling Best Practices
- RFC 7807 - Problem Details for HTTP APIs
