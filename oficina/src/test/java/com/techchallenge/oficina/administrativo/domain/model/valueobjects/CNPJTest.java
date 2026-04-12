package com.techchallenge.oficina.administrativo.domain.model.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de CNPJ - Domain Layer")
class CNPJTest {

    @ParameterizedTest
    @DisplayName("Deve criar CNPJ válido com sucesso")
    @MethodSource("provideValidCnpjs")
    void deveCriarCnpjValidoComSucesso(String cnpjInput, String expectedValue) {
        // Act
        CNPJ cnpj = CNPJ.of(cnpjInput);

        // Assert
        assertNotNull(cnpj);
        assertEquals(expectedValue, cnpj.getValor());
    }

    private static Stream<Arguments> provideValidCnpjs() {
        return Stream.of(
                Arguments.of("12345678000195", "12345678000195"),
                Arguments.of("12.345.678/0001-95", "12345678000195"),
                Arguments.of("11222333000181", "11222333000181")
        );
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
    @DisplayName("Deve validar CNPJ com primeiro dígito verificador 0")
    void deveValidarCnpjComPrimeiroDigitoVerificador0() {
        // Act
        CNPJ cnpj = CNPJ.of("04252011000110");

        // Assert
        assertNotNull(cnpj);
    }
}
