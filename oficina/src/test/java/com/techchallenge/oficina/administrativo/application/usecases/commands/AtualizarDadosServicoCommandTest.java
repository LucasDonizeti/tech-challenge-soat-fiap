package com.techchallenge.oficina.administrativo.application.usecases.commands;

import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoServicoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - AtualizarDadosServicoCommand")
class AtualizarDadosServicoCommandTest {

    @Test
    @DisplayName("Deve criar comando com dados completos")
    void deveCriarComandoComDadosCompletos() {
        // Act
        AtualizarDadosServicoCommand command = new AtualizarDadosServicoCommand("Troca de Óleo", "Troca completa de óleo");

        // Assert
        assertNotNull(command);
        assertEquals("Troca de Óleo", command.getNome());
        assertEquals("Troca completa de óleo", command.getDescricao());
    }

    @Test
    @DisplayName("Deve criar comando apenas com nome")
    void deveCriarComandoApenasComNome() {
        // Act
        AtualizarDadosServicoCommand command = new AtualizarDadosServicoCommand("Alinhamento", null);

        // Assert
        assertNotNull(command);
        assertEquals("Alinhamento", command.getNome());
        assertNull(command.getDescricao());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é nulo")
    void deveLancarExcecaoQuandoNomeENulo() {
        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> new AtualizarDadosServicoCommand(null, "Descrição")
        );

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é vazio")
    void deveLancarExcecaoQuandoNomeEVazio() {
        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> new AtualizarDadosServicoCommand("", "Descrição")
        );

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é apenas espaços")
    void deveLancarExcecaoQuandoNomeEApenasEspacos() {
        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> new AtualizarDadosServicoCommand("   ", "Descrição")
        );

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve criar comando com descrição vazia")
    void deveCriarComandoComDescricaoVazia() {
        // Act
        AtualizarDadosServicoCommand command = new AtualizarDadosServicoCommand("Serviço", "");

        // Assert
        assertNotNull(command);
        assertEquals("Serviço", command.getNome());
        assertEquals("", command.getDescricao());
    }

    @Test
    @DisplayName("Deve criar comando com nome longo")
    void deveCriarComandoComNomeLongo() {
        // Arrange
        String nomeLongo = "Troca Completa de Óleo Sintético com Filtro e Verificação de Níveis";

        // Act
        AtualizarDadosServicoCommand command = new AtualizarDadosServicoCommand(nomeLongo, "Descrição");

        // Assert
        assertNotNull(command);
        assertEquals(nomeLongo, command.getNome());
    }

    @Test
    @DisplayName("Deve criar comando com descrição longa")
    void deveCriarComandoComDescricaoLonga() {
        // Arrange
        String descricaoLonga = "Serviço completo de manutenção preventiva incluindo troca de óleo, filtros, verificação de fluidos, inspeção de freios e alinhamento";

        // Act
        AtualizarDadosServicoCommand command = new AtualizarDadosServicoCommand("Serviço", descricaoLonga);

        // Assert
        assertNotNull(command);
        assertEquals(descricaoLonga, command.getDescricao());
    }

    @Test
    @DisplayName("Deve criar comando com caracteres especiais no nome")
    void deveCriarComandoComCaracteresEspeciaisNoNome() {
        // Act
        AtualizarDadosServicoCommand command = new AtualizarDadosServicoCommand("Troca de Óleo & Filtros", "Descrição");

        // Assert
        assertNotNull(command);
        assertEquals("Troca de Óleo & Filtros", command.getNome());
    }

    @Test
    @DisplayName("Deve criar comando com espaços no nome")
    void deveCriarComandoComEspacosNoNome() {
        // Act
        AtualizarDadosServicoCommand command = new AtualizarDadosServicoCommand("  Troca de Óleo  ", "Descrição");

        // Assert
        assertNotNull(command);
        assertEquals("  Troca de Óleo  ", command.getNome());
    }
}
