package com.techchallenge.oficina.administrativo.domain.exceptions;

import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CNPJ;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Testes Unitários - CnpjJaCadastradoException")
class CnpjJaCadastradoExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com CNPJ e mensagem formatada")
    void deveCriarExcecaoComCnpjEMensagem() {
        // Arrange
        CNPJ cnpj = CNPJ.of("11222333000181");
        String mensagemEsperada = "CNPJ já cadastrado: 11.222.333/0001-81";

        // Act
        CnpjJaCadastradoException exception = new CnpjJaCadastradoException(cnpj);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagemEsperada, exception.getMessage());
        assertEquals(cnpj, exception.getCnpj());
    }

    @Test
    @DisplayName("Deve manter referência ao CNPJ após criação")
    void deveManterReferenciaAoCnpj() {
        // Arrange
        CNPJ cnpj = CNPJ.of("12345678000195");

        // Act
        CnpjJaCadastradoException exception = new CnpjJaCadastradoException(cnpj);

        // Assert
        assertEquals(cnpj, exception.getCnpj());
        assertEquals("12.345.678/0001-95", exception.getCnpj().getFormatado());
    }
}
