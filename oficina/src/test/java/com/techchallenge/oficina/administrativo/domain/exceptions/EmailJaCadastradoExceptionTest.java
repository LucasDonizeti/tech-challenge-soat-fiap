package com.techchallenge.oficina.administrativo.domain.exceptions;

import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Testes Unitários - EmailJaCadastradoException")
class EmailJaCadastradoExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com Email e mensagem formatada")
    void deveCriarExcecaoComEmailEMensagem() {
        // Arrange
        Email email = Email.of("teste@exemplo.com");
        String mensagemEsperada = "Email já cadastrado: teste@exemplo.com";

        // Act
        EmailJaCadastradoException exception = new EmailJaCadastradoException(email);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagemEsperada, exception.getMessage());
        assertEquals(email, exception.getEmail());
    }

    @Test
    @DisplayName("Deve manter referência ao Email após criação")
    void deveManterReferenciaAoEmail() {
        // Arrange
        Email email = Email.of("empresa@dominio.com.br");

        // Act
        EmailJaCadastradoException exception = new EmailJaCadastradoException(email);

        // Assert
        assertEquals(email, exception.getEmail());
        assertEquals("empresa@dominio.com.br", exception.getEmail().getEndereco());
    }
}
