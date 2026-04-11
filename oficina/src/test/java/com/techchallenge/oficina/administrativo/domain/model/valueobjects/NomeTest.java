package com.techchallenge.oficina.administrativo.domain.model.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class NomeTest {

    @Test
    @DisplayName("Deve criar nome válido")
    void deveCriarNomeValido() {
        // Arrange
        String nomeValido = "João Silva";
        
        // Act
        Nome nome = Nome.of(nomeValido);
        
        // Assert
        assertNotNull(nome);
        assertEquals("João Silva", nome.getValor());
    }

    @Test
    @DisplayName("Deve lançar exceção para nome nulo")
    void deveLancarExcecaoParaNomeNulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Nome.of(null)
        );
        
        assertEquals("Nome não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para nome vazio")
    void deveLancarExcecaoParaNomeVazio() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Nome.of("")
        );
        
        assertEquals("Nome não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para nome com menos de 3 caracteres")
    void deveLancarExcecaoParaNomeCurto() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Nome.of("Jo")
        );
        
        assertEquals("Nome deve ter no mínimo 3 caracteres", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para nome com mais de 100 caracteres")
    void deveLancarExcecaoParaNomeLongo() {
        // Arrange
        String nomeLongo = "A".repeat(101);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Nome.of(nomeLongo)
        );
        
        assertEquals("Nome deve ter no máximo 100 caracteres", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para nome com números")
    void deveLancarExcecaoParaNomeComNumeros() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Nome.of("João123")
        );
        
        assertEquals("Nome deve conter apenas letras e espaços", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para nome com caracteres especiais")
    void deveLancarExcecaoParaNomeComCaracteresEspeciais() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Nome.of("João@Silva")
        );
        
        assertEquals("Nome deve conter apenas letras e espaços", exception.getMessage());
    }

    @Test
    @DisplayName("Deve remover espaços em branco")
    void deveRemoverEspacosEmBranco() {
        // Arrange
        String nomeComEspacos = "  João Silva  ";
        
        // Act
        Nome nome = Nome.of(nomeComEspacos);
        
        // Assert
        assertEquals("João Silva", nome.getValor());
    }

    @Test
    @DisplayName("Deve retornar primeiro nome corretamente")
    void deveRetornarPrimeiroNomeCorretamente() {
        // Arrange
        Nome nome = Nome.of("João da Silva Santos");
        
        // Act & Assert
        assertEquals("João", nome.getPrimeiroNome());
    }

    @Test
    @DisplayName("Deve retornar sobrenome corretamente")
    void deveRetornarSobrenomeCorretamente() {
        // Arrange
        Nome nome = Nome.of("João da Silva Santos");
        
        // Act & Assert
        assertEquals("da Silva Santos", nome.getSobrenome());
    }

    @Test
    @DisplayName("Deve retornar sobrenome vazio para nome único")
    void deveRetornarSobrenomeVazioParaNomeUnico() {
        // Arrange
        Nome nome = Nome.of("João");
        
        // Act & Assert
        assertEquals("", nome.getSobrenome());
    }

    @Test
    @DisplayName("Deve implementar equals e hashCode corretamente")
    void deveImplementarEqualsAndHashCodeCorretamente() {
        // Arrange
        Nome nome1 = Nome.of("João Silva");
        Nome nome2 = Nome.of("João Silva");
        Nome nome3 = Nome.of("Maria Santos");
        
        // Assert
        assertEquals(nome1, nome2);
        assertEquals(nome1.hashCode(), nome2.hashCode());
        assertNotEquals(nome1, nome3);
        assertNotEquals(nome1.hashCode(), nome3.hashCode());
    }
}
