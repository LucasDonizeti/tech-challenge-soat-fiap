package com.techchallenge.oficina.os.web.dto;

import com.techchallenge.oficina.os.application.usecases.commands.CriarOrdemServicoCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - CriarOrdemServicoRequest")
class CriarOrdemServicoRequestTest {

    private CriarOrdemServicoRequest request;

    @BeforeEach
    void setUp() {
        request = new CriarOrdemServicoRequest();
    }

    @Test
    @DisplayName("Deve criar CriarOrdemServicoCommand com dados válidos")
    void deveCriarCriarOrdemServicoCommandComDadosValidos() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();
        request.setClienteId(clienteId);
        request.setVeiculoId(veiculoId);

        // Act
        CriarOrdemServicoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals(clienteId, command.getClienteId());
        assertEquals(veiculoId, command.getVeiculoId());
    }

    @Test
    @DisplayName("Deve permitir definir e obter clienteId")
    void devePermitirDefinirEObterClienteId() {
        // Arrange
        UUID clienteId = UUID.randomUUID();

        // Act
        request.setClienteId(clienteId);

        // Assert
        assertEquals(clienteId, request.getClienteId());
    }

    @Test
    @DisplayName("Deve permitir definir e obter veiculoId")
    void devePermitirDefinirEObterVeiculoId() {
        // Arrange
        UUID veiculoId = UUID.randomUUID();

        // Act
        request.setVeiculoId(veiculoId);

        // Assert
        assertEquals(veiculoId, request.getVeiculoId());
    }

    @Test
    @DisplayName("Deve permitir atualizar clienteId múltiplas vezes")
    void devePermitirAtualizarClienteIdMultiplasVezes() {
        // Act & Assert
        UUID clienteId1 = UUID.randomUUID();
        UUID clienteId2 = UUID.randomUUID();

        request.setClienteId(clienteId1);
        assertEquals(clienteId1, request.getClienteId());

        request.setClienteId(clienteId2);
        assertEquals(clienteId2, request.getClienteId());
    }

    @Test
    @DisplayName("Deve permitir atualizar veiculoId múltiplas vezes")
    void devePermitirAtualizarVeiculoIdMultiplasVezes() {
        // Act & Assert
        UUID veiculoId1 = UUID.randomUUID();
        UUID veiculoId2 = UUID.randomUUID();

        request.setVeiculoId(veiculoId1);
        assertEquals(veiculoId1, request.getVeiculoId());

        request.setVeiculoId(veiculoId2);
        assertEquals(veiculoId2, request.getVeiculoId());
    }

    @Test
    @DisplayName("Deve criar command com clienteId específico")
    void deveCriarCommandComClienteIdEspecifico() {
        // Arrange
        UUID clienteId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID veiculoId = UUID.randomUUID();
        request.setClienteId(clienteId);
        request.setVeiculoId(veiculoId);

        // Act
        CriarOrdemServicoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals(clienteId, command.getClienteId());
    }

    @Test
    @DisplayName("Deve criar command com veiculoId específico")
    void deveCriarCommandComVeiculoIdEspecifico() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.fromString("987e6543-e21b-43d2-a456-426614174999");
        request.setClienteId(clienteId);
        request.setVeiculoId(veiculoId);

        // Act
        CriarOrdemServicoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals(veiculoId, command.getVeiculoId());
    }
}
