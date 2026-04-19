package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoMROException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - AtualizarEstoqueMROCommand")
class AtualizarEstoqueMROCommandTest {

    @Test
    @DisplayName("Deve criar comando com sucesso")
    void deveCriarComandoComSucesso() {
        // Arrange
        UUID id = UUID.randomUUID();
        Integer quantidadeEstoque = 150;

        // Act
        AtualizarEstoqueMROCommand command = new AtualizarEstoqueMROCommand(id, quantidadeEstoque);

        // Assert
        assertNotNull(command);
        assertEquals(id, command.getId());
        assertEquals(quantidadeEstoque, command.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID é nulo")
    void deveLancarExcecaoQuandoIdENulo() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new AtualizarEstoqueMROCommand(null, 150)
        );

        assertEquals("ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando quantidade de estoque é nula")
    void deveLancarExcecaoQuandoQuantidadeEstoqueENula() {
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
    @DisplayName("Deve lançar exceção quando quantidade de estoque é negativa")
    void deveLancarExcecaoQuandoQuantidadeEstoqueENegativa() {
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
    @DisplayName("Deve aceitar quantidade de estoque zero")
    void deveAceitarQuantidadeEstoqueZero() {
        // Arrange
        UUID id = UUID.randomUUID();
        Integer quantidadeEstoque = 0;

        // Act
        AtualizarEstoqueMROCommand command = new AtualizarEstoqueMROCommand(id, quantidadeEstoque);

        // Assert
        assertNotNull(command);
        assertEquals(quantidadeEstoque, command.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve aceitar quantidade de estoque muito grande")
    void deveAceitarQuantidadeEstoqueMuitoGrande() {
        // Arrange
        UUID id = UUID.randomUUID();
        Integer quantidadeEstoque = 99999;

        // Act
        AtualizarEstoqueMROCommand command = new AtualizarEstoqueMROCommand(id, quantidadeEstoque);

        // Assert
        assertNotNull(command);
        assertEquals(quantidadeEstoque, command.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve aceitar quantidade de estoque pequena")
    void deveAceitarQuantidadeEstoquePequena() {
        // Arrange
        UUID id = UUID.randomUUID();
        Integer quantidadeEstoque = 1;

        // Act
        AtualizarEstoqueMROCommand command = new AtualizarEstoqueMROCommand(id, quantidadeEstoque);

        // Assert
        assertNotNull(command);
        assertEquals(quantidadeEstoque, command.getQuantidadeEstoque());
    }
}
