package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - AdicionarServicoOrdemCommand")
class AdicionarServicoOrdemCommandTest {

    @Test
    @DisplayName("Deve criar comando com dados válidos")
    void deveCriarComandoComDadosValidos() {
        // Arrange
        UUID ordemServicoId = UUID.randomUUID();
        UUID servicoId = UUID.randomUUID();

        // Act
        AdicionarServicoOrdemCommand command = new AdicionarServicoOrdemCommand(ordemServicoId, servicoId);

        // Assert
        assertNotNull(command);
        assertEquals(ordemServicoId, command.getOrdemServicoId());
        assertEquals(servicoId, command.getServicoId());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ordemServicoId é nulo")
    void deveLancarExcecaoQuandoOrdemServicoIdENulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new AdicionarServicoOrdemCommand(null, UUID.randomUUID())
        );

        assertEquals("Ordem de Serviço ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando servicoId é nulo")
    void deveLancarExcecaoQuandoServicoIdENulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new AdicionarServicoOrdemCommand(UUID.randomUUID(), null)
        );

        assertEquals("Serviço ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ambos os IDs são nulos")
    void deveLancarExcecaoQuandoAmbosOsIdsSaoNulos() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new AdicionarServicoOrdemCommand(null, null)
        );

        assertEquals("Ordem de Serviço ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve criar comando com IDs específicos")
    void deveCriarComandoComIdsEspecificos() {
        // Arrange
        UUID ordemServicoId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID servicoId = UUID.fromString("987e6543-e21b-43d2-a456-426614174999");

        // Act
        AdicionarServicoOrdemCommand command = new AdicionarServicoOrdemCommand(ordemServicoId, servicoId);

        // Assert
        assertNotNull(command);
        assertEquals(ordemServicoId, command.getOrdemServicoId());
        assertEquals(servicoId, command.getServicoId());
    }

    @Test
    @DisplayName("Deve criar comando com ordemServicoId específico")
    void deveCriarComandoComOrdemServicoIdEspecifico() {
        // Arrange
        UUID ordemServicoId = UUID.fromString("111e2222-e33b-44d3-a456-426614174111");

        // Act
        AdicionarServicoOrdemCommand command = new AdicionarServicoOrdemCommand(ordemServicoId, UUID.randomUUID());

        // Assert
        assertNotNull(command);
        assertEquals(ordemServicoId, command.getOrdemServicoId());
    }

    @Test
    @DisplayName("Deve criar comando com servicoId específico")
    void deveCriarComandoComServicoIdEspecifico() {
        // Arrange
        UUID servicoId = UUID.fromString("222e3333-e44b-55d3-a456-426614174222");

        // Act
        AdicionarServicoOrdemCommand command = new AdicionarServicoOrdemCommand(UUID.randomUUID(), servicoId);

        // Assert
        assertNotNull(command);
        assertEquals(servicoId, command.getServicoId());
    }
}
