package com.techchallenge.oficina.os.web.dto;

import com.techchallenge.oficina.os.application.usecases.commands.AdicionarServicoOrdemCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - AdicionarServicoOrdemRequest")
class AdicionarServicoOrdemRequestTest {

    private AdicionarServicoOrdemRequest request;

    @BeforeEach
    void setUp() {
        request = new AdicionarServicoOrdemRequest();
    }

    @Test
    @DisplayName("Deve criar AdicionarServicoOrdemCommand com dados válidos")
    void deveCriarAdicionarServicoOrdemCommandComDadosValidos() {
        // Arrange
        UUID servicoId = UUID.randomUUID();
        UUID ordemServicoId = UUID.randomUUID();
        request.setServicoId(servicoId);

        // Act
        AdicionarServicoOrdemCommand command = request.toCommand(ordemServicoId);

        // Assert
        assertNotNull(command);
        assertEquals(ordemServicoId, command.getOrdemServicoId());
        assertEquals(servicoId, command.getServicoId());
    }

    @Test
    @DisplayName("Deve permitir definir e obter servicoId")
    void devePermitirDefinirEObterServicoId() {
        // Arrange
        UUID servicoId = UUID.randomUUID();

        // Act
        request.setServicoId(servicoId);

        // Assert
        assertEquals(servicoId, request.getServicoId());
    }

    @Test
    @DisplayName("Deve permitir atualizar servicoId múltiplas vezes")
    void devePermitirAtualizarServicoIdMultiplasVezes() {
        // Act & Assert
        UUID servicoId1 = UUID.randomUUID();
        UUID servicoId2 = UUID.randomUUID();
        UUID servicoId3 = UUID.randomUUID();

        request.setServicoId(servicoId1);
        assertEquals(servicoId1, request.getServicoId());

        request.setServicoId(servicoId2);
        assertEquals(servicoId2, request.getServicoId());

        request.setServicoId(servicoId3);
        assertEquals(servicoId3, request.getServicoId());
    }

    @Test
    @DisplayName("Deve criar command com ordemServicoId diferente")
    void deveCriarCommandComOrdemServicoIdDiferente() {
        // Arrange
        UUID servicoId = UUID.randomUUID();
        UUID ordemServicoId = UUID.randomUUID();
        UUID outroOrdemServicoId = UUID.randomUUID();
        request.setServicoId(servicoId);

        // Act
        AdicionarServicoOrdemCommand command = request.toCommand(outroOrdemServicoId);

        // Assert
        assertNotNull(command);
        assertEquals(outroOrdemServicoId, command.getOrdemServicoId());
        assertEquals(servicoId, command.getServicoId());
    }

    @Test
    @DisplayName("Deve criar command com servicoId específico")
    void deveCriarCommandComServicoIdEspecifico() {
        // Arrange
        UUID servicoId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID ordemServicoId = UUID.randomUUID();
        request.setServicoId(servicoId);

        // Act
        AdicionarServicoOrdemCommand command = request.toCommand(ordemServicoId);

        // Assert
        assertNotNull(command);
        assertEquals(servicoId, command.getServicoId());
    }
}
