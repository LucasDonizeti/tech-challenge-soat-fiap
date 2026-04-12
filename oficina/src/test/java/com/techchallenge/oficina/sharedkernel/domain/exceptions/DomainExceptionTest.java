package com.techchallenge.oficina.sharedkernel.domain.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - DomainException")
class DomainExceptionTest {

    @Test
    @DisplayName("Deve criar BusinessException com mensagem")
    void deveCriarBusinessExceptionComMensagem() {
        // Act
        BusinessException exception = new BusinessException("Erro de negócio");

        // Assert
        assertNotNull(exception);
        assertEquals("Erro de negócio", exception.getMessage());
        assertEquals("BusinessException", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve criar BusinessException com mensagem e código de erro")
    void deveCriarBusinessExceptionComMensagemECodigoErro() {
        // Act
        BusinessException exception = new BusinessException("Erro de negócio", "BIZ_ERR_001");

        // Assert
        assertNotNull(exception);
        assertEquals("Erro de negócio", exception.getMessage());
        assertEquals("BIZ_ERR_001", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve criar ValorInvalidoException com mensagem")
    void deveCriarValorInvalidoExceptionComMensagem() {
        // Act
        ValorInvalidoException exception = new ValorInvalidoException("Valor inválido");

        // Assert
        assertNotNull(exception);
        assertEquals("Valor inválido", exception.getMessage());
        assertEquals("ValorInvalidoException", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve criar ValorInvalidoException com mensagem e código de erro")
    void deveCriarValorInvalidoExceptionComMensagemECodigoErro() {
        // Act
        ValorInvalidoException exception = new ValorInvalidoException("Valor inválido", "VAL_ERR_001");

        // Assert
        assertNotNull(exception);
        assertEquals("Valor inválido", exception.getMessage());
        assertEquals("VAL_ERR_001", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve criar subclasse concreta de DomainException")
    void deveCriarSubclasseConcretaDeDomainException() {
        // Arrange & Act
        class CustomDomainException extends DomainException {
            public CustomDomainException(String message) {
                super(message);
            }

            public CustomDomainException(String message, String errorCode) {
                super(message, errorCode);
            }
        }

        CustomDomainException exception1 = new CustomDomainException("Erro customizado");
        CustomDomainException exception2 = new CustomDomainException("Erro customizado", "CUSTOM_ERR");

        // Assert
        assertNotNull(exception1);
        assertEquals("Erro customizado", exception1.getMessage());
        assertEquals("CustomDomainException", exception1.getErrorCode());

        assertNotNull(exception2);
        assertEquals("Erro customizado", exception2.getMessage());
        assertEquals("CUSTOM_ERR", exception2.getErrorCode());
    }

    @Test
    @DisplayName("Deve usar nome da classe como código de erro quando não fornecido")
    void deveUsarNomeDaClasseComoCodigoErroQuandoNaoFornecido() {
        // Act
        BusinessException exception = new BusinessException("Erro");

        // Assert
        assertEquals("BusinessException", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve lançar DomainException como RuntimeException")
    void deveLancarDomainExceptionComoRuntimeException() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            throw new BusinessException("Erro de negócio");
        });
    }

    @Test
    @DisplayName("Deve manter código de erro fornecido mesmo que seja nulo")
    void deveManterCodigoErroFornecidoMesmoQueSejaNulo() {
        // Act
        BusinessException exception = new BusinessException("Erro", null);

        // Assert
        assertNotNull(exception);
        assertEquals("BusinessException", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve criar BusinessException com código de erro customizado")
    void deveCriarBusinessExceptionComCodigoErroCustomizado() {
        // Act
        BusinessException exception = new BusinessException("Erro", "CUSTOM_CODE");

        // Assert
        assertEquals("CUSTOM_CODE", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve criar ValorInvalidoException com código de erro customizado")
    void deveCriarValorInvalidoExceptionComCodigoErroCustomizado() {
        // Act
        ValorInvalidoException exception = new ValorInvalidoException("Erro", "VAL_CODE");

        // Assert
        assertEquals("VAL_CODE", exception.getErrorCode());
    }

    @Test
    @DisplayName("Deve lançar BusinessException e capturar mensagem")
    void deveLancarBusinessExceptionECapturarMensagem() {
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            throw new BusinessException("Erro de negócio");
        });

        assertEquals("Erro de negócio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar ValorInvalidoException e capturar código de erro")
    void deveLancarValorInvalidoExceptionECapturarCodigoErro() {
        // Act & Assert
        ValorInvalidoException exception = assertThrows(ValorInvalidoException.class, () -> {
            throw new ValorInvalidoException("Valor inválido", "VAL_ERR");
        });

        assertEquals("VAL_ERR", exception.getErrorCode());
    }
}
