package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoMROException;
import com.techchallenge.oficina.os.domain.model.valueobjects.TipoMRO;
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
        String nome = "Óleo Motor 5W30";
        String descricao = "Óleo para motor automotivo";
        TipoMRO tipo = TipoMRO.INSUMO;

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
    @DisplayName("Deve lançar exceção quando ID é nulo")
    void deveLancarExcecaoQuandoIdENulo() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new AtualizarDadosMROCommand(null, "Nome", "Descrição", TipoMRO.INSUMO)
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
                () -> new AtualizarDadosMROCommand(id, null, "Descrição", TipoMRO.INSUMO)
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
                () -> new AtualizarDadosMROCommand(id, "", "Descrição", TipoMRO.INSUMO)
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
                () -> new AtualizarDadosMROCommand(id, "   ", "Descrição", TipoMRO.INSUMO)
        );

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar descrição nula")
    void deveAceitarDescricaoNula() {
        // Arrange
        UUID id = UUID.randomUUID();
        String nome = "Produto Sem Descrição";

        // Act
        AtualizarDadosMROCommand command = new AtualizarDadosMROCommand(id, nome, null, TipoMRO.PECA);

        // Assert
        assertNotNull(command);
        assertNull(command.getDescricao());
    }

    @Test
    @DisplayName("Deve aceitar tipo nulo")
    void deveAceitarTipoNulo() {
        // Arrange
        UUID id = UUID.randomUUID();
        String nome = "Produto Genérico";

        // Act
        AtualizarDadosMROCommand command = new AtualizarDadosMROCommand(id, nome, "Descrição", null);

        // Assert
        assertNotNull(command);
        assertNull(command.getTipo());
    }

    @Test
    @DisplayName("Deve criar comando com tipo PECA")
    void deveCriarComandoComTipoPeca() {
        // Arrange
        UUID id = UUID.randomUUID();
        String nome = "Filtro de Óleo";

        // Act
        AtualizarDadosMROCommand command = new AtualizarDadosMROCommand(id, nome, "Filtro para motor", TipoMRO.PECA);

        // Assert
        assertNotNull(command);
        assertEquals(TipoMRO.PECA, command.getTipo());
    }

    @Test
    @DisplayName("Deve criar comando com tipo INSUMO")
    void deveCriarComandoComTipoInsumo() {
        // Arrange
        UUID id = UUID.randomUUID();
        String nome = "Graxa Automotiva";

        // Act
        AtualizarDadosMROCommand command = new AtualizarDadosMROCommand(id, nome, "Graxa para lubrificação", TipoMRO.INSUMO);

        // Assert
        assertNotNull(command);
        assertEquals(TipoMRO.INSUMO, command.getTipo());
    }
}
