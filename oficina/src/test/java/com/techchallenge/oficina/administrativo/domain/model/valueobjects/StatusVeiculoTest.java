package com.techchallenge.oficina.administrativo.domain.model.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de StatusVeiculo - Domain Layer")
class StatusVeiculoTest {

    @Test
    @DisplayName("Deve retornar descrição correta para ATIVO")
    void deveRetornarDescricaoCorretaParaAtivo() {
        // Act
        StatusVeiculo status = StatusVeiculo.ATIVO;

        // Assert
        assertEquals("Ativo", status.getDescricao());
    }

    @Test
    @DisplayName("Deve retornar descrição correta para INATIVO")
    void deveRetornarDescricaoCorretaParaInativo() {
        // Act
        StatusVeiculo status = StatusVeiculo.INATIVO;

        // Assert
        assertEquals("Inativo", status.getDescricao());
    }

    @Test
    @DisplayName("Deve ter exatamente dois valores")
    void deveTerExatamenteDoisValores() {
        // Act
        StatusVeiculo[] valores = StatusVeiculo.values();

        // Assert
        assertEquals(2, valores.length);
        assertEquals(StatusVeiculo.ATIVO, valores[0]);
        assertEquals(StatusVeiculo.INATIVO, valores[1]);
    }

    @Test
    @DisplayName("Deve encontrar valor por nome")
    void deveEncontrarValorPorNome() {
        // Act & Assert
        assertEquals(StatusVeiculo.ATIVO, StatusVeiculo.valueOf("ATIVO"));
        assertEquals(StatusVeiculo.INATIVO, StatusVeiculo.valueOf("INATIVO"));
    }

    @Test
    @DisplayName("Deve lançar exceção para nome inválido")
    void deveLancarExcecaoParaNomeInvalido() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> StatusVeiculo.valueOf("VENDIDO"));
    }
}
