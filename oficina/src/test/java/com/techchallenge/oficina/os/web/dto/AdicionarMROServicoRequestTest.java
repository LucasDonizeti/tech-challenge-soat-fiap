package com.techchallenge.oficina.os.web.dto;

import com.techchallenge.oficina.os.application.usecases.commands.AdicionarMROServicoCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - AdicionarMROServicoRequest")
class AdicionarMROServicoRequestTest {

    private AdicionarMROServicoRequest request;

    @BeforeEach
    void setUp() {
        request = new AdicionarMROServicoRequest();
    }

    @Test
    @DisplayName("Deve criar AdicionarMROServicoCommand com dados válidos")
    void deveCriarAdicionarMROServicoCommandComDadosValidos() {
        // Arrange
        UUID itemServicoId = UUID.randomUUID();
        UUID mroId = UUID.randomUUID();
        UUID ordemServicoId = UUID.randomUUID();
        request.setItemServicoId(itemServicoId);
        request.setMroId(mroId);
        request.setQuantidade(5);

        // Act
        AdicionarMROServicoCommand command = request.toCommand(ordemServicoId);

        // Assert
        assertNotNull(command);
        assertEquals(ordemServicoId, command.getOrdemServicoId());
        assertEquals(itemServicoId, command.getItemServicoId());
        assertEquals(mroId, command.getMroId());
        assertEquals(5, command.getQuantidade());
    }

    @Test
    @DisplayName("Deve criar AdicionarMROServicoCommand com quantidade 1")
    void deveCriarAdicionarMROServicoCommandComQuantidade1() {
        // Arrange
        UUID itemServicoId = UUID.randomUUID();
        UUID mroId = UUID.randomUUID();
        UUID ordemServicoId = UUID.randomUUID();
        request.setItemServicoId(itemServicoId);
        request.setMroId(mroId);
        request.setQuantidade(1);

        // Act
        AdicionarMROServicoCommand command = request.toCommand(ordemServicoId);

        // Assert
        assertNotNull(command);
        assertEquals(1, command.getQuantidade());
    }

    @Test
    @DisplayName("Deve criar AdicionarMROServicoCommand com quantidade alta")
    void deveCriarAdicionarMROServicoCommandComQuantidadeAlta() {
        // Arrange
        UUID itemServicoId = UUID.randomUUID();
        UUID mroId = UUID.randomUUID();
        UUID ordemServicoId = UUID.randomUUID();
        request.setItemServicoId(itemServicoId);
        request.setMroId(mroId);
        request.setQuantidade(100);

        // Act
        AdicionarMROServicoCommand command = request.toCommand(ordemServicoId);

        // Assert
        assertNotNull(command);
        assertEquals(100, command.getQuantidade());
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
    @DisplayName("Deve permitir definir e obter mroId")
    void devePermitirDefinirEObterMroId() {
        // Arrange
        UUID mroId = UUID.randomUUID();

        // Act
        request.setMroId(mroId);

        // Assert
        assertEquals(mroId, request.getMroId());
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
    @DisplayName("Deve criar command com ordemServicoId diferente")
    void deveCriarCommandComOrdemServicoIdDiferente() {
        // Arrange
        UUID itemServicoId = UUID.randomUUID();
        UUID mroId = UUID.randomUUID();
        UUID ordemServicoId = UUID.randomUUID();
        UUID outroOrdemServicoId = UUID.randomUUID();
        request.setItemServicoId(itemServicoId);
        request.setMroId(mroId);
        request.setQuantidade(3);

        // Act
        AdicionarMROServicoCommand command = request.toCommand(outroOrdemServicoId);

        // Assert
        assertNotNull(command);
        assertEquals(outroOrdemServicoId, command.getOrdemServicoId());
    }
}
