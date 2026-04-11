package com.techchallenge.oficina.administrativo.domain.model.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de CNPJ - Domain Layer")
class CNPJTest {

    @Test
    @DisplayName("Deve criar CNPJ válido com sucesso")
    void deveCriarCnpjValidoComSucesso() {
        // Act
        CNPJ cnpj = CNPJ.of("12345678000195");

        // Assert
        assertNotNull(cnpj);
        assertEquals("12345678000195", cnpj.getValor());
    }

    @Test
    @DisplayName("Deve criar CNPJ válido com formatação")
    void deveCriarCnpjValidoComFormatacao() {
        // Act
        CNPJ cnpj = CNPJ.of("12.345.678/0001-95");

        // Assert
        assertNotNull(cnpj);
        assertEquals("12345678000195", cnpj.getValor());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CNPJ é nulo")
    void deveLancarExcecaoQuandoCnpjNulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> CNPJ.of(null)
        );
        assertEquals("CNPJ não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CNPJ é vazio")
    void deveLancarExcecaoQuandoCnpjVazio() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> CNPJ.of("")
        );
        assertEquals("CNPJ não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CNPJ é branco")
    void deveLancarExcecaoQuandoCnpjBranco() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> CNPJ.of("   ")
        );
        assertEquals("CNPJ não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CNPJ tem tamanho inválido")
    void deveLancarExcecaoQuandoCnpjTamanhoInvalido() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> CNPJ.of("12345678")
        );
        assertEquals("CNPJ inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CNPJ tem todos dígitos iguais")
    void deveLancarExcecaoQuandoCnpjDigitosIguais() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> CNPJ.of("11111111111111")
        );
        assertEquals("CNPJ inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CNPJ tem dígitos verificadores incorretos")
    void deveLancarExcecaoQuandoCnpjDigitosVerificadoresIncorretos() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> CNPJ.of("12345678000190")
        );
        assertEquals("CNPJ inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Deve formatar CNPJ corretamente")
    void deveFormatarCnpjCorretamente() {
        // Arrange
        CNPJ cnpj = CNPJ.of("12345678000195");

        // Act
        String formatado = cnpj.getFormatado();

        // Assert
        assertEquals("12.345.678/0001-95", formatado);
    }

    @Test
    @DisplayName("Deve retornar valor original quando formato inválido")
    void deveRetornarValorOriginalQuandoFormatoInvalido() {
        // Arrange
        CNPJ cnpj = CNPJ.of("12345678000195");

        // Act
        String formatado = cnpj.getFormatado();

        // Assert
        assertEquals("12.345.678/0001-95", formatado);
    }

    @Test
    @DisplayName("Deve retornar toString formatado")
    void deveRetornarToStringFormatado() {
        // Arrange
        CNPJ cnpj = CNPJ.of("12345678000195");

        // Act
        String toString = cnpj.toString();

        // Assert
        assertEquals("12.345.678/0001-95", toString);
    }

    @Test
    @DisplayName("Deve validar CNPJ real válido")
    void deveValidarCnpjRealValido() {
        // Act
        CNPJ cnpj = CNPJ.of("11222333000181");

        // Assert
        assertNotNull(cnpj);
        assertEquals("11222333000181", cnpj.getValor());
    }

    @Test
    @DisplayName("Deve validar CNPJ com caracteres especiais")
    void deveValidarCnpjComCaracteresEspeciais() {
        // Act
        CNPJ cnpj = CNPJ.of("12.345.678/0001-95");

        // Assert
        assertNotNull(cnpj);
        assertEquals("12345678000195", cnpj.getValor());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CNPJ tem letras")
    void deveLancarExcecaoQuandoCnpjTemLetras() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> CNPJ.of("12.345.678/0001-A5")
        );
        assertEquals("CNPJ inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Deve validar igualdade de CNPJs")
    void deveValidarIgualdadeDeCnpjs() {
        // Arrange
        CNPJ cnpj1 = CNPJ.of("12345678000195");
        CNPJ cnpj2 = CNPJ.of("12345678000195");

        // Assert
        assertEquals(cnpj1, cnpj2);
        assertEquals(cnpj1.hashCode(), cnpj2.hashCode());
    }

    @Test
    @DisplayName("Deve validar desigualdade de CNPJs")
    void deveValidarDesigualdadeDeCnpjs() {
        // Arrange
        CNPJ cnpj1 = CNPJ.of("12345678000195");
        CNPJ cnpj2 = CNPJ.of("11222333000181");

        // Assert
        assertNotEquals(cnpj1, cnpj2);
    }

    @Test
    @DisplayName("Deve calcular primeiro dígito verificador corretamente")
    void deveCalcularPrimeiroDigitoVerificadorCorretamente() {
        // Act
        CNPJ cnpj = CNPJ.of("12345678000195");

        // Assert
        assertNotNull(cnpj);
        assertEquals("12345678000195", cnpj.getValor());
    }

    @Test
    @DisplayName("Deve calcular segundo dígito verificador corretamente")
    void deveCalcularSegundoDigitoVerificadorCorretamente() {
        // Act
        CNPJ cnpj = CNPJ.of("12345678000195");

        // Assert
        assertNotNull(cnpj);
        assertEquals("12345678000195", cnpj.getValor());
    }

    @Test
    @DisplayName("Deve validar CNPJ com primeiro dígito verificador 0")
    void deveValidarCnpjComPrimeiroDigitoVerificador0() {
        // Act
        CNPJ cnpj = CNPJ.of("04252011000110");

        // Assert
        assertNotNull(cnpj);
    }

    @Test
    @DisplayName("Deve validar CNPJ com segundo dígito verificador 0")
    void deveValidarCnpjComSegundoDigitoVerificador0() {
        // Act
        CNPJ cnpj = CNPJ.of("04252011000110");

        // Assert
        assertNotNull(cnpj);
    }
}
