package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarDadosServicoCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - AtualizarDadosServicoRequest")
class AtualizarDadosServicoRequestTest {

    private AtualizarDadosServicoRequest request;

    @BeforeEach
    void setUp() {
        request = new AtualizarDadosServicoRequest();
    }

    @Test
    @DisplayName("Deve criar AtualizarDadosServicoCommand com dados válidos")
    void deveCriarAtualizarDadosServicoCommandComDadosValidos() {
        // Arrange
        request.setNome("Troca de Óleo");
        request.setDescricao("Troca completa de óleo do motor");

        // Act
        AtualizarDadosServicoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals("Troca de Óleo", command.getNome());
        assertEquals("Troca completa de óleo do motor", command.getDescricao());
    }

    @Test
    @DisplayName("Deve criar AtualizarDadosServicoCommand apenas com nome")
    void deveCriarAtualizarDadosServicoCommandApenasComNome() {
        // Arrange
        request.setNome("Alinhamento");
        request.setDescricao(null);

        // Act
        AtualizarDadosServicoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals("Alinhamento", command.getNome());
        assertNull(command.getDescricao());
    }

    @Test
    @DisplayName("Deve criar AtualizarDadosServicoCommand apenas com descrição")
    void deveCriarAtualizarDadosServicoCommandApenasComDescricao() {
        // Arrange
        request.setNome("Serviço");
        request.setDescricao("Descrição do serviço");

        // Act
        AtualizarDadosServicoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals("Serviço", command.getNome());
        assertEquals("Descrição do serviço", command.getDescricao());
    }

    @Test
    @DisplayName("Deve permitir definir e obter nome")
    void devePermitirDefinirEObterNome() {
        // Act
        request.setNome("Freio");

        // Assert
        assertEquals("Freio", request.getNome());
    }

    @Test
    @DisplayName("Deve permitir definir e obter descrição")
    void devePermitirDefinirEObterDescricao() {
        // Act
        request.setDescricao("Troca de pastilhas de freio");

        // Assert
        assertEquals("Troca de pastilhas de freio", request.getDescricao());
    }

    @Test
    @DisplayName("Deve permitir atualizar nome múltiplas vezes")
    void devePermitirAtualizarNomeMultiplasVezes() {
        // Act & Assert
        request.setNome("Nome 1");
        assertEquals("Nome 1", request.getNome());

        request.setNome("Nome 2");
        assertEquals("Nome 2", request.getNome());
    }

    @Test
    @DisplayName("Deve permitir atualizar descrição múltiplas vezes")
    void devePermitirAtualizarDescricaoMultiplasVezes() {
        // Act & Assert
        request.setDescricao("Descrição 1");
        assertEquals("Descrição 1", request.getDescricao());

        request.setDescricao("Descrição 2");
        assertEquals("Descrição 2", request.getDescricao());
    }

    @Test
    @DisplayName("Deve criar command com nome longo")
    void deveCriarCommandComNomeLongo() {
        // Arrange
        String nomeLongo = "Troca Completa de Óleo Sintético com Filtro e Verificação de Níveis";
        request.setNome(nomeLongo);

        // Act
        AtualizarDadosServicoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals(nomeLongo, command.getNome());
    }

    @Test
    @DisplayName("Deve criar command com descrição longa")
    void deveCriarCommandComDescricaoLonga() {
        // Arrange
        request.setNome("Serviço");
        String descricaoLonga = "Serviço completo de manutenção preventiva incluindo troca de óleo, filtros, verificação de fluidos, inspeção de freios e alinhamento";
        request.setDescricao(descricaoLonga);

        // Act
        AtualizarDadosServicoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals(descricaoLonga, command.getDescricao());
    }

    @Test
    @DisplayName("Deve criar command com caracteres especiais no nome")
    void deveCriarCommandComCaracteresEspeciaisNoNome() {
        // Arrange
        request.setNome("Troca de Óleo & Filtros");

        // Act
        AtualizarDadosServicoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals("Troca de Óleo & Filtros", command.getNome());
    }

    @Test
    @DisplayName("Deve criar command com descrição vazia")
    void deveCriarCommandComDescricaoVazia() {
        // Arrange
        request.setNome("Serviço");
        request.setDescricao("");

        // Act
        AtualizarDadosServicoCommand command = request.toCommand();

        // Assert
        assertNotNull(command);
        assertEquals("Serviço", command.getNome());
        assertEquals("", command.getDescricao());
    }
}
