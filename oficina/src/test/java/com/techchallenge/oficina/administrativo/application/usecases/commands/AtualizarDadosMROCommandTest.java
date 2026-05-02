package com.techchallenge.oficina.administrativo.application.usecases.commands;

import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoMROException;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.TipoMRO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - AtualizarDadosMROCommand")
class AtualizarDadosMROCommandTest {

    @Test
    @DisplayName("Deve criar comando com sucesso")
    void deveCriarComandoComSucesso() {
        // Arrange
        UUID id = UUID.randomUUID();
        String nome = "Óleo Motor";
        String descricao = "Óleo para motor 1.6";
        TipoMRO tipo = TipoMRO.PECA;

        // Act
        AtualizarDadosMROCommand command = new AtualizarDadosMROCommand(id, nome, descricao, tipo);

        // Assert
        assertNotNull(command);
        assertEquals(id, command.getId());
        assertEquals(nome, command.getNome());
        assertEquals(descricao, command.getDescricao());
        assertEquals(tipo, command.getTipo());
    }

    @Test
    @DisplayName("Deve criar comando com descrição nula")
    void deveCriarComandoComDescricaoNula() {
        // Arrange
        UUID id = UUID.randomUUID();
        String nome = "Óleo Motor";
        TipoMRO tipo = TipoMRO.PECA;

        // Act
        AtualizarDadosMROCommand command = new AtualizarDadosMROCommand(id, nome, null, tipo);

        // Assert
        assertNotNull(command);
        assertEquals(id, command.getId());
        assertEquals(nome, command.getNome());
        assertNull(command.getDescricao());
        assertEquals(tipo, command.getTipo());
    }

    @Test
    @DisplayName("Deve criar comando com tipo nulo")
    void deveCriarComandoComTipoNulo() {
        // Arrange
        UUID id = UUID.randomUUID();
        String nome = "Óleo Motor";
        String descricao = "Óleo para motor 1.6";

        // Act
        AtualizarDadosMROCommand command = new AtualizarDadosMROCommand(id, nome, descricao, null);

        // Assert
        assertNotNull(command);
        assertEquals(id, command.getId());
        assertEquals(nome, command.getNome());
        assertEquals(descricao, command.getDescricao());
        assertNull(command.getTipo());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID é nulo")
    void deveLancarExcecaoQuandoIdENulo() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new AtualizarDadosMROCommand(null, "Óleo Motor", "Descrição", TipoMRO.PECA)
        );

        assertEquals("ID é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é nulo")
    void deveLancarExcecaoQuandoNomeENulo() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new AtualizarDadosMROCommand(id, null, "Descrição", TipoMRO.PECA)
        );

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é vazio")
    void deveLancarExcecaoQuandoNomeEVazio() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new AtualizarDadosMROCommand(id, "", "Descrição", TipoMRO.PECA)
        );

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é apenas espaços")
    void deveLancarExcecaoQuandoNomeEApenasEspacos() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new AtualizarDadosMROCommand(id, "   ", "Descrição", TipoMRO.PECA)
        );

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve criar comando com nome longo")
    void deveCriarComandoComNomeLongo() {
        // Arrange
        UUID id = UUID.randomUUID();
        String nomeLongo = "Nome muito longo do material de reparo e operação";
        String descricao = "Descrição";
        TipoMRO tipo = TipoMRO.PECA;

        // Act
        AtualizarDadosMROCommand command = new AtualizarDadosMROCommand(id, nomeLongo, descricao, tipo);

        // Assert
        assertNotNull(command);
        assertEquals(nomeLongo, command.getNome());
    }

    @Test
    @DisplayName("Deve criar comando com tipo PECA")
    void deveCriarComandoComTipoPeca() {
        // Arrange
        UUID id = UUID.randomUUID();
        String nome = "Filtro de Ar";

        // Act
        AtualizarDadosMROCommand command = new AtualizarDadosMROCommand(id, nome, "Descrição", TipoMRO.PECA);

        // Assert
        assertEquals(TipoMRO.PECA, command.getTipo());
    }

    @Test
    @DisplayName("Deve criar comando com tipo INSUMO")
    void deveCriarComandoComTipoInsumo() {
        // Arrange
        UUID id = UUID.randomUUID();
        String nome = "Graxa";

        // Act
        AtualizarDadosMROCommand command = new AtualizarDadosMROCommand(id, nome, "Descrição", TipoMRO.INSUMO);

        // Assert
        assertEquals(TipoMRO.INSUMO, command.getTipo());
    }

    @Test
    @DisplayName("Deve manter campos imutáveis")
    void deveManterCamposImutaveis() {
        // Arrange
        UUID id = UUID.randomUUID();
        String nome = "Óleo Motor";
        String descricao = "Descrição";
        TipoMRO tipo = TipoMRO.PECA;

        // Act
        AtualizarDadosMROCommand command = new AtualizarDadosMROCommand(id, nome, descricao, tipo);

        // Assert
        assertEquals(id, command.getId());
        assertEquals(nome, command.getNome());
        assertEquals(descricao, command.getDescricao());
        assertEquals(tipo, command.getTipo());
    }

    @Test
    @DisplayName("Deve criar comando com UUID válido")
    void deveCriarComandoComUUIDValido() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        AtualizarDadosMROCommand command = new AtualizarDadosMROCommand(id, "Nome", "Descrição", TipoMRO.PECA);

        // Assert
        assertNotNull(command.getId());
        assertEquals(4, command.getId().version());
    }
}
