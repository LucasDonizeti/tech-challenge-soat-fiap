package com.techchallenge.oficina.administrativo.domain.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de ValidacaoValorException - Domain Layer")
class ValidacaoValorExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem")
    void deveCriarExcecaoComMensagem() {
        // Arrange
        String mensagem = "Valor não pode ser negativo";

        // Act
        ValidacaoValorException exception = new ValidacaoValorException(mensagem);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
        assertEquals("VALIDACAO_VALOR", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem vazia")
    void deveCriarExcecaoComMensagemVazia() {
        // Arrange
        String mensagem = "";

        // Act
        ValidacaoValorException exception = new ValidacaoValorException(mensagem);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
        assertEquals("VALIDACAO_VALOR", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve lançar e capturar exceção")
    void deveLancarECapturarExcecao() {
        // Arrange
        String mensagem = "Valor inválido";

        // Act & Assert
        ValidacaoValorException exception = assertThrows(
                ValidacaoValorException.class,
                () -> {
                    throw new ValidacaoValorException(mensagem);
                }
        );

        assertEquals(mensagem, exception.getMessage());
        assertEquals("VALIDACAO_VALOR", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve ser do tipo DomainException")
    void deveSerTipoDomainException() {
        // Arrange
        String mensagem = "Valor fora do intervalo permitido";

        // Act
        ValidacaoValorException exception = new ValidacaoValorException(mensagem);

        // Assert
        assertTrue(exception instanceof com.techchallenge.oficina.sharedkernel.domain.exceptions.DomainException);
    }
}
