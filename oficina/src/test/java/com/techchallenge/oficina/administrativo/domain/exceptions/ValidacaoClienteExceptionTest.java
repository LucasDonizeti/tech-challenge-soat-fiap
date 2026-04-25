package com.techchallenge.oficina.administrativo.domain.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de ValidacaoClienteException - Domain Layer")
class ValidacaoClienteExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem")
    void deveCriarExcecaoComMensagem() {
        // Arrange
        String mensagem = "Nome é obrigatório";

        // Act
        ValidacaoClienteException exception = new ValidacaoClienteException(mensagem);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
        assertEquals("VALIDACAO_CLIENTE", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem vazia")
    void deveCriarExcecaoComMensagemVazia() {
        // Arrange
        String mensagem = "";

        // Act
        ValidacaoClienteException exception = new ValidacaoClienteException(mensagem);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
        assertEquals("VALIDACAO_CLIENTE", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve lançar e capturar exceção")
    void deveLancarECapturarExcecao() {
        // Arrange
        String mensagem = "Email inválido";

        // Act & Assert
        ValidacaoClienteException exception = assertThrows(
                ValidacaoClienteException.class,
                () -> {
                    throw new ValidacaoClienteException(mensagem);
                }
        );

        assertEquals(mensagem, exception.getMessage());
        assertEquals("VALIDACAO_CLIENTE", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve ser do tipo DomainException")
    void deveSerTipoDomainException() {
        // Arrange
        String mensagem = "CPF inválido";

        // Act
        ValidacaoClienteException exception = new ValidacaoClienteException(mensagem);

        // Assert
        assertTrue(exception instanceof com.techchallenge.oficina.sharedkernel.domain.exceptions.DomainException);
    }
}
