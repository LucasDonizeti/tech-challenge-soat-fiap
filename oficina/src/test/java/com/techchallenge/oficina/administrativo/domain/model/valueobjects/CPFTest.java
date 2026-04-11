package com.techchallenge.oficina.administrativo.domain.model.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class CPFTest {

    @Test
    @DisplayName("Deve criar CPF válido")
    void deveCriarCpfValido() {
        // Arrange
        String cpfValido = "12345678909";
        
        // Act
        CPF cpf = CPF.of(cpfValido);
        
        // Assert
        assertNotNull(cpf);
        assertEquals("12345678909", cpf.getValor());
        assertEquals("123.456.789-09", cpf.getFormatado());
    }

    @Test
    @DisplayName("Deve lançar exceção para CPF nulo")
    void deveLancarExcecaoParaCpfNulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> CPF.of(null)
        );
        
        assertEquals("CPF não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para CPF vazio")
    void deveLancarExcecaoParaCpfVazio() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> CPF.of("")
        );
        
        assertEquals("CPF não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para CPF com tamanho inválido")
    void deveLancarExcecaoParaCpfTamanhoInvalido() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> CPF.of("123456789")
        );
        
        assertEquals("CPF inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para CPF com dígitos iguais")
    void deveLancarExcecaoParaCpfDigitosIguais() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> CPF.of("11111111111")
        );
        
        assertEquals("CPF inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para CPF com checksum inválido")
    void deveLancarExcecaoParaCpfChecksumInvalido() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> CPF.of("12345678900")
        );
        
        assertEquals("CPF inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Deve formatar CPF corretamente")
    void deveFormatarCpfCorretamente() {
        // Arrange
        String cpfValido = "12345678909";
        
        // Act
        CPF cpf = CPF.of(cpfValido);
        
        // Assert
        assertEquals("123.456.789-09", cpf.getFormatado());
        assertEquals("123.456.789-09", cpf.toString());
    }

    @Test
    @DisplayName("Deve remover formatação ao criar CPF")
    void deveRemoverFormatacaoAoCriarCpf() {
        // Arrange
        String cpfComFormatacao = "123.456.789-09";
        
        // Act
        CPF cpf = CPF.of(cpfComFormatacao);
        
        // Assert
        assertEquals("12345678909", cpf.getValor());
        assertEquals("123.456.789-09", cpf.getFormatado());
    }

    @Test
    @DisplayName("Deve implementar equals e hashCode corretamente")
    void deveImplementarEqualsAndHashCodeCorretamente() {
        // Arrange
        CPF cpf1 = CPF.of("12345678909");
        CPF cpf2 = CPF.of("12345678909");
        CPF cpf3 = CPF.of("98765432100");
        
        // Assert
        assertEquals(cpf1, cpf2);
        assertEquals(cpf1.hashCode(), cpf2.hashCode());
        assertNotEquals(cpf1, cpf3);
        assertNotEquals(cpf1.hashCode(), cpf3.hashCode());
    }
}
