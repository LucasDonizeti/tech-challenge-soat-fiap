package com.techchallenge.oficina.sharedkernel.web.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - FieldError")
class FieldErrorTest {

    @Test
    @DisplayName("Deve criar FieldError com campo e mensagem")
    void deveCriarFieldErrorComCampoEMensagem() {
        // Act
        FieldError fieldError = new FieldError("nome", "Nome é obrigatório");

        // Assert
        assertNotNull(fieldError);
        assertEquals("nome", fieldError.field());
        assertEquals("Nome é obrigatório", fieldError.message());
    }

    @Test
    @DisplayName("Deve criar FieldError com campo vazio")
    void deveCriarFieldErrorComCampoVazio() {
        // Act
        FieldError fieldError = new FieldError("", "Mensagem de erro");

        // Assert
        assertEquals("", fieldError.field());
        assertEquals("Mensagem de erro", fieldError.message());
    }

    @Test
    @DisplayName("Deve criar FieldError com mensagem vazia")
    void deveCriarFieldErrorComMensagemVazia() {
        // Act
        FieldError fieldError = new FieldError("campo", "");

        // Assert
        assertEquals("campo", fieldError.field());
        assertEquals("", fieldError.message());
    }

    @Test
    @DisplayName("Deve criar FieldError com campo e mensagem nulos")
    void deveCriarFieldErrorComCampoEMensagemNulos() {
        // Act
        FieldError fieldError = new FieldError(null, null);

        // Assert
        assertNull(fieldError.field());
        assertNull(fieldError.message());
    }

    @Test
    @DisplayName("Deve criar FieldError com campo contendo espaços")
    void deveCriarFieldErrorComCampoContendoEspacos() {
        // Act
        FieldError fieldError = new FieldError("nome completo", "Nome completo é obrigatório");

        // Assert
        assertEquals("nome completo", fieldError.field());
        assertEquals("Nome completo é obrigatório", fieldError.message());
    }

    @Test
    @DisplayName("Deve criar FieldError com mensagem longa")
    void deveCriarFieldErrorComMensagemLonga() {
        // Arrange
        String longMessage = "Esta é uma mensagem de erro muito longa que descreve em detalhes o problema ocorrido no campo do formulário de validação.";

        // Act
        FieldError fieldError = new FieldError("campo", longMessage);

        // Assert
        assertEquals("campo", fieldError.field());
        assertEquals(longMessage, fieldError.message());
    }

    @Test
    @DisplayName("Deve criar FieldError com caracteres especiais")
    void deveCriarFieldErrorComCaracteresEspeciais() {
        // Act
        FieldError fieldError = new FieldError("email", "Email deve conter @ e .");

        // Assert
        assertEquals("email", fieldError.field());
        assertEquals("Email deve conter @ e .", fieldError.message());
    }

    @Test
    @DisplayName("Deve criar FieldError com números no campo")
    void deveCriarFieldErrorComNumerosNoCampo() {
        // Act
        FieldError fieldError = new FieldError("campo123", "Erro no campo 123");

        // Assert
        assertEquals("campo123", fieldError.field());
        assertEquals("Erro no campo 123", fieldError.message());
    }

    @Test
    @DisplayName("Deve criar FieldError com underscore no campo")
    void deveCriarFieldErrorComUnderscoreNoCampo() {
        // Act
        FieldError fieldError = new FieldError("nome_completo", "Erro no campo");

        // Assert
        assertEquals("nome_completo", fieldError.field());
        assertEquals("Erro no campo", fieldError.message());
    }

    @Test
    @DisplayName("Deve criar FieldError com camelCase no campo")
    void deveCriarFieldErrorComCamelCaseNoCampo() {
        // Act
        FieldError fieldError = new FieldError("nomeCompleto", "Erro no campo");

        // Assert
        assertEquals("nomeCompleto", fieldError.field());
        assertEquals("Erro no campo", fieldError.message());
    }
}
