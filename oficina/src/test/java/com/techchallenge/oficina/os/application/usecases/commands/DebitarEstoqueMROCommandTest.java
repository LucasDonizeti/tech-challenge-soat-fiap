package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoMROException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - DebitarEstoqueMROCommand")
class DebitarEstoqueMROCommandTest {

    @Test
    @DisplayName("Deve criar comando com sucesso")
    void deveCriarComandoComSucesso() {
        // Arrange
        UUID id = UUID.randomUUID();
        Integer quantidade = 10;

        // Act
        DebitarEstoqueMROCommand command = new DebitarEstoqueMROCommand(id, quantidade);

        // Assert
        assertNotNull(command);
        assertEquals(id, command.getId());
        assertEquals(quantidade, command.getQuantidade());
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
    @DisplayName("Deve criar comando com quantidade grande")
    void deveCriarComandoComQuantidadeGrande() {
        // Arrange
        UUID id = UUID.randomUUID();
        Integer quantidade = 1000;

        // Act
        DebitarEstoqueMROCommand command = new DebitarEstoqueMROCommand(id, quantidade);

        // Assert
        assertNotNull(command);
        assertEquals(quantidade, command.getQuantidade());
    }
}
