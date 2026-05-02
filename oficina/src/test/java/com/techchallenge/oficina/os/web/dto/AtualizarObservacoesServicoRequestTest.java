package com.techchallenge.oficina.os.web.dto;

import com.techchallenge.oficina.os.application.usecases.commands.AtualizarObservacoesServicoCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - AtualizarObservacoesServicoRequest")
class AtualizarObservacoesServicoRequestTest {

    private AtualizarObservacoesServicoRequest request;
    private UUID ordemServicoId;

    @BeforeEach
    void setUp() {
        request = new AtualizarObservacoesServicoRequest();
        ordemServicoId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Deve criar AtualizarObservacoesServicoCommand com sucesso")
    void deveCriarAtualizarObservacoesServicoCommandComSucesso() {
        // Arrange
        UUID itemServicoId = UUID.randomUUID();
        String observacoes = "Observações do serviço";
        request.setItemServicoId(itemServicoId);
        request.setObservacoes(observacoes);

        // Act
        AtualizarObservacoesServicoCommand command = request.toCommand(ordemServicoId);

        // Assert
        assertNotNull(command);
        assertEquals(ordemServicoId, command.getOrdemServicoId());
        assertEquals(itemServicoId, command.getItemServicoId());
        assertEquals(observacoes, command.getObservacoes());
    }

    @Test
    @DisplayName("Deve criar command com observações nulas")
    void deveCriarCommandComObservacoesNulas() {
        // Arrange
        UUID itemServicoId = UUID.randomUUID();
        request.setItemServicoId(itemServicoId);
        request.setObservacoes(null);

        // Act
        AtualizarObservacoesServicoCommand command = request.toCommand(ordemServicoId);

        // Assert
        assertNotNull(command);
        assertEquals(ordemServicoId, command.getOrdemServicoId());
        assertEquals(itemServicoId, command.getItemServicoId());
        assertNull(command.getObservacoes());
    }

    @Test
    @DisplayName("Deve criar command com observações vazias")
    void deveCriarCommandComObservacoesVazias() {
        // Arrange
        UUID itemServicoId = UUID.randomUUID();
        request.setItemServicoId(itemServicoId);
        request.setObservacoes("");

        // Act
        AtualizarObservacoesServicoCommand command = request.toCommand(ordemServicoId);

        // Assert
        assertNotNull(command);
        assertEquals(ordemServicoId, command.getOrdemServicoId());
        assertEquals(itemServicoId, command.getItemServicoId());
        assertEquals("", command.getObservacoes());
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
    @DisplayName("Deve permitir definir e obter observacoes")
    void devePermitirDefinirEObterObservacoes() {
        // Act
        request.setObservacoes("Nova observação");

        // Assert
        assertEquals("Nova observação", request.getObservacoes());
    }

    @Test
    @DisplayName("Deve permitir atualizar observacoes múltiplas vezes")
    void devePermitirAtualizarObservacoesMultiplasVezes() {
        // Act & Assert
        request.setObservacoes("Observação 1");
        assertEquals("Observação 1", request.getObservacoes());

        request.setObservacoes("Observação 2");
        assertEquals("Observação 2", request.getObservacoes());
    }

    @Test
    @DisplayName("Deve criar command com observações longas")
    void deveCriarCommandComObservacoesLongas() {
        // Arrange
        UUID itemServicoId = UUID.randomUUID();
        String observacoesLongas = "Esta é uma observação muito longa que descreve detalhadamente o serviço realizado no veículo, incluindo problemas encontrados, soluções aplicadas e recomendações futuras para o cliente.";
        request.setItemServicoId(itemServicoId);
        request.setObservacoes(observacoesLongas);

        // Act
        AtualizarObservacoesServicoCommand command = request.toCommand(ordemServicoId);

        // Assert
        assertNotNull(command);
        assertEquals(observacoesLongas, command.getObservacoes());
    }

    @Test
    @DisplayName("Deve criar command com caracteres especiais nas observações")
    void deveCriarCommandComCaracteresEspeciaisNasObservacoes() {
        // Arrange
        UUID itemServicoId = UUID.randomUUID();
        String observacoes = "Serviço realizado com sucesso! @#$%&*()";
        request.setItemServicoId(itemServicoId);
        request.setObservacoes(observacoes);

        // Act
        AtualizarObservacoesServicoCommand command = request.toCommand(ordemServicoId);

        // Assert
        assertNotNull(command);
        assertEquals(observacoes, command.getObservacoes());
    }

    @Test
    @DisplayName("Deve criar command com diferentes ordemServicoId")
    void deveCriarCommandComDiferentesOrdemServicoId() {
        // Arrange
        UUID itemServicoId = UUID.randomUUID();
        request.setItemServicoId(itemServicoId);
        request.setObservacoes("Teste");

        // Act
        UUID ordemServicoId1 = UUID.randomUUID();
        AtualizarObservacoesServicoCommand command1 = request.toCommand(ordemServicoId1);

        UUID ordemServicoId2 = UUID.randomUUID();
        AtualizarObservacoesServicoCommand command2 = request.toCommand(ordemServicoId2);

        // Assert
        assertNotEquals(command1.getOrdemServicoId(), command2.getOrdemServicoId());
        assertEquals(ordemServicoId1, command1.getOrdemServicoId());
        assertEquals(ordemServicoId2, command2.getOrdemServicoId());
    }

    @Test
    @DisplayName("Deve usar construtor com todos os parâmetros")
    void deveUsarConstrutorComTodosOsParametros() {
        // Arrange
        UUID itemServicoId = UUID.randomUUID();
        String observacoes = "Teste completo";

        // Act
        AtualizarObservacoesServicoRequest requestCompleto = new AtualizarObservacoesServicoRequest(itemServicoId, observacoes);

        // Assert
        assertEquals(itemServicoId, requestCompleto.getItemServicoId());
        assertEquals(observacoes, requestCompleto.getObservacoes());
    }
}
