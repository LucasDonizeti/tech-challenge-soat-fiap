package com.techchallenge.oficina.administrativo.domain.model.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de StatusCliente - Domain Layer")
class StatusClienteTest {

    @Test
    @DisplayName("Deve retornar descrição correta para ATIVO")
    void deveRetornarDescricaoCorretaParaAtivo() {
        // Act
        StatusCliente status = StatusCliente.ATIVO;

        // Assert
        assertEquals("Ativo", status.getDescricao());
    }

    @Test
    @DisplayName("Deve retornar descrição correta para INATIVO")
    void deveRetornarDescricaoCorretaParaInativo() {
        // Act
        StatusCliente status = StatusCliente.INATIVO;

        // Assert
        assertEquals("Inativo", status.getDescricao());
    }

    @Test
    @DisplayName("Deve ter exatamente dois valores")
    void deveTerExatamenteDoisValores() {
        // Act
        StatusCliente[] valores = StatusCliente.values();

        // Assert
        assertEquals(2, valores.length);
        assertEquals(StatusCliente.ATIVO, valores[0]);
        assertEquals(StatusCliente.INATIVO, valores[1]);
    }

    @Test
    @DisplayName("Deve encontrar valor por nome")
    void deveEncontrarValorPorNome() {
        // Act & Assert
        assertEquals(StatusCliente.ATIVO, StatusCliente.valueOf("ATIVO"));
        assertEquals(StatusCliente.INATIVO, StatusCliente.valueOf("INATIVO"));
    }

    @Test
    @DisplayName("Deve lançar exceção para nome inválido")
    void deveLancarExcecaoParaNomeInvalido() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> StatusCliente.valueOf("CANCELADO"));
    }
}
