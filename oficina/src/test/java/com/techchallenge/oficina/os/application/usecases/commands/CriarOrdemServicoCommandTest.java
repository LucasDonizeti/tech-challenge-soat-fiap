package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - CriarOrdemServicoCommand")
class CriarOrdemServicoCommandTest {

    @Test
    @DisplayName("Deve criar comando com dados válidos")
    void deveCriarComandoComDadosValidos() {
        // Arrange
        UUID clienteId = UUID.randomUUID();
        UUID veiculoId = UUID.randomUUID();

        // Act
        CriarOrdemServicoCommand command = new CriarOrdemServicoCommand(clienteId, veiculoId);

        // Assert
        assertNotNull(command);
        assertEquals(clienteId, command.getClienteId());
        assertEquals(veiculoId, command.getVeiculoId());
    }

    @Test
    @DisplayName("Deve lançar exceção quando clienteId é nulo")
    void deveLancarExcecaoQuandoClienteIdENulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new CriarOrdemServicoCommand(null, UUID.randomUUID())
        );

        assertEquals("Cliente ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando veiculoId é nulo")
    void deveLancarExcecaoQuandoVeiculoIdENulo() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new CriarOrdemServicoCommand(UUID.randomUUID(), null)
        );

        assertEquals("Veículo ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ambos os IDs são nulos")
    void deveLancarExcecaoQuandoAmbosOsIdsSaoNulos() {
        // Act & Assert
        ValidacaoOrdemServicoException exception = assertThrows(
                ValidacaoOrdemServicoException.class,
                () -> new CriarOrdemServicoCommand(null, null)
        );

        assertEquals("Cliente ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve criar comando com IDs específicos")
    void deveCriarComandoComIdsEspecificos() {
        // Arrange
        UUID clienteId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID veiculoId = UUID.fromString("987e6543-e21b-43d2-a456-426614174999");

        // Act
        CriarOrdemServicoCommand command = new CriarOrdemServicoCommand(clienteId, veiculoId);

        // Assert
        assertNotNull(command);
        assertEquals(clienteId, command.getClienteId());
        assertEquals(veiculoId, command.getVeiculoId());
    }

    @Test
    @DisplayName("Deve criar comando com clienteId específico")
    void deveCriarComandoComClienteIdEspecifico() {
        // Arrange
        UUID clienteId = UUID.fromString("111e2222-e33b-44d3-a456-426614174111");

        // Act
        CriarOrdemServicoCommand command = new CriarOrdemServicoCommand(clienteId, UUID.randomUUID());

        // Assert
        assertNotNull(command);
        assertEquals(clienteId, command.getClienteId());
    }

    @Test
    @DisplayName("Deve criar comando com veiculoId específico")
    void deveCriarComandoComVeiculoIdEspecifico() {
        // Arrange
        UUID veiculoId = UUID.fromString("222e3333-e44b-55d3-a456-426614174222");

        // Act
        CriarOrdemServicoCommand command = new CriarOrdemServicoCommand(UUID.randomUUID(), veiculoId);

        // Assert
        assertNotNull(command);
        assertEquals(veiculoId, command.getVeiculoId());
    }
}
