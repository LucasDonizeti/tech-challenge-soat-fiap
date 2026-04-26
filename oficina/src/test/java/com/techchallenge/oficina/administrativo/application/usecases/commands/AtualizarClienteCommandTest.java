package com.techchallenge.oficina.administrativo.application.usecases.commands;

import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoClienteException;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - AtualizarClienteCommand")
class AtualizarClienteCommandTest {

    @Test
    @DisplayName("Deve criar comando com dados válidos")
    void deveCriarComandoComDadosValidos() {
        // Act
        AtualizarClienteCommand command = new AtualizarClienteCommand("João Silva", "joao@example.com");

        // Assert
        assertNotNull(command);
        assertEquals("João Silva", command.getNome().getValor());
        assertEquals("joao@example.com", command.getEmail().getEndereco());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é nulo")
    void deveLancarExcecaoQuandoNomeENulo() {
        // Act & Assert
        ValidacaoClienteException exception = assertThrows(
                ValidacaoClienteException.class,
                () -> new AtualizarClienteCommand(null, "joao@example.com")
        );

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é vazio")
    void deveLancarExcecaoQuandoNomeEVazio() {
        // Act & Assert
        ValidacaoClienteException exception = assertThrows(
                ValidacaoClienteException.class,
                () -> new AtualizarClienteCommand("", "joao@example.com")
        );

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é apenas espaços")
    void deveLancarExcecaoQuandoNomeEApenasEspacos() {
        // Act & Assert
        ValidacaoClienteException exception = assertThrows(
                ValidacaoClienteException.class,
                () -> new AtualizarClienteCommand("   ", "joao@example.com")
        );

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando email é nulo")
    void deveLancarExcecaoQuandoEmailENulo() {
        // Act & Assert
        ValidacaoClienteException exception = assertThrows(
                ValidacaoClienteException.class,
                () -> new AtualizarClienteCommand("João Silva", null)
        );

        assertEquals("Email é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando email é vazio")
    void deveLancarExcecaoQuandoEmailEVazio() {
        // Act & Assert
        ValidacaoClienteException exception = assertThrows(
                ValidacaoClienteException.class,
                () -> new AtualizarClienteCommand("João Silva", "")
        );

        assertEquals("Email é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando email é apenas espaços")
    void deveLancarExcecaoQuandoEmailEApenasEspacos() {
        // Act & Assert
        ValidacaoClienteException exception = assertThrows(
                ValidacaoClienteException.class,
                () -> new AtualizarClienteCommand("João Silva", "   ")
        );

        assertEquals("Email é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve converter nome para value object")
    void deveConverterNomeParaValueObject() {
        // Act
        AtualizarClienteCommand command = new AtualizarClienteCommand("Maria Santos", "maria@example.com");

        // Assert
        assertNotNull(command.getNome());
        assertEquals(Nome.class, command.getNome().getClass());
        assertEquals("Maria Santos", command.getNome().getValor());
    }

    @Test
    @DisplayName("Deve converter email para value object")
    void deveConverterEmailParaValueObject() {
        // Act
        AtualizarClienteCommand command = new AtualizarClienteCommand("Carlos", "carlos@example.com");

        // Assert
        assertNotNull(command.getEmail());
        assertEquals(Email.class, command.getEmail().getClass());
        assertEquals("carlos@example.com", command.getEmail().getEndereco());
    }

    @Test
    @DisplayName("Deve criar comando com nome longo")
    void deveCriarComandoComNomeLongo() {
        // Arrange
        String nomeLongo = "João Francisco da Silva Santos Oliveira Pereira Costa";

        // Act
        AtualizarClienteCommand command = new AtualizarClienteCommand(nomeLongo, "joao@example.com");

        // Assert
        assertNotNull(command);
        assertEquals(nomeLongo, command.getNome().getValor());
    }

    @Test
    @DisplayName("Deve criar comando com caracteres especiais no nome")
    void deveCriarComandoComCaracteresEspeciaisNoNome() {
        // Act
        AtualizarClienteCommand command = new AtualizarClienteCommand("José Antônio da Conceição", "jose@example.com");

        // Assert
        assertNotNull(command);
        assertEquals("José Antônio da Conceição", command.getNome().getValor());
    }

    @Test
    @DisplayName("Deve criar comando com email em maiúsculas")
    void deveCriarComandoComEmailEmMaiusculas() {
        // Act
        AtualizarClienteCommand command = new AtualizarClienteCommand("Cliente", "CLIENTE@EXAMPLE.COM");

        // Assert
        assertNotNull(command);
        assertEquals("cliente@example.com", command.getEmail().getEndereco());
    }

    @Test
    @DisplayName("Deve criar comando com espaços no nome")
    void deveCriarComandoComEspacosNoNome() {
        // Act
        AtualizarClienteCommand command = new AtualizarClienteCommand("  João Silva  ", "joao@example.com");

        // Assert
        assertNotNull(command);
        assertEquals("João Silva", command.getNome().getValor());
    }

    @Test
    @DisplayName("Deve criar comando com espaços no email")
    void deveCriarComandoComEspacosNoEmail() {
        // Act
        AtualizarClienteCommand command = new AtualizarClienteCommand("João Silva", "  joao@example.com  ");

        // Assert
        assertNotNull(command);
        assertEquals("joao@example.com", command.getEmail().getEndereco());
    }
}
