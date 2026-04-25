package com.techchallenge.oficina.administrativo.domain.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de ValidacaoVeiculoException - Domain Layer")
class ValidacaoVeiculoExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem")
    void deveCriarExcecaoComMensagem() {
        // Arrange
        String mensagem = "Placa inválida";

        // Act
        ValidacaoVeiculoException exception = new ValidacaoVeiculoException(mensagem);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
        assertEquals("VALIDACAO_VEICULO", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem vazia")
    void deveCriarExcecaoComMensagemVazia() {
        // Arrange
        String mensagem = "";

        // Act
        ValidacaoVeiculoException exception = new ValidacaoVeiculoException(mensagem);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
        assertEquals("VALIDACAO_VEICULO", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve lançar e capturar exceção")
    void deveLancarECapturarExcecao() {
        // Arrange
        String mensagem = "Ano do veículo inválido";

        // Act & Assert
        ValidacaoVeiculoException exception = assertThrows(
                ValidacaoVeiculoException.class,
                () -> {
                    throw new ValidacaoVeiculoException(mensagem);
                }
        );

        assertEquals(mensagem, exception.getMessage());
        assertEquals("VALIDACAO_VEICULO", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve ser do tipo DomainException")
    void deveSerTipoDomainException() {
        // Arrange
        String mensagem = "Modelo é obrigatório";

        // Act
        ValidacaoVeiculoException exception = new ValidacaoVeiculoException(mensagem);

        // Assert
        assertTrue(exception instanceof com.techchallenge.oficina.sharedkernel.domain.exceptions.DomainException);
    }
}
