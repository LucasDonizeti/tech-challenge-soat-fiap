package com.techchallenge.oficina.administrativo.domain.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - ValidacaoMROException")
class ValidacaoMROExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem")
    void deveCriarExcecaoComMensagem() {
        // Arrange
        String mensagem = "Nome é obrigatório";

        // Act
        ValidacaoMROException exception = new ValidacaoMROException(mensagem);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
        assertEquals("VALIDACAO_MRO", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem e causa")
    void deveCriarExcecaoComMensagemECausa() {
        // Arrange
        String mensagem = "Erro ao validar MRO";
        Throwable causa = new RuntimeException("Erro interno");

        // Act
        ValidacaoMROException exception = new ValidacaoMROException(mensagem, causa);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
        assertEquals("VALIDACAO_MRO", exception.getErrorCode());
        assertEquals(causa, exception.getCause());
    }

    @Test
    @DisplayName("Deve herdar de DomainException")
    void deveHerdarDeDomainException() {
        // Arrange
        String mensagem = "Preço inválido";

        // Act
        ValidacaoMROException exception = new ValidacaoMROException(mensagem);

        // Assert
        assertTrue(exception instanceof com.techchallenge.oficina.sharedkernel.domain.exceptions.DomainException);
    }

    @Test
    @DisplayName("Deve manter código de erro consistente")
    void deveManterCodigoDeErroConsistente() {
        // Arrange
        String mensagem1 = "Erro 1";
        String mensagem2 = "Erro 2";

        // Act
        ValidacaoMROException exception1 = new ValidacaoMROException(mensagem1);
        ValidacaoMROException exception2 = new ValidacaoMROException(mensagem2);

        // Assert
        assertEquals("VALIDACAO_MRO", exception1.getErrorCode());
        assertEquals("VALIDACAO_MRO", exception2.getErrorCode());
    }

    @Test
    @DisplayName("Deve permitir mensagem vazia")
    void devePermitirMensagemVazia() {
        // Arrange
        String mensagem = "";

        // Act
        ValidacaoMROException exception = new ValidacaoMROException(mensagem);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
    }

    @Test
    @DisplayName("Deve propagar causa corretamente")
    void devePropagarCausaCorretamente() {
        // Arrange
        String mensagem = "Erro de validação";
        Throwable causa = new IllegalArgumentException("Argumento inválido");

        // Act
        ValidacaoMROException exception = new ValidacaoMROException(mensagem, causa);

        // Assert
        assertNotNull(exception.getCause());
        assertEquals(causa, exception.getCause());
        assertEquals("Argumento inválido", exception.getCause().getMessage());
    }
}
