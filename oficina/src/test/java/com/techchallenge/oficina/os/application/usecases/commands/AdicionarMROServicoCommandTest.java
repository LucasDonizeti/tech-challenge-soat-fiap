package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - AdicionarMROServicoCommand")
class AdicionarMROServicoCommandTest {

    @Test
    @DisplayName("Deve criar comando com dados válidos")
    void deveCriarComandoComDadosValidos() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID itemServicoId = UUID.randomUUID();
        UUID mroId = UUID.randomUUID();

        // Act
        AdicionarMROServicoCommand command = new AdicionarMROServicoCommand(ordemServicoId, itemServicoId, mroId, 5);

        // Assert
        assertNotNull(command);
        assertEquals(ordemServicoId, command.getOrdemServicoId());
        assertEquals(itemServicoId, command.getItemServicoId());
        assertEquals(mroId, command.getMroId());
        assertEquals(5, command.getQuantidade());
    }

    @Test
    @DisplayName("Deve criar comando com quantidade 1")
    void deveCriarComandoComQuantidade1() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID itemServicoId = UUID.randomUUID();
        UUID mroId = UUID.randomUUID();

        // Act
        AdicionarMROServicoCommand command = new AdicionarMROServicoCommand(ordemServicoId, itemServicoId, mroId, 1);

        // Assert
        assertNotNull(command);
        assertEquals(1, command.getQuantidade());
    }

    @Test
    @DisplayName("Deve criar comando com quantidade alta")
    void deveCriarComandoComQuantidadeAlta() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID itemServicoId = UUID.randomUUID();
        UUID mroId = UUID.randomUUID();

        // Act
        AdicionarMROServicoCommand command = new AdicionarMROServicoCommand(ordemServicoId, itemServicoId, mroId, 100);

        // Assert
        assertNotNull(command);
        assertEquals(100, command.getQuantidade());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordemServicoId é nulo")
    void deveLancarExcecaoQuandoOrdemServicoIdENulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new AdicionarMROServicoCommand(null, UUID.randomUUID(), UUID.randomUUID(), 5)
        );

        assertEquals("Ordem de Serviço ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando itemServicoId é nulo")
    void deveLancarExcecaoQuandoItemServicoIdENulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new AdicionarMROServicoCommand(UUID.randomUUID(), null, UUID.randomUUID(), 5)
        );

        assertEquals("Item de Serviço ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando mroId é nulo")
    void deveLancarExcecaoQuandoMroIdENulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new AdicionarMROServicoCommand(UUID.randomUUID(), UUID.randomUUID(), null, 5)
        );

        assertEquals("MRO ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando quantidade é nula")
    void deveLancarExcecaoQuandoQuantidadeENula() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new AdicionarMROServicoCommand(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), null)
        );

        assertEquals("Quantidade deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando quantidade é zero")
    void deveLancarExcecaoQuandoQuantidadeEZero() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new AdicionarMROServicoCommand(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 0)
        );

        assertEquals("Quantidade deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando quantidade é negativa")
    void deveLancarExcecaoQuandoQuantidadeENegativa() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new AdicionarMROServicoCommand(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), -5)
        );

        assertEquals("Quantidade deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve criar comando com IDs específicos")
    void deveCriarComandoComIdsEspecificos() {
        // Arrange
        UUID ordemServicoId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID itemServicoId = UUID.fromString("987e6543-e21b-43d2-a456-426614174999");
        UUID mroId = UUID.fromString("456e7890-e32b-54d3-a456-426614174888");

        // Act
        AdicionarMROServicoCommand command = new AdicionarMROServicoCommand(ordemServicoId, itemServicoId, mroId, 10);

        // Assert
        assertNotNull(command);
        assertEquals(ordemServicoId, command.getOrdemServicoId());
        assertEquals(itemServicoId, command.getItemServicoId());
        assertEquals(mroId, command.getMroId());
    }
}
