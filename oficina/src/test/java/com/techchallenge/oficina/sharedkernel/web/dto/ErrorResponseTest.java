package com.techchallenge.oficina.sharedkernel.web.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - ErrorResponse")
class ErrorResponseTest {

    @Test
    @DisplayName("Deve criar ErrorResponse usando método estático of sem errors")
    void deveCriarErrorResponseUsandoMetodoEstaticoOfSemErrors() {
        // Act
        ErrorResponse response = ErrorResponse.of(
                400,
                "Bad Request",
                "Erro de validação",
                "VALIDATION_ERROR",
                "/api/test"
        );

        // Assert
        assertNotNull(response);
        assertEquals(400, response.status());
        assertEquals("Bad Request", response.error());
        assertEquals("Erro de validação", response.message());
        assertEquals("VALIDATION_ERROR", response.errorCode());
        assertEquals("/api/test", response.path());
        assertNull(response.errors());
        assertNotNull(response.timestamp());
    }

    @Test
    @DisplayName("Deve criar ErrorResponse usando método estático of com errors")
    void deveCriarErrorResponseUsandoMetodoEstaticoOfComErrors() {
        // Arrange
        List<FieldError> fieldErrors = List.of(
                new FieldError("nome", "Nome é obrigatório"),
                new FieldError("email", "Email inválido")
        );

        // Act
        ErrorResponse response = ErrorResponse.of(
                400,
                "Bad Request",
                "Erro de validação",
                "VALIDATION_ERROR",
                "/api/test",
                fieldErrors
        );

        // Assert
        assertNotNull(response);
        assertEquals(400, response.status());
        assertEquals("Bad Request", response.error());
        assertEquals("Erro de validação", response.message());
        assertEquals("VALIDATION_ERROR", response.errorCode());
        assertEquals("/api/test", response.path());
        assertNotNull(response.errors());
        assertEquals(2, response.errors().size());
        assertNotNull(response.timestamp());
    }

    @Test
    @DisplayName("Deve criar ErrorResponse usando construtor")
    void deveCriarErrorResponseUsandoConstrutor() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.now();
        List<FieldError> fieldErrors = List.of(new FieldError("field", "error"));

        // Act
        ErrorResponse response = new ErrorResponse(
                timestamp,
                500,
                "Internal Server Error",
                "Erro interno",
                "INTERNAL_ERROR",
                "/api/error",
                fieldErrors
        );

        // Assert
        assertNotNull(response);
        assertEquals(timestamp, response.timestamp());
        assertEquals(500, response.status());
        assertEquals("Internal Server Error", response.error());
        assertEquals("Erro interno", response.message());
        assertEquals("INTERNAL_ERROR", response.errorCode());
        assertEquals("/api/error", response.path());
        assertEquals(fieldErrors, response.errors());
    }

    @Test
    @DisplayName("Deve criar ErrorResponse com timestamp atual usando método of")
    void deveCriarErrorResponseComTimestampAtualUsandoMetodoOf() {
        // Act
        ErrorResponse response = ErrorResponse.of(
                404,
                "Not Found",
                "Recurso não encontrado",
                "NOT_FOUND",
                "/api/resource/1"
        );

        // Assert
        assertNotNull(response.timestamp());
        assertTrue(response.timestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(response.timestamp().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    @DisplayName("Deve criar ErrorResponse com errors vazios")
    void deveCriarErrorResponseComErrorsVazios() {
        // Arrange
        List<FieldError> emptyErrors = List.of();

        // Act
        ErrorResponse response = ErrorResponse.of(
                400,
                "Bad Request",
                "Erro",
                "ERROR_CODE",
                "/api/test",
                emptyErrors
        );

        // Assert
        assertNotNull(response.errors());
        assertTrue(response.errors().isEmpty());
    }

    @Test
    @DisplayName("Deve criar ErrorResponse com código de erro nulo")
    void deveCriarErrorResponseComCodigoErroNulo() {
        // Act
        ErrorResponse response = ErrorResponse.of(
                400,
                "Bad Request",
                "Erro",
                null,
                "/api/test"
        );

        // Assert
        assertNull(response.errorCode());
    }

    @Test
    @DisplayName("Deve criar ErrorResponse com path vazio")
    void deveCriarErrorResponseComPathVazio() {
        // Act
        ErrorResponse response = ErrorResponse.of(
                400,
                "Bad Request",
                "Erro",
                "ERROR_CODE",
                ""
        );

        // Assert
        assertEquals("", response.path());
    }

    @Test
    @DisplayName("Deve criar ErrorResponse com status zero")
    void deveCriarErrorResponseComStatusZero() {
        // Act
        ErrorResponse response = ErrorResponse.of(
                0,
                "Unknown",
                "Erro desconhecido",
                "UNKNOWN_ERROR",
                "/api/test"
        );

        // Assert
        assertEquals(0, response.status());
    }

    @Test
    @DisplayName("Deve criar ErrorResponse com message vazia")
    void deveCriarErrorResponseComMessageVazia() {
        // Act
        ErrorResponse response = ErrorResponse.of(
                400,
                "Bad Request",
                "",
                "ERROR_CODE",
                "/api/test"
        );

        // Assert
        assertEquals("", response.message());
    }

    @Test
    @DisplayName("Deve criar ErrorResponse com error description vazia")
    void deveCriarErrorResponseComErrorDescriptionVazia() {
        // Act
        ErrorResponse response = ErrorResponse.of(
                400,
                "",
                "Erro",
                "ERROR_CODE",
                "/api/test"
        );

        // Assert
        assertEquals("", response.error());
    }
}
