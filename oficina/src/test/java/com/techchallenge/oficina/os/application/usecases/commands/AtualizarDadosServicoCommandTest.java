package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoServicoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - AtualizarDadosServicoCommand")
class AtualizarDadosServicoCommandTest {

    @Test
    @DisplayName("Deve criar comando com nome e descrição válidos")
    void deveCriarComandoComNomeEDescricaoValidos() {
        // Arrange
        String nome = "Troca de Óleo Premium";
        String descricao = "Troca de óleo sintético premium";

        // Act
        AtualizarDadosServicoCommand command = new AtualizarDadosServicoCommand(nome, descricao);

        // Assert
        assertNotNull(command);
        assertEquals(nome, command.getNome());
        assertEquals(descricao, command.getDescricao());
    }

    @Test
    @DisplayName("Deve criar comando com nome válido e descrição null")
    void deveCriarComandoComNomeValidoEDescricaoNull() {
        // Arrange
        String nome = "Troca de Óleo";
        String descricao = null;

        // Act
        AtualizarDadosServicoCommand command = new AtualizarDadosServicoCommand(nome, descricao);

        // Assert
        assertNotNull(command);
        assertEquals(nome, command.getNome());
        assertNull(command.getDescricao());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é null")
    void deveLancarExcecaoQuandoNomeENull() {
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
        // Arrange
        String nomeVazio = "";

        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> new AtualizarDadosServicoCommand(nomeVazio, "Descrição")
        );

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é apenas espaços")
    void deveLancarExcecaoQuandoNomeEApenasEspacos() {
        // Arrange
        String nomeEspacos = "   ";

        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> new AtualizarDadosServicoCommand(nomeEspacos, "Descrição")
        );

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar descrição vazia")
    void deveAceitarDescricaoVazia() {
        // Arrange
        String nome = "Troca de Óleo";
        String descricaoVazia = "";

        // Act
        AtualizarDadosServicoCommand command = new AtualizarDadosServicoCommand(nome, descricaoVazia);

        // Assert
        assertNotNull(command);
        assertEquals(nome, command.getNome());
        assertEquals(descricaoVazia, command.getDescricao());
    }

    @Test
    @DisplayName("Deve aceitar descrição apenas espaços")
    void deveAceitarDescricaoApenasEspacos() {
        // Arrange
        String nome = "Troca de Óleo";
        String descricaoEspacos = "   ";

        // Act
        AtualizarDadosServicoCommand command = new AtualizarDadosServicoCommand(nome, descricaoEspacos);

        // Assert
        assertNotNull(command);
        assertEquals(nome, command.getNome());
        assertEquals(descricaoEspacos, command.getDescricao());
    }

    @Test
    @DisplayName("Deve manter nome imutável")
    void deveManterNomeImutavel() {
        // Arrange
        String nome = "Troca de Óleo";
        AtualizarDadosServicoCommand command = new AtualizarDadosServicoCommand(nome, "Descrição");

        // Act
        String nomeRetornado = command.getNome();

        // Assert
        assertEquals(nome, nomeRetornado);
        assertSame(nome, nomeRetornado);
    }

    @Test
    @DisplayName("Deve manter descrição imutável")
    void deveManterDescricaoImutavel() {
        // Arrange
        String descricao = "Descrição do serviço";
        AtualizarDadosServicoCommand command = new AtualizarDadosServicoCommand("Nome", descricao);

        // Act
        String descricaoRetornada = command.getDescricao();

        // Assert
        assertEquals(descricao, descricaoRetornada);
        assertSame(descricao, descricaoRetornada);
    }

    @Test
    @DisplayName("Deve aceitar nome com caracteres especiais")
    void deveAceitarNomeComCaracteresEspeciais() {
        // Arrange
        String nomeEspecial = "Troca de Óleo (Sintético)";

        // Act
        AtualizarDadosServicoCommand command = new AtualizarDadosServicoCommand(nomeEspecial, "Descrição");

        // Assert
        assertNotNull(command);
        assertEquals(nomeEspecial, command.getNome());
    }

    @Test
    @DisplayName("Deve aceitar descrição com caracteres especiais")
    void deveAceitarDescricaoComCaracteresEspeciais() {
        // Arrange
        String descricaoEspecial = "Serviço de troca de óleo sintético (inclui filtro)";

        // Act
        AtualizarDadosServicoCommand command = new AtualizarDadosServicoCommand("Nome", descricaoEspecial);

        // Assert
        assertNotNull(command);
        assertEquals(descricaoEspecial, command.getDescricao());
    }

    @Test
    @DisplayName("Deve aceitar nome com acentuação")
    void deveAceitarNomeComAcentuacao() {
        // Arrange
        String nomeAcentuado = "Troca de Óleo";

        // Act
        AtualizarDadosServicoCommand command = new AtualizarDadosServicoCommand(nomeAcentuado, "Descrição");

        // Assert
        assertNotNull(command);
        assertEquals(nomeAcentuado, command.getNome());
    }

    @Test
    @DisplayName("Deve aceitar nome muito longo")
    void deveAceitarNomeMuitoLongo() {
        // Arrange
        String nomeLongo = "Troca de Óleo Sintético Premium com Filtro de Alta Qualidade para Veículos Importados";

        // Act
        AtualizarDadosServicoCommand command = new AtualizarDadosServicoCommand(nomeLongo, "Descrição");

        // Assert
        assertNotNull(command);
        assertEquals(nomeLongo, command.getNome());
    }

    @Test
    @DisplayName("Deve aceitar descrição muito longa")
    void deveAceitarDescricaoMuitoLonga() {
        // Arrange
        String descricaoLonga = "Serviço completo de troca de óleo que inclui a substituição do óleo do motor, verificação dos níveis de fluidos, inspeção visual do motor e limpeza básica do compartimento do motor.";

        // Act
        AtualizarDadosServicoCommand command = new AtualizarDadosServicoCommand("Nome", descricaoLonga);

        // Assert
        assertNotNull(command);
        assertEquals(descricaoLonga, command.getDescricao());
    }

    @Test
    @DisplayName("Deve criar comando com nome curto")
    void deveCriarComandoComNomeCurto() {
        // Arrange
        String nomeCurto = "Óleo";

        // Act
        AtualizarDadosServicoCommand command = new AtualizarDadosServicoCommand(nomeCurto, "Descrição");

        // Assert
        assertNotNull(command);
        assertEquals(nomeCurto, command.getNome());
    }
}
