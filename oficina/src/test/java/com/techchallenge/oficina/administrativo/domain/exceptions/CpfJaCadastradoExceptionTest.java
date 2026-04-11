package com.techchallenge.oficina.administrativo.domain.exceptions;

import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Testes Unitários - CpfJaCadastradoException")
class CpfJaCadastradoExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com CPF e mensagem formatada")
    void deveCriarExcecaoComCpfEMensagem() {
        // Arrange
        CPF cpf = CPF.of("12345678909");
        String mensagemEsperada = "CPF já cadastrado: 123.456.789-09";

        // Act
        CpfJaCadastradoException exception = new CpfJaCadastradoException(cpf);

        // Assert
        assertNotNull(exception);
        assertEquals(mensagemEsperada, exception.getMessage());
        assertEquals(cpf, exception.getCpf());
    }

    @Test
    @DisplayName("Deve manter referência ao CPF após criação")
        void deveManterReferenciaAoCpf() {
        // Arrange
        CPF cpf = CPF.of("98765432100");

        // Act
        CpfJaCadastradoException exception = new CpfJaCadastradoException(cpf);

        // Assert
        assertEquals(cpf, exception.getCpf());
        assertEquals("987.654.321-00", exception.getCpf().getFormatado());
    }
}
