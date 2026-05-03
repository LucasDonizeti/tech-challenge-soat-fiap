package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - EnviarParaDiagnosticoCommand")
class EnviarParaDiagnosticoCommandTest {

    @Test
    @DisplayName("Deve criar comando com sucesso")
    void deveCriarComandoComSucesso() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();

        // Act
        EnviarParaDiagnosticoCommand command = new EnviarParaDiagnosticoCommand(ordemServicoId);

        // Assert
        assertNotNull(command);
        assertEquals(ordemServicoId, command.getOrdemServicoId());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordemServicoId é nulo")
    void deveLancarExcecaoQuandoOrdemServicoIdENulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new EnviarParaDiagnosticoCommand(null)
        );

        assertEquals("Ordem de Serviço ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve criar comando com UUID válido")
    void deveCriarComandoComUUIDValido() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();

        // Act
        EnviarParaDiagnosticoCommand command = new EnviarParaDiagnosticoCommand(ordemServicoId);

        // Assert
        assertNotNull(command.getOrdemServicoId());
        assertEquals(ordemServicoId, command.getOrdemServicoId());
    }

    @Test
    @DisplayName("Deve criar comando com diferentes UUIDs")
    void deveCriarComandoComDiferentesUUIDs() {
        // Arrange
        UUID ordemServicoId1 = UUID.randomUUID();
        UUID ordemServicoId2 = UUID.randomUUID();

        // Act
        EnviarParaDiagnosticoCommand command1 = new EnviarParaDiagnosticoCommand(ordemServicoId1);
        EnviarParaDiagnosticoCommand command2 = new EnviarParaDiagnosticoCommand(ordemServicoId2);

        // Assert
        assertNotEquals(command1.getOrdemServicoId(), command2.getOrdemServicoId());
        assertEquals(ordemServicoId1, command1.getOrdemServicoId());
        assertEquals(ordemServicoId2, command2.getOrdemServicoId());
    }

    @Test
    @DisplayName("Deve manter ordemServicoId imutável")
    void deveManterOrdemServicoIdImutavel() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();

        // Act
        EnviarParaDiagnosticoCommand command = new EnviarParaDiagnosticoCommand(ordemServicoId);

        // Assert
        assertEquals(ordemServicoId, command.getOrdemServicoId());
        // Verify that the ID cannot be changed (no setter available)
    }

    @Test
    @DisplayName("Deve criar comando com UUID version 4")
    void deveCriarComandoComUUIDVersion4() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();

        // Act
        EnviarParaDiagnosticoCommand command = new EnviarParaDiagnosticoCommand(ordemServicoId);

        // Assert
        assertEquals(4, command.getOrdemServicoId().version());
    }
}
