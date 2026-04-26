package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - AtualizarQuantidadeMROCommand")
class AtualizarQuantidadeMROCommandTest {

    @Test
    @DisplayName("Deve criar comando com dados válidos")
    void deveCriarComandoComDadosValidos() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID itemServicoId = UUID.randomUUID();
        UUID itemMroId = UUID.randomUUID();

        // Act
        AtualizarQuantidadeMROCommand command = new AtualizarQuantidadeMROCommand(ordemServicoId, itemServicoId, itemMroId, 10);

        // Assert
        assertNotNull(command);
        assertEquals(ordemServicoId, command.getOrdemServicoId());
        assertEquals(itemServicoId, command.getItemServicoId());
        assertEquals(itemMroId, command.getItemMroId());
        assertEquals(10, command.getNovaQuantidade());
    }

    @Test
    @DisplayName("Deve criar comando com quantidade 1")
    void deveCriarComandoComQuantidade1() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID itemServicoId = UUID.randomUUID();
        UUID itemMroId = UUID.randomUUID();

        // Act
        AtualizarQuantidadeMROCommand command = new AtualizarQuantidadeMROCommand(ordemServicoId, itemServicoId, itemMroId, 1);

        // Assert
        assertNotNull(command);
        assertEquals(1, command.getNovaQuantidade());
    }

    @Test
    @DisplayName("Deve criar comando com quantidade alta")
    void deveCriarComandoComQuantidadeAlta() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID itemServicoId = UUID.randomUUID();
        UUID itemMroId = UUID.randomUUID();

        // Act
        AtualizarQuantidadeMROCommand command = new AtualizarQuantidadeMROCommand(ordemServicoId, itemServicoId, itemMroId, 100);

        // Assert
        assertNotNull(command);
        assertEquals(100, command.getNovaQuantidade());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordemServicoId é nulo")
    void deveLancarExcecaoQuandoOrdemServicoIdENulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new AtualizarQuantidadeMROCommand(null, UUID.randomUUID(), UUID.randomUUID(), 10)
        );

        assertEquals("Ordem de Serviço ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando itemServicoId é nulo")
    void deveLancarExcecaoQuandoItemServicoIdENulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new AtualizarQuantidadeMROCommand(UUID.randomUUID(), null, UUID.randomUUID(), 10)
        );

        assertEquals("Item de Serviço ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando itemMroId é nulo")
    void deveLancarExcecaoQuandoItemMroIdENulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new AtualizarQuantidadeMROCommand(UUID.randomUUID(), UUID.randomUUID(), null, 10)
        );

        assertEquals("Item de MRO ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando novaQuantidade é nula")
    void deveLancarExcecaoQuandoNovaQuantidadeENula() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new AtualizarQuantidadeMROCommand(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), null)
        );

        assertEquals("Quantidade deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando novaQuantidade é zero")
    void deveLancarExcecaoQuandoNovaQuantidadeEZero() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new AtualizarQuantidadeMROCommand(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 0)
        );

        assertEquals("Quantidade deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando novaQuantidade é negativa")
    void deveLancarExcecaoQuandoNovaQuantidadeENegativa() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new AtualizarQuantidadeMROCommand(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), -5)
        );

        assertEquals("Quantidade deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve criar comando com IDs específicos")
    void deveCriarComandoComIdsEspecificos() {
        // Arrange
        UUID ordemServicoId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID itemServicoId = UUID.fromString("987e6543-e21b-43d2-a456-426614174999");
        UUID itemMroId = UUID.fromString("456e7890-e32b-54d3-a456-426614174888");

        // Act
        AtualizarQuantidadeMROCommand command = new AtualizarQuantidadeMROCommand(ordemServicoId, itemServicoId, itemMroId, 15);

        // Assert
        assertNotNull(command);
        assertEquals(ordemServicoId, command.getOrdemServicoId());
        assertEquals(itemServicoId, command.getItemServicoId());
        assertEquals(itemMroId, command.getItemMroId());
    }
}
