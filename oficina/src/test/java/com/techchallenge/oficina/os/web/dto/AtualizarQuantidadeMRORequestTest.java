package com.techchallenge.oficina.os.web.dto;

import com.techchallenge.oficina.os.application.usecases.commands.AtualizarQuantidadeMROCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - AtualizarQuantidadeMRORequest")
class AtualizarQuantidadeMRORequestTest {

    private AtualizarQuantidadeMRORequest request;
    private UUID ordemServicoId;

    @BeforeEach
    void setUp() {
        request = new AtualizarQuantidadeMRORequest();
        ordemServicoId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Deve criar AtualizarQuantidadeMROCommand com sucesso")
    void deveCriarAtualizarQuantidadeMROCommandComSucesso() {
        // Arrange
        UUID itemServicoId = UUID.randomUUID();
        UUID itemMroId = UUID.randomUUID();
        Integer quantidade = 5;
        request.setItemServicoId(itemServicoId);
        request.setItemMroId(itemMroId);
        request.setQuantidade(quantidade);

        // Act
        AtualizarQuantidadeMROCommand command = request.toCommand(ordemServicoId);

        // Assert
        assertNotNull(command);
        assertEquals(ordemServicoId, command.getOrdemServicoId());
        assertEquals(itemServicoId, command.getItemServicoId());
        assertEquals(itemMroId, command.getItemMroId());
        assertEquals(quantidade, command.getNovaQuantidade());
    }

    @Test
    @DisplayName("Deve criar command com quantidade 1")
    void deveCriarCommandComQuantidade1() {
        // Arrange
        UUID itemServicoId = UUID.randomUUID();
        UUID itemMroId = UUID.randomUUID();
        request.setItemServicoId(itemServicoId);
        request.setItemMroId(itemMroId);
        request.setQuantidade(1);

        // Act
        AtualizarQuantidadeMROCommand command = request.toCommand(ordemServicoId);

        // Assert
        assertNotNull(command);
        assertEquals(1, command.getNovaQuantidade());
    }

    @Test
    @DisplayName("Deve criar command com quantidade grande")
    void deveCriarCommandComQuantidadeGrande() {
        // Arrange
        UUID itemServicoId = UUID.randomUUID();
        UUID itemMroId = UUID.randomUUID();
        request.setItemServicoId(itemServicoId);
        request.setItemMroId(itemMroId);
        request.setQuantidade(100);

        // Act
        AtualizarQuantidadeMROCommand command = request.toCommand(ordemServicoId);

        // Assert
        assertNotNull(command);
        assertEquals(100, command.getNovaQuantidade());
    }

    @Test
    @DisplayName("Deve permitir definir e obter itemServicoId")
    void devePermitirDefinirEObterItemServicoId() {
        // Arrange
        UUID itemServicoId = UUID.randomUUID();

        // Act
        request.setItemServicoId(itemServicoId);

        // Assert
        assertEquals(itemServicoId, request.getItemServicoId());
    }

    @Test
    @DisplayName("Deve permitir definir e obter itemMroId")
    void devePermitirDefinirEObterItemMroId() {
        // Arrange
        UUID itemMroId = UUID.randomUUID();

        // Act
        request.setItemMroId(itemMroId);

        // Assert
        assertEquals(itemMroId, request.getItemMroId());
    }

    @Test
    @DisplayName("Deve permitir definir e obter quantidade")
    void devePermitirDefinirEObterQuantidade() {
        // Act
        request.setQuantidade(10);

        // Assert
        assertEquals(10, request.getQuantidade());
    }

    @Test
    @DisplayName("Deve permitir atualizar quantidade múltiplas vezes")
    void devePermitirAtualizarQuantidadeMultiplasVezes() {
        // Act & Assert
        request.setQuantidade(5);
        assertEquals(5, request.getQuantidade());

        request.setQuantidade(10);
        assertEquals(10, request.getQuantidade());

        request.setQuantidade(15);
        assertEquals(15, request.getQuantidade());
    }

    @Test
    @DisplayName("Deve criar command com diferentes ordemServicoId")
    void deveCriarCommandComDiferentesOrdemServicoId() {
        // Arrange
        UUID itemServicoId = UUID.randomUUID();
        UUID itemMroId = UUID.randomUUID();
        request.setItemServicoId(itemServicoId);
        request.setItemMroId(itemMroId);
        request.setQuantidade(5);

        // Act
        UUID ordemServicoId1 = UUID.randomUUID();
        AtualizarQuantidadeMROCommand command1 = request.toCommand(ordemServicoId1);

        UUID ordemServicoId2 = UUID.randomUUID();
        AtualizarQuantidadeMROCommand command2 = request.toCommand(ordemServicoId2);

        // Assert
        assertNotEquals(command1.getOrdemServicoId(), command2.getOrdemServicoId());
        assertEquals(ordemServicoId1, command1.getOrdemServicoId());
        assertEquals(ordemServicoId2, command2.getOrdemServicoId());
    }

    @Test
    @DisplayName("Deve manter IDs consistentes entre requests")
    void deveManterIdsConsistentesEntreRequests() {
        // Arrange
        UUID itemServicoId = UUID.randomUUID();
        UUID itemMroId = UUID.randomUUID();
        Integer quantidade = 7;
        request.setItemServicoId(itemServicoId);
        request.setItemMroId(itemMroId);
        request.setQuantidade(quantidade);

        // Act
        AtualizarQuantidadeMROCommand command = request.toCommand(ordemServicoId);

        // Assert
        assertEquals(itemServicoId, command.getItemServicoId());
        assertEquals(itemMroId, command.getItemMroId());
        assertEquals(quantidade, command.getNovaQuantidade());
    }

    @Test
    @DisplayName("Deve criar command com UUIDs válidos")
    void deveCriarCommandComUUIDsValidos() {
        // Arrange
        UUID itemServicoId = UUID.randomUUID();
        UUID itemMroId = UUID.randomUUID();
        request.setItemServicoId(itemServicoId);
        request.setItemMroId(itemMroId);
        request.setQuantidade(3);

        // Act
        AtualizarQuantidadeMROCommand command = request.toCommand(ordemServicoId);

        // Assert
        assertNotNull(command.getItemServicoId());
        assertNotNull(command.getItemMroId());
        assertNotNull(command.getOrdemServicoId());
    }
}
