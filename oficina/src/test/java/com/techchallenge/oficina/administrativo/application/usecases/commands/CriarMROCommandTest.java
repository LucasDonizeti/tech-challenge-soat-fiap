package com.techchallenge.oficina.administrativo.application.usecases.commands;

import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoMROException;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.TipoMRO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - CriarMROCommand")
class CriarMROCommandTest {

    @Test
    @DisplayName("Deve criar comando com sucesso")
    void deveCriarComandoComSucesso() {
        // Arrange
        String nome = "Óleo Motor";
        String descricao = "Óleo para motor 1.6";
        TipoMRO tipo = TipoMRO.PECA;
        Integer quantidadeEstoque = 10;
        BigDecimal precoUnitario = new BigDecimal("50.00");

        // Act
        CriarMROCommand command = new CriarMROCommand(nome, descricao, tipo, quantidadeEstoque, precoUnitario);

        // Assert
        assertNotNull(command);
        assertEquals(nome, command.getNome());
        assertEquals(descricao, command.getDescricao());
        assertEquals(tipo, command.getTipo());
        assertEquals(quantidadeEstoque, command.getQuantidadeEstoque());
        assertEquals(precoUnitario, command.getPrecoUnitario());
    }

    @Test
    @DisplayName("Deve criar comando com quantidade estoque nula")
    void deveCriarComandoComQuantidadeEstoqueNula() {
        // Arrange
        String nome = "Óleo Motor";
        TipoMRO tipo = TipoMRO.PECA;
        BigDecimal precoUnitario = new BigDecimal("50.00");

        // Act
        CriarMROCommand command = new CriarMROCommand(nome, null, tipo, null, precoUnitario);

        // Assert
        assertNotNull(command);
        assertEquals(0, command.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve criar comando com descrição nula")
    void deveCriarComandoComDescricaoNula() {
        // Arrange
        String nome = "Óleo Motor";
        TipoMRO tipo = TipoMRO.PECA;
        BigDecimal precoUnitario = new BigDecimal("50.00");

        // Act
        CriarMROCommand command = new CriarMROCommand(nome, null, tipo, 10, precoUnitario);

        // Assert
        assertNotNull(command);
        assertNull(command.getDescricao());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é nulo")
    void deveLancarExcecaoQuandoNomeENulo() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new CriarMROCommand(null, "Descrição", TipoMRO.PECA, 10, new BigDecimal("50.00"))
        );

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é vazio")
    void deveLancarExcecaoQuandoNomeEVazio() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new CriarMROCommand("", "Descrição", TipoMRO.PECA, 10, new BigDecimal("50.00"))
        );

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é apenas espaços")
    void deveLancarExcecaoQuandoNomeEApenasEspacos() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new CriarMROCommand("   ", "Descrição", TipoMRO.PECA, 10, new BigDecimal("50.00"))
        );

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando tipo é nulo")
    void deveLancarExcecaoQuandoTipoENulo() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new CriarMROCommand("Óleo Motor", "Descrição", null, 10, new BigDecimal("50.00"))
        );

        assertEquals("Tipo é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando quantidade estoque é negativa")
    void deveLancarExcecaoQuandoQuantidadeEstoqueENegativa() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new CriarMROCommand("Óleo Motor", "Descrição", TipoMRO.PECA, -5, new BigDecimal("50.00"))
        );

        assertEquals("Quantidade de estoque não pode ser negativa", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço unitário é nulo")
    void deveLancarExcecaoQuandoPrecoUnitarioENulo() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new CriarMROCommand("Óleo Motor", "Descrição", TipoMRO.PECA, 10, null)
        );

        assertEquals("Preço unitário deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço unitário é zero")
    void deveLancarExcecaoQuandoPrecoUnitarioEZero() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new CriarMROCommand("Óleo Motor", "Descrição", TipoMRO.PECA, 10, BigDecimal.ZERO)
        );

        assertEquals("Preço unitário deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço unitário é negativo")
    void deveLancarExcecaoQuandoPrecoUnitarioENegativo() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new CriarMROCommand("Óleo Motor", "Descrição", TipoMRO.PECA, 10, new BigDecimal("-10.00"))
        );

        assertEquals("Preço unitário deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve criar comando com quantidade estoque zero")
    void deveCriarComandoComQuantidadeEstoqueZero() {
        // Arrange
        BigDecimal precoUnitario = new BigDecimal("50.00");

        // Act
        CriarMROCommand command = new CriarMROCommand("Óleo Motor", "Descrição", TipoMRO.PECA, 0, precoUnitario);

        // Assert
        assertNotNull(command);
        assertEquals(0, command.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve criar comando com preço unitário decimal")
    void deveCriarComandoComPrecoUnitarioDecimal() {
        // Arrange
        BigDecimal precoUnitario = new BigDecimal("99.99");

        // Act
        CriarMROCommand command = new CriarMROCommand("Óleo Motor", "Descrição", TipoMRO.PECA, 10, precoUnitario);

        // Assert
        assertEquals(new BigDecimal("99.99"), command.getPrecoUnitario());
    }

    @Test
    @DisplayName("Deve criar comando com tipo PECA")
    void deveCriarComandoComTipoPeca() {
        // Act
        CriarMROCommand command = new CriarMROCommand("Filtro", "Descrição", TipoMRO.PECA, 10, new BigDecimal("50.00"));

        // Assert
        assertEquals(TipoMRO.PECA, command.getTipo());
    }

    @Test
    @DisplayName("Deve criar comando com tipo INSUMO")
    void deveCriarComandoComTipoInsumo() {
        // Act
        CriarMROCommand command = new CriarMROCommand("Graxa", "Descrição", TipoMRO.INSUMO, 10, new BigDecimal("50.00"));

        // Assert
        assertEquals(TipoMRO.INSUMO, command.getTipo());
    }

    @Test
    @DisplayName("Deve manter campos imutáveis")
    void deveManterCamposImutaveis() {
        // Arrange
        String nome = "Óleo Motor";
        String descricao = "Descrição";
        TipoMRO tipo = TipoMRO.PECA;
        Integer quantidadeEstoque = 10;
        BigDecimal precoUnitario = new BigDecimal("50.00");

        // Act
        CriarMROCommand command = new CriarMROCommand(nome, descricao, tipo, quantidadeEstoque, precoUnitario);

        // Assert
        assertEquals(nome, command.getNome());
        assertEquals(descricao, command.getDescricao());
        assertEquals(tipo, command.getTipo());
        assertEquals(quantidadeEstoque, command.getQuantidadeEstoque());
        assertEquals(precoUnitario, command.getPrecoUnitario());
    }

    @Test
    @DisplayName("Deve criar comando com quantidade estoque grande")
    void deveCriarComandoComQuantidadeEstoqueGrande() {
        // Act
        CriarMROCommand command = new CriarMROCommand("Óleo Motor", "Descrição", TipoMRO.PECA, 1000, new BigDecimal("50.00"));

        // Assert
        assertEquals(1000, command.getQuantidadeEstoque());
    }
}
