# ADR-011: Padronização de Respostas de Erro da API

**Número do ADR:** 011  
**Título:** Padronização de Respostas de Erro da API  
**Data:** 2026-04-04  
**Responsável:** Arquiteto do Projeto  
**Status:** Aceito

## Contexto
A API precisa retornar mensagens de erro consistentes, amigáveis e úteis para os clientes (frontend, mobile ou integrações). Era necessário definir um formato padrão de resposta de erro que não exponha detalhes internos do domínio ou stack traces, seguindo boas práticas de REST.

## Decisão
Foi decidido criar um `ErrorResponse` padronizado na camada `web`, tratado por um `@ControllerAdvice` global.

**Localização dos artefatos:**

```text
contextoA/
└── web/
    ├── dtos/
    │   ├── ErrorResponse.java
    │   └── FieldError.java
    └── exception/
        └── GlobalExceptionHandler.java
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
- Fornece respostas de erro consistentes em toda a API
- Separa claramente as responsabilidades (domínio lança exceção → web transforma em resposta HTTP)
- Melhora experiência do consumidor da API
- Facilita logging, monitoramento e tratamento de erros no frontend
- Segue padrões REST e boas práticas de API Design

## Alternativas Consideradas
- Criar handler por controller → Rejeitado (duplicação de código)

## Consequências

**Benefícios:**
- Respostas de erro padronizadas e previsíveis
- Melhor integração com frontend e sistemas externos
- Facilita debug e monitoramento (com errorCode)

**Desafios:**
- Manter o GlobalExceptionHandler atualizado quando novas exceções forem criadas
- Garantir que todas as exceções do domínio sejam mapeadas corretamente

**Impacto no Desenvolvimento:**
- Todo novo DomainException deve ter tratamento adequado no GlobalExceptionHandler

## Referências
- REST API Error Handling Best Practices
- RFC 7807 - Problem Details for HTTP APIs
- Clean Architecture – Robert C. Martin
