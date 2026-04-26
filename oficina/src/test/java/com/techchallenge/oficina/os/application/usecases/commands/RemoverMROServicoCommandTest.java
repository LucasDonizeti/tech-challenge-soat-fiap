package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - RemoverMROServicoCommand")
class RemoverMROServicoCommandTest {

    @Test
    @DisplayName("Deve criar comando com dados válidos")
    void deveCriarComandoComDadosValidos() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID itemServicoId = UUID.randomUUID();
        UUID itemMroId = UUID.randomUUID();

        // Act
        RemoverMROServicoCommand command = new RemoverMROServicoCommand(ordemServicoId, itemServicoId, itemMroId);

        // Assert
        assertNotNull(command);
        assertEquals(ordemServicoId, command.getOrdemServicoId());
        assertEquals(itemServicoId, command.getItemServicoId());
        assertEquals(itemMroId, command.getItemMroId());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordemServicoId é nulo")
    void deveLancarExcecaoQuandoOrdemServicoIdENulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new RemoverMROServicoCommand(null, UUID.randomUUID(), UUID.randomUUID())
        );

        assertEquals("Ordem de Serviço ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando itemServicoId é nulo")
    void deveLancarExcecaoQuandoItemServicoIdENulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new RemoverMROServicoCommand(UUID.randomUUID(), null, UUID.randomUUID())
        );

        assertEquals("Item de Serviço ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando itemMroId é nulo")
    void deveLancarExcecaoQuandoItemMroIdENulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new RemoverMROServicoCommand(UUID.randomUUID(), UUID.randomUUID(), null)
        );

        assertEquals("Item de MRO ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando todos os IDs são nulos")
    void deveLancarExcecaoQuandoTodosOsIdsSaoNulos() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new RemoverMROServicoCommand(null, null, null)
        );

        assertEquals("Ordem de Serviço ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve criar comando com IDs específicos")
    void deveCriarComandoComIdsEspecificos() {
        // Arrange
        UUID ordemServicoId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID itemServicoId = UUID.fromString("987e6543-e21b-43d2-a456-426614174999");
        UUID itemMroId = UUID.fromString("456e7890-e32b-54d3-a456-426614174888");

        // Act
        RemoverMROServicoCommand command = new RemoverMROServicoCommand(ordemServicoId, itemServicoId, itemMroId);

        // Assert
        assertNotNull(command);
        assertEquals(ordemServicoId, command.getOrdemServicoId());
        assertEquals(itemServicoId, command.getItemServicoId());
        assertEquals(itemMroId, command.getItemMroId());
    }

    @Test
    @DisplayName("Deve criar comando com ordemServicoId específico")
    void deveCriarComandoComOrdemServicoIdEspecifico() {
        // Arrange
        UUID ordemServicoId = UUID.fromString("111e2222-e33b-44d3-a456-426614174111");

        // Act
        RemoverMROServicoCommand command = new RemoverMROServicoCommand(ordemServicoId, UUID.randomUUID(), UUID.randomUUID());

        // Assert
        assertNotNull(command);
        assertEquals(ordemServicoId, command.getOrdemServicoId());
    }

    @Test
    @DisplayName("Deve criar comando com itemServicoId específico")
    void deveCriarComandoComItemServicoIdEspecifico() {
        // Arrange
        UUID itemServicoId = UUID.fromString("222e3333-e44b-55d3-a456-426614174222");

        // Act
        RemoverMROServicoCommand command = new RemoverMROServicoCommand(UUID.randomUUID(), itemServicoId, UUID.randomUUID());

        // Assert
        assertNotNull(command);
        assertEquals(itemServicoId, command.getItemServicoId());
    }

    @Test
    @DisplayName("Deve criar comando com itemMroId específico")
    void deveCriarComandoComItemMroIdEspecifico() {
        // Arrange
        UUID itemMroId = UUID.fromString("333e4444-e55b-66d3-a456-426614174333");

        // Act
        RemoverMROServicoCommand command = new RemoverMROServicoCommand(UUID.randomUUID(), UUID.randomUUID(), itemMroId);

        // Assert
        assertNotNull(command);
        assertEquals(itemMroId, command.getItemMroId());
    }
}
