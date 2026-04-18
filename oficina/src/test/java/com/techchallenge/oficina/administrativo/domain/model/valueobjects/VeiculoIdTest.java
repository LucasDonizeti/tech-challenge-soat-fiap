package com.techchallenge.oficina.administrativo.domain.model.valueobjects;

import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoValorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class VeiculoIdTest {

    @Test
    @DisplayName("Deve criar VeiculoId com UUID válido")
    void deveCriarVeiculoIdComUuidValido() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        
        // Act
        VeiculoId veiculoId = VeiculoId.of(uuid);
        
        // Assert
        assertNotNull(veiculoId);
        assertEquals(uuid, veiculoId.getValue());
        assertEquals(uuid.toString(), veiculoId.toString());
    }

    @Test
    @DisplayName("Deve lançar exceção para UUID nulo")
    void deveLancarExcecaoParaUuidNulo() {
        // Act & Assert
        ValidacaoValorException exception = assertThrows(
                ValidacaoValorException.class,
                () -> VeiculoId.of(null)
        );
        
        assertEquals("VeiculoId não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve gerar VeiculoId aleatório")
    void deveGerarVeiculoIdAleatorio() {
        // Act
        VeiculoId veiculoId = VeiculoId.generate();
        
        // Assert
        assertNotNull(veiculoId);
        assertNotNull(veiculoId.getValue());
    }

    @Test
    @DisplayName("Deve criar VeiculoId a partir de string válida")
    void deveCriarVeiculoIdAPartirDeStringValida() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();
        
        // Act
        VeiculoId veiculoId = VeiculoId.fromString(uuidString);
        
        // Assert
        assertNotNull(veiculoId);
        assertEquals(uuid, veiculoId.getValue());
    }

    @Test
    @DisplayName("Deve lançar exceção para string nula")
    void deveLancarExcecaoParaStringNula() {
        // Act & Assert
        ValidacaoValorException exception = assertThrows(
                ValidacaoValorException.class,
                () -> VeiculoId.fromString(null)
        );
        
        assertEquals("VeiculoId não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para string vazia")
    void deveLancarExcecaoParaStringVazia() {
        // Act & Assert
        ValidacaoValorException exception = assertThrows(
                ValidacaoValorException.class,
                () -> VeiculoId.fromString("")
        );
        
        assertEquals("VeiculoId não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para string em branco")
    void deveLancarExcecaoParaStringEmBranco() {
        // Act & Assert
        ValidacaoValorException exception = assertThrows(
                ValidacaoValorException.class,
                () -> VeiculoId.fromString("   ")
        );
        
        assertEquals("VeiculoId não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para string inválida")
    void deveLancarExcecaoParaStringInvalida() {
        // Act & Assert
        ValidacaoValorException exception = assertThrows(
                ValidacaoValorException.class,
                () -> VeiculoId.fromString("uuid-invalido")
        );
        
        assertEquals("VeiculoId inválido: uuid-invalido", exception.getMessage());
    }
}
