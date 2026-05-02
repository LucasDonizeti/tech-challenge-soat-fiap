package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - AtualizarObservacoesServicoCommand")
class AtualizarObservacoesServicoCommandTest {

    @Test
    @DisplayName("Deve criar comando com sucesso")
    void deveCriarComandoComSucesso() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID itemServicoId = UUID.randomUUID();
        String observacoes = "Observações do serviço";

        // Act
        AtualizarObservacoesServicoCommand command = new AtualizarObservacoesServicoCommand(ordemServicoId, itemServicoId, observacoes);

        // Assert
        assertNotNull(command);
        assertEquals(ordemServicoId, command.getOrdemServicoId());
        assertEquals(itemServicoId, command.getItemServicoId());
        assertEquals(observacoes, command.getObservacoes());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordemServicoId é nulo")
    void deveLancarExcecaoQuandoOrdemServicoIdENulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new AtualizarObservacoesServicoCommand(null, UUID.randomUUID(), "Observações")
        );

        assertEquals("Ordem de Serviço ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando itemServicoId é nulo")
    void deveLancarExcecaoQuandoItemServicoIdENulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new AtualizarObservacoesServicoCommand(UUID.randomUUID(), null, "Observações")
        );

        assertEquals("Item de Serviço ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ambos os IDs são nulos")
    void deveLancarExcecaoQuandoAmbosOsIdsSaoNulos() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new AtualizarObservacoesServicoCommand(null, null, "Observações")
        );

        assertEquals("Ordem de Serviço ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve criar comando com observações nulas")
    void deveCriarComandoComObservacoesNulas() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID itemServicoId = UUID.randomUUID();

        // Act
        AtualizarObservacoesServicoCommand command = new AtualizarObservacoesServicoCommand(ordemServicoId, itemServicoId, null);

        // Assert
        assertNotNull(command);
        assertEquals(ordemServicoId, command.getOrdemServicoId());
        assertEquals(itemServicoId, command.getItemServicoId());
        assertNull(command.getObservacoes());
    }

    @Test
    @DisplayName("Deve criar comando com observações vazias")
    void deveCriarComandoComObservacoesVazias() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID itemServicoId = UUID.randomUUID();

        // Act
        AtualizarObservacoesServicoCommand command = new AtualizarObservacoesServicoCommand(ordemServicoId, itemServicoId, "");

        // Assert
        assertNotNull(command);
        assertEquals(ordemServicoId, command.getOrdemServicoId());
        assertEquals(itemServicoId, command.getItemServicoId());
        assertEquals("", command.getObservacoes());
    }

    @Test
    @DisplayName("Deve criar comando com observações longas")
    void deveCriarComandoComObservacoesLongas() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID itemServicoId = UUID.randomUUID();
        String observacoesLongas = "Esta é uma observação muito longa que descreve detalhadamente o serviço realizado no veículo, incluindo problemas encontrados, soluções aplicadas e recomendações futuras para o cliente.";

        // Act
        AtualizarObservacoesServicoCommand command = new AtualizarObservacoesServicoCommand(ordemServicoId, itemServicoId, observacoesLongas);

        // Assert
        assertNotNull(command);
        assertEquals(observacoesLongas, command.getObservacoes());
    }

    @Test
    @DisplayName("Deve criar comando com caracteres especiais nas observações")
    void deveCriarComandoComCaracteresEspeciaisNasObservacoes() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID itemServicoId = UUID.randomUUID();
        String observacoes = "Serviço realizado com sucesso! @#$%&*()";

        // Act
        AtualizarObservacoesServicoCommand command = new AtualizarObservacoesServicoCommand(ordemServicoId, itemServicoId, observacoes);

        // Assert
        assertNotNull(command);
        assertEquals(observacoes, command.getObservacoes());
    }

    @Test
    @DisplayName("Deve criar comando com diferentes UUIDs")
    void deveCriarComandoComDiferentesUUIDs() {
        // Arrange
        UUID ordemServicoId1 = UUID.randomUUID();
        UUID itemServicoId1 = UUID.randomUUID();
        UUID ordemServicoId2 = UUID.randomUUID();
        UUID itemServicoId2 = UUID.randomUUID();

        // Act
        AtualizarObservacoesServicoCommand command1 = new AtualizarObservacoesServicoCommand(ordemServicoId1, itemServicoId1, "Obs 1");
        AtualizarObservacoesServicoCommand command2 = new AtualizarObservacoesServicoCommand(ordemServicoId2, itemServicoId2, "Obs 2");

        // Assert
        assertNotEquals(command1.getOrdemServicoId(), command2.getOrdemServicoId());
        assertNotEquals(command1.getItemServicoId(), command2.getItemServicoId());
    }

    @Test
    @DisplayName("Deve manter campos imutáveis")
    void deveManterCamposImutaveis() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID itemServicoId = UUID.randomUUID();
        String observacoes = "Teste";

        // Act
        AtualizarObservacoesServicoCommand command = new AtualizarObservacoesServicoCommand(ordemServicoId, itemServicoId, observacoes);

        // Assert
        assertEquals(ordemServicoId, command.getOrdemServicoId());
        assertEquals(itemServicoId, command.getItemServicoId());
        assertEquals(observacoes, command.getObservacoes());
    }

    @Test
    @DisplayName("Deve criar comando com UUIDs válidos")
    void deveCriarComandoComUUIDsValidos() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID itemServicoId = UUID.randomUUID();

        // Act
        AtualizarObservacoesServicoCommand command = new AtualizarObservacoesServicoCommand(ordemServicoId, itemServicoId, "Teste");

        // Assert
        assertNotNull(command.getOrdemServicoId());
        assertNotNull(command.getItemServicoId());
        assertEquals(4, command.getOrdemServicoId().version());
        assertEquals(4, command.getItemServicoId().version());
    }
}
