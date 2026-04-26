package com.techchallenge.oficina.administrativo.domain.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - ValidacaoServicoException")
class ValidacaoServicoExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem")
    void deveCriarExcecaoComMensagem() {
        // Arrange
        String mensagem = "Preço deve ser maior que zero";

        // Act
        ValidacaoServicoException exception = new ValidacaoServicoException(mensagem);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem e causa")
    void deveCriarExcecaoComMensagemECausa() {
        // Arrange
        String mensagem = "Erro ao validar serviço";
        Throwable causa = new RuntimeException("Erro interno");

        // Act
        ValidacaoServicoException exception = new ValidacaoServicoException(mensagem, causa);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
        assertEquals(causa, exception.getCause());
    }

    @Test
    @DisplayName("Deve ser do tipo RuntimeException")
    void deveSerDoTipoRuntimeException() {
        // Arrange
        String mensagem = "Erro de validação";

        // Act
        ValidacaoServicoException exception = new ValidacaoServicoException(mensagem);

        // Assert
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Deve manter mensagem vazia quando fornecida")
    void deveManterMensagemVaziaQuandoFornecida() {
        // Arrange
        String mensagem = "";

        // Act
        ValidacaoServicoException exception = new ValidacaoServicoException(mensagem);

        // Assert
        assertNotNull(exception);
        assertEquals("", exception.getMessage());
    }

    @Test
    @DisplayName("Deve manter causa null quando não fornecida")
    void deveManterCausaNullQuandoNaoFornecida() {
        // Arrange
        String mensagem = "Erro de validação";

        // Act
        ValidacaoServicoException exception = new ValidacaoServicoException(mensagem);

        // Assert
        assertNotNull(exception);
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve manter mensagem quando causa é fornecida")
    void deveManterMensagemQuandoCausaEFornecida() {
        // Arrange
        String mensagem = "Erro ao validar preço";
        Throwable causa = new IllegalArgumentException("Preço inválido");

        // Act
        ValidacaoServicoException exception = new ValidacaoServicoException(mensagem, causa);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
        assertEquals(causa, exception.getCause());
    }

    @Test
    @DisplayName("Deve permitir lançar exceção com try-catch")
    void devePermitirLancarExcecaoComTryCatch() {
        // Arrange
        String mensagem = "Serviço inválido";

        // Act & Assert
        assertThrows(ValidacaoServicoException.class, () -> {
            throw new ValidacaoServicoException(mensagem);
        });
    }

    @Test
    @DisplayName("Deve permitir capturar mensagem em catch")
    void devePermitirCapturarMensagemEmCatch() {
        // Arrange
        String mensagem = "Nome é obrigatório";

        // Act
        try {
            throw new ValidacaoServicoException(mensagem);
        } catch (ValidacaoServicoException e) {
            // Assert
            assertEquals(mensagem, e.getMessage());
        }
    }

    @Test
    @DisplayName("Deve manter stack trace da causa")
    void deveManterStackTraceDaCausa() {
        // Arrange
        String mensagem = "Erro ao processar";
        Throwable causa = new RuntimeException("Erro interno");

        // Act
        ValidacaoServicoException exception = new ValidacaoServicoException(mensagem, causa);

        // Assert
        assertNotNull(exception.getStackTrace());
        assertNotNull(exception.getCause().getStackTrace());
    }

    @Test
    @DisplayName("Deve ser possível criar exceção com mensagem longa")
    void deveSerPossivelCriarExcecaoComMensagemLonga() {
        // Arrange
        String mensagemLonga = "Esta é uma mensagem de erro muito longa que descreve detalhadamente o problema encontrado ao validar o serviço no sistema";

        // Act
        ValidacaoServicoException exception = new ValidacaoServicoException(mensagemLonga);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagemLonga, exception.getMessage());
    }

    @Test
    @DisplayName("Deve ser possível criar exceção com caracteres especiais na mensagem")
    void deveSerPossivelCriarExcecaoComCaracteresEspeciaisNaMensagem() {
        // Arrange
        String mensagem = "Erro: preço inválido! (R$ 0,00)";

        // Act
        ValidacaoServicoException exception = new ValidacaoServicoException(mensagem);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
    }
}
