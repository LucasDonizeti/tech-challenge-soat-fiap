package com.techchallenge.oficina.administrativo.application.usecases.commands;

import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoMROException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - DebitarEstoqueMROCommand")
class DebitarEstoqueMROCommandTest {

    @Test
    @DisplayName("Deve criar comando com dados válidos")
    void deveCriarComandoComDadosValidos() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        DebitarEstoqueMROCommand command = new DebitarEstoqueMROCommand(id, 10);

        // Assert
        assertNotNull(command);
        assertEquals(id, command.getId());
        assertEquals(10, command.getQuantidade());
    }

    @Test
    @DisplayName("Deve criar comando com quantidade 1")
    void deveCriarComandoComQuantidade1() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        DebitarEstoqueMROCommand command = new DebitarEstoqueMROCommand(id, 1);

        // Assert
        assertNotNull(command);
        assertEquals(1, command.getQuantidade());
    }

    @Test
    @DisplayName("Deve criar comando com quantidade alta")
    void deveCriarComandoComQuantidadeAlta() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        DebitarEstoqueMROCommand command = new DebitarEstoqueMROCommand(id, 1000);

        // Assert
        assertNotNull(command);
        assertEquals(1000, command.getQuantidade());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID é nulo")
    void deveLancarExcecaoQuandoIdENulo() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new DebitarEstoqueMROCommand(null, 10)
        );

        assertEquals("ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando quantidade é nula")
    void deveLancarExcecaoQuandoQuantidadeENula() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new DebitarEstoqueMROCommand(id, null)
        );

        assertEquals("Quantidade é obrigatória", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando quantidade é zero")
    void deveLancarExcecaoQuandoQuantidadeEZero() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new DebitarEstoqueMROCommand(id, 0)
        );

        assertEquals("Quantidade deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando quantidade é negativa")
    void deveLancarExcecaoQuandoQuantidadeENegativa() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new DebitarEstoqueMROCommand(id, -5)
        );

        assertEquals("Quantidade deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve criar comando com ID específico")
    void deveCriarComandoComIdEspecifico() {
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        // Act
        DebitarEstoqueMROCommand command = new DebitarEstoqueMROCommand(id, 5);

        // Assert
        assertNotNull(command);
        assertEquals(id, command.getId());
    }

    @Test
    @DisplayName("Deve criar comando com quantidade pequena")
    void deveCriarComandoComQuantidadePequena() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        DebitarEstoqueMROCommand command = new DebitarEstoqueMROCommand(id, 3);

        // Assert
        assertNotNull(command);
        assertEquals(3, command.getQuantidade());
    }
}
