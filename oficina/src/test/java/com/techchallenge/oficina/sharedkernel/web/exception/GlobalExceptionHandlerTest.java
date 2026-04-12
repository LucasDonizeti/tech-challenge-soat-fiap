package com.techchallenge.oficina.sharedkernel.web.exception;

import com.techchallenge.oficina.sharedkernel.domain.exceptions.BusinessException;
import com.techchallenge.oficina.sharedkernel.domain.exceptions.ValorInvalidoException;
import com.techchallenge.oficina.sharedkernel.web.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private MethodArgumentTypeMismatchException typeMismatchException;

    @BeforeEach
    void setUp() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");
    }

    @Test
    @DisplayName("Deve handle DomainException")
    void deveHandleDomainException() {
        // Arrange
        BusinessException exception = new BusinessException("Erro de domínio", "DOMAIN_ERR");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDomainException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.status());
        assertEquals("Erro de domínio", errorResponse.message());
        assertEquals("DOMAIN_ERR", errorResponse.errorCode());
        assertEquals("/api/test", errorResponse.path());
    }

    @Test
    @DisplayName("Deve handle BusinessException")
    void deveHandleBusinessException() {
        // Arrange
        BusinessException exception = new BusinessException("Erro de negócio", "BIZ_ERR");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDomainException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("Erro de negócio", errorResponse.message());
        assertEquals("BIZ_ERR", errorResponse.errorCode());
    }

    @Test
    @DisplayName("Deve handle ValorInvalidoException")
    void deveHandleValorInvalidoException() {
        // Arrange
        ValorInvalidoException exception = new ValorInvalidoException("Valor inválido", "VAL_ERR");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDomainException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("Valor inválido", errorResponse.message());
        assertEquals("VAL_ERR", errorResponse.errorCode());
    }

    @Test
    @DisplayName("Deve handle MethodArgumentNotValidException")
    void deveHandleMethodArgumentNotValidException() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        org.springframework.validation.FieldError springFieldError = new org.springframework.validation.FieldError(
                "objectName", "nome", null, false, null, null, "Nome é obrigatório"
        );
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(springFieldError));

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationError(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.status());
        assertEquals("Dados de entrada inválidos", errorResponse.message());
        assertEquals("VALIDATION_ERROR", errorResponse.errorCode());
        assertNotNull(errorResponse.errors());
        assertEquals(1, errorResponse.errors().size());
        assertEquals("nome", errorResponse.errors().get(0).field());
        assertEquals("Nome é obrigatório", errorResponse.errors().get(0).message());
    }

    @Test
    @DisplayName("Deve handle MethodArgumentNotValidException com múltiplos erros")
    void deveHandleMethodArgumentNotValidExceptionComMultiplosErros() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        org.springframework.validation.FieldError fieldError1 = new org.springframework.validation.FieldError(
                "objectName", "nome", null, false, null, null, "Nome é obrigatório"
        );
        org.springframework.validation.FieldError fieldError2 = new org.springframework.validation.FieldError(
                "objectName", "email", null, false, null, null, "Email inválido"
        );
        org.springframework.validation.FieldError fieldError3 = new org.springframework.validation.FieldError(
                "objectName", "cpf", null, false, null, null, "CPF inválido"
        );
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2, fieldError3));

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationError(exception, webRequest);

        // Assert
        assertNotNull(response);
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(3, errorResponse.errors().size());
    }

    @Test
    @DisplayName("Deve handle MethodArgumentTypeMismatchException")
    void deveHandleMethodArgumentTypeMismatchException() {
        // Arrange
        when(typeMismatchException.getName()).thenReturn("id");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleTypeMismatchException(typeMismatchException, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.status());
        assertEquals("Parâmetro inválido: id", errorResponse.message());
        assertEquals("INVALID_PARAMETER", errorResponse.errorCode());
    }

    @Test
    @DisplayName("Deve handle Exception genérica")
    void deveHandleExceptionGenerica() {
        // Arrange
        Exception exception = new Exception("Erro inesperado");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.status());
        assertEquals("Erro interno do servidor", errorResponse.message());
        assertEquals("INTERNAL_ERROR", errorResponse.errorCode());
    }

    @Test
    @DisplayName("Deve extrair path corretamente do WebRequest")
    void deveExtrairPathCorretamenteDoWebRequest() {
        // Arrange
        when(webRequest.getDescription(false)).thenReturn("uri=/api/resource/123");
        BusinessException exception = new BusinessException("Erro", "ERR");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDomainException(exception, webRequest);

        // Assert
        ErrorResponse errorResponse = response.getBody();
        assertEquals("/api/resource/123", errorResponse.path());
    }

    @Test
    @DisplayName("Deve usar código de erro da classe quando não fornecido")
    void deveUsarCodigoErroDaClasseQuandoNaoFornecido() {
        // Arrange
        BusinessException exception = new BusinessException("Erro");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDomainException(exception, webRequest);

        // Assert
        ErrorResponse errorResponse = response.getBody();
        assertEquals("BusinessException", errorResponse.errorCode());
    }

    @Test
    @DisplayName("Deve retornar status BAD_REQUEST para DomainException")
    void deveRetornarStatusBadRequestParaDomainException() {
        // Arrange
        BusinessException exception = new BusinessException("Erro");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDomainException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Deve retornar status INTERNAL_SERVER_ERROR para Exception genérica")
    void deveRetornarStatusInternalServerErrorParaExceptionGenerica() {
        // Arrange
        Exception exception = new Exception("Erro");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
