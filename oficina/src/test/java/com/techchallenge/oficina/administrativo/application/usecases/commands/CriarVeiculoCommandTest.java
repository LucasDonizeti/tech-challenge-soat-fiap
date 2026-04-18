package com.techchallenge.oficina.administrativo.application.usecases.commands;

import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoVeiculoException;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - CriarVeiculoCommand")
class CriarVeiculoCommandTest {

    @Test
    @DisplayName("Deve criar comando com sucesso")
    void deveCriarComandoComSucesso() {
        // Arrange
        String placa = "ABC1234";
        String marca = "Toyota";
        String modelo = "Corolla";
        Integer ano = 2022;
        String cor = "Prata";
        UUID clienteId = UUID.randomUUID();

        // Act
        CriarVeiculoCommand command = new CriarVeiculoCommand(placa, marca, modelo, ano, cor, clienteId);

        // Assert
        assertNotNull(command);
        assertEquals(placa, command.getPlaca().getValor());
        assertEquals(marca, command.getMarca());
        assertEquals(modelo, command.getModelo());
        assertEquals(ano, command.getAno());
        assertEquals(cor, command.getCor());
        assertEquals(clienteId, command.getClienteId());
    }

    @Test
    @DisplayName("Deve lançar exceção quando placa é nula")
    void deveLancarExcecaoQuandoPlacaENula() {
        // Act & Assert
        ValidacaoVeiculoException exception = assertThrows(
                ValidacaoVeiculoException.class,
                () -> new CriarVeiculoCommand(null, "Toyota", "Corolla", 2022, "Prata", UUID.randomUUID())
        );

        assertEquals("Placa é obrigatória", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando placa é vazia")
    void deveLancarExcecaoQuandoPlacaEVazia() {
        // Act & Assert
        ValidacaoVeiculoException exception = assertThrows(
                ValidacaoVeiculoException.class,
                () -> new CriarVeiculoCommand("", "Toyota", "Corolla", 2022, "Prata", UUID.randomUUID())
        );

        assertEquals("Placa é obrigatória", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando placa é apenas espaços")
    void deveLancarExcecaoQuandoPlacaEApenasEspacos() {
        // Act & Assert
        ValidacaoVeiculoException exception = assertThrows(
                ValidacaoVeiculoException.class,
                () -> new CriarVeiculoCommand("   ", "Toyota", "Corolla", 2022, "Prata", UUID.randomUUID())
        );

        assertEquals("Placa é obrigatória", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando marca é nula")
    void deveLancarExcecaoQuandoMarcaENula() {
        // Act & Assert
        ValidacaoVeiculoException exception = assertThrows(
                ValidacaoVeiculoException.class,
                () -> new CriarVeiculoCommand("ABC1234", null, "Corolla", 2022, "Prata", UUID.randomUUID())
        );

        assertEquals("Marca é obrigatória", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando marca é vazia")
    void deveLancarExcecaoQuandoMarcaEVazia() {
        // Act & Assert
        ValidacaoVeiculoException exception = assertThrows(
                ValidacaoVeiculoException.class,
                () -> new CriarVeiculoCommand("ABC1234", "", "Corolla", 2022, "Prata", UUID.randomUUID())
        );

        assertEquals("Marca é obrigatória", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando modelo é nulo")
    void deveLancarExcecaoQuandoModeloENulo() {
        // Act & Assert
        ValidacaoVeiculoException exception = assertThrows(
                ValidacaoVeiculoException.class,
                () -> new CriarVeiculoCommand("ABC1234", "Toyota", null, 2022, "Prata", UUID.randomUUID())
        );

        assertEquals("Modelo é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando modelo é vazio")
    void deveLancarExcecaoQuandoModeloEVazio() {
        // Act & Assert
        ValidacaoVeiculoException exception = assertThrows(
                ValidacaoVeiculoException.class,
                () -> new CriarVeiculoCommand("ABC1234", "Toyota", "", 2022, "Prata", UUID.randomUUID())
        );

        assertEquals("Modelo é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ano é nulo")
    void deveLancarExcecaoQuandoAnoENulo() {
        // Act & Assert
        ValidacaoVeiculoException exception = assertThrows(
                ValidacaoVeiculoException.class,
                () -> new CriarVeiculoCommand("ABC1234", "Toyota", "Corolla", null, "Prata", UUID.randomUUID())
        );

        assertEquals("Ano é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando clienteId é nulo")
    void deveLancarExcecaoQuandoClienteIdENulo() {
        // Act & Assert
        ValidacaoVeiculoException exception = assertThrows(
                ValidacaoVeiculoException.class,
                () -> new CriarVeiculoCommand("ABC1234", "Toyota", "Corolla", 2022, "Prata", null)
        );

        assertEquals("Cliente ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar cor nula")
    void deveAceitarCorNula() {
        // Arrange
        String placa = "ABC1234";
        String marca = "Toyota";
        String modelo = "Corolla";
        Integer ano = 2022;
        String cor = null;
        UUID clienteId = UUID.randomUUID();

        // Act
        CriarVeiculoCommand command = new CriarVeiculoCommand(placa, marca, modelo, ano, cor, clienteId);

        // Assert
        assertNotNull(command);
        assertNull(command.getCor());
    }

    @Test
    @DisplayName("Deve converter placa para value object")
    void deveConverterPlacaParaValueObject() {
        // Arrange
        String placa = "ABC1234";

        // Act
        CriarVeiculoCommand command = new CriarVeiculoCommand(placa, "Toyota", "Corolla", 2022, "Prata", UUID.randomUUID());

        // Assert
        assertNotNull(command.getPlaca());
        assertEquals(Placa.class, command.getPlaca().getClass());
        assertEquals(placa, command.getPlaca().getValor());
    }
}
