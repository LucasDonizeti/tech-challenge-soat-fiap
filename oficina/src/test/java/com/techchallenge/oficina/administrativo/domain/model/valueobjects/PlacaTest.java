package com.techchallenge.oficina.administrativo.domain.model.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class PlacaTest {

    @Test
    @DisplayName("Deve criar placa no formato Mercosul válido")
    void deveCriarPlacaFormatoMercosulValido() {
        // Arrange
        String placaMercosul = "ABC1D23";
        
        // Act
        Placa placa = Placa.of(placaMercosul);
        
        // Assert
        assertNotNull(placa);
        assertEquals("ABC1D23", placa.getValor());
        assertEquals("ABC-1D23", placa.getFormatada());
        assertTrue(placa.isMercosul());
        assertFalse(placa.isFormatoAntigo());
    }

    @Test
    @DisplayName("Deve criar placa no formato antigo válido")
    void deveCriarPlacaFormatoAntigoValido() {
        // Arrange
        String placaAntiga = "ABC1234";
        
        // Act
        Placa placa = Placa.of(placaAntiga);
        
        // Assert
        assertNotNull(placa);
        assertEquals("ABC1234", placa.getValor());
        assertEquals("ABC-1234", placa.getFormatada());
        assertFalse(placa.isMercosul());
        assertTrue(placa.isFormatoAntigo());
    }

    @Test
    @DisplayName("Deve lançar exceção para placa nula")
    void deveLancarExcecaoParaPlacaNula() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Placa.of(null)
        );
        
        assertEquals("Placa não pode ser nula ou vazia", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para placa vazia")
    void deveLancarExcecaoParaPlacaVazia() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Placa.of("")
        );
        
        assertEquals("Placa não pode ser nula ou vazia", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para placa em branco")
    void deveLancarExcecaoParaPlacaEmBranco() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Placa.of("   ")
        );
        
        assertEquals("Placa não pode ser nula ou vazia", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para placa com tamanho inválido")
    void deveLancarExcecaoParaPlacaTamanhoInvalido() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Placa.of("ABC12")
        );
        
        assertEquals("Placa inválida: ABC12", exception.getMessage());
    }

    @Test
    @DisplayName("Deve limpar caracteres especiais da placa")
    void deveLimparCaracteresEspeciaisDaPlaca() {
        // Arrange
        String placaComFormatacao = "ABC-1D23";
        
        // Act
        Placa placa = Placa.of(placaComFormatacao);
        
        // Assert
        assertEquals("ABC1D23", placa.getValor());
    }


    @Test
    @DisplayName("Deve formatar placa Mercosul corretamente")
    void deveFormatarPlacaMercosulCorretamente() {
        // Arrange
        Placa placa = Placa.of("ABC1D23");
        
        // Act & Assert
        assertEquals("ABC-1D23", placa.getFormatada());
    }

    @Test
    @DisplayName("Deve formatar placa antiga corretamente")
    void deveFormatarPlacaAntigaCorretamente() {
        // Arrange
        Placa placa = Placa.of("ABC1234");
        
        // Act & Assert
        assertEquals("ABC-1234", placa.getFormatada());
    }

    @Test
    @DisplayName("Deve identificar placa Mercosul")
    void deveIdentificarPlacaMercosul() {
        // Arrange
        Placa placa = Placa.of("ABC1D23");
        
        // Act & Assert
        assertTrue(placa.isMercosul());
        assertFalse(placa.isFormatoAntigo());
    }

    @Test
    @DisplayName("Deve identificar placa formato antigo")
    void deveIdentificarPlacaFormatoAntigo() {
        // Arrange
        Placa placa = Placa.of("ABC1234");
        
        // Act & Assert
        assertTrue(placa.isFormatoAntigo());
        assertFalse(placa.isMercosul());
    }

    @Test
    @DisplayName("Deve retornar valor formatado para placa válida")
    void deveRetornarValorFormatadoParaPlacaValida() {
        // Arrange
        Placa placaValida = Placa.of("ABC1D23");
        
        // Act & Assert
        assertEquals("ABC-1D23", placaValida.getFormatada());
    }

    @Test
    @DisplayName("Deve implementar equals e hashCode corretamente")
    void deveImplementarEqualsAndHashCodeCorretamente() {
        // Arrange
        Placa placa1 = Placa.of("ABC1D23");
        Placa placa2 = Placa.of("ABC1D23");
        Placa placa3 = Placa.of("XYZ9W87");
        
        // Assert
        assertEquals(placa1, placa2);
        assertEquals(placa1.hashCode(), placa2.hashCode());
        assertNotEquals(placa1, placa3);
        assertNotEquals(placa1.hashCode(), placa3.hashCode());
    }

    @Test
    @DisplayName("Deve retornar toString com formato")
    void deveRetornarToStringComFormato() {
        // Arrange
        Placa placa = Placa.of("ABC1D23");
        
        // Act & Assert
        assertEquals("ABC-1D23", placa.toString());
    }
}
