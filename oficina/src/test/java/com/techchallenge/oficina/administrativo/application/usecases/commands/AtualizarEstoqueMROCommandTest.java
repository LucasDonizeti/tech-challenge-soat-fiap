package com.techchallenge.oficina.administrativo.application.usecases.commands;

import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoMROException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - AtualizarEstoqueMROCommand")
class AtualizarEstoqueMROCommandTest {

    @Test
    @DisplayName("Deve criar comando com dados válidos")
    void deveCriarComandoComDadosValidos() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        AtualizarEstoqueMROCommand command = new AtualizarEstoqueMROCommand(id, 100);

        // Assert
        assertNotNull(command);
        assertEquals(id, command.getId());
        assertEquals(100, command.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve criar comando com quantidade zero")
    void deveCriarComandoComQuantidadeZero() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        AtualizarEstoqueMROCommand command = new AtualizarEstoqueMROCommand(id, 0);

        // Assert
        assertNotNull(command);
        assertEquals(0, command.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve criar comando com quantidade alta")
    void deveCriarComandoComQuantidadeAlta() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        AtualizarEstoqueMROCommand command = new AtualizarEstoqueMROCommand(id, 10000);

        // Assert
        assertNotNull(command);
        assertEquals(10000, command.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID é nulo")
    void deveLancarExcecaoQuandoIdENulo() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new AtualizarEstoqueMROCommand(null, 100)
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
                () -> new AtualizarEstoqueMROCommand(id, null)
        );

        assertEquals("Quantidade de estoque é obrigatória", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando quantidade é negativa")
    void deveLancarExcecaoQuandoQuantidadeENegativa() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new AtualizarEstoqueMROCommand(id, -10)
        );

        assertEquals("Quantidade de estoque não pode ser negativa", exception.getMessage());
    }

    @Test
    @DisplayName("Deve criar comando com ID específico")
    void deveCriarComandoComIdEspecifico() {
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        // Act
        AtualizarEstoqueMROCommand command = new AtualizarEstoqueMROCommand(id, 50);

        // Assert
        assertNotNull(command);
        assertEquals(id, command.getId());
    }

    @Test
    @DisplayName("Deve criar comando com quantidade 1")
    void deveCriarComandoComQuantidade1() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        AtualizarEstoqueMROCommand command = new AtualizarEstoqueMROCommand(id, 1);

        // Assert
        assertNotNull(command);
        assertEquals(1, command.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve criar comando com quantidade pequena")
    void deveCriarComandoComQuantidadePequena() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        AtualizarEstoqueMROCommand command = new AtualizarEstoqueMROCommand(id, 5);

        // Assert
        assertNotNull(command);
        assertEquals(5, command.getQuantidadeEstoque());
    }
}
