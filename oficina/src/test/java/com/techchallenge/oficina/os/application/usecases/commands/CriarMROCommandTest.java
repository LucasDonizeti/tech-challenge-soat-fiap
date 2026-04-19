package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoMROException;
import com.techchallenge.oficina.os.domain.model.valueobjects.TipoMRO;
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
        String nome = "Óleo Motor 5W30";
        String descricao = "Óleo para motor automotivo";
        TipoMRO tipo = TipoMRO.INSUMO;
        Integer quantidadeEstoque = 100;
        BigDecimal precoUnitario = new BigDecimal("45.90");

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
    @DisplayName("Deve lançar exceção quando nome é nulo")
    void deveLancarExcecaoQuandoNomeENulo() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new CriarMROCommand(null, "Descrição", TipoMRO.INSUMO, 100, new BigDecimal("45.90"))
        );

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é vazio")
    void deveLancarExcecaoQuandoNomeEVazio() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new CriarMROCommand("", "Descrição", TipoMRO.INSUMO, 100, new BigDecimal("45.90"))
        );

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é apenas espaços")
    void deveLancarExcecaoQuandoNomeEApenasEspacos() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new CriarMROCommand("   ", "Descrição", TipoMRO.INSUMO, 100, new BigDecimal("45.90"))
        );

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando tipo é nulo")
    void deveLancarExcecaoQuandoTipoENulo() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new CriarMROCommand("Óleo Motor", "Descrição", null, 100, new BigDecimal("45.90"))
        );

        assertEquals("Tipo é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando quantidade de estoque é negativa")
    void deveLancarExcecaoQuandoQuantidadeEstoqueENegativa() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new CriarMROCommand("Óleo Motor", "Descrição", TipoMRO.INSUMO, -10, new BigDecimal("45.90"))
        );

        assertEquals("Quantidade de estoque não pode ser negativa", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço unitário é nulo")
    void deveLancarExcecaoQuandoPrecoUnitarioENulo() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new CriarMROCommand("Óleo Motor", "Descrição", TipoMRO.INSUMO, 100, null)
        );

        assertEquals("Preço unitário deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço unitário é zero")
    void deveLancarExcecaoQuandoPrecoUnitarioEZero() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new CriarMROCommand("Óleo Motor", "Descrição", TipoMRO.INSUMO, 100, BigDecimal.ZERO)
        );

        assertEquals("Preço unitário deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço unitário é negativo")
    void deveLancarExcecaoQuandoPrecoUnitarioENegativo() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> new CriarMROCommand("Óleo Motor", "Descrição", TipoMRO.INSUMO, 100, new BigDecimal("-10.00"))
        );

        assertEquals("Preço unitário deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar descrição nula")
    void deveAceitarDescricaoNula() {
        // Arrange
        String nome = "Óleo Motor 5W30";
        TipoMRO tipo = TipoMRO.INSUMO;
        Integer quantidadeEstoque = 100;
        BigDecimal precoUnitario = new BigDecimal("45.90");

        // Act
        CriarMROCommand command = new CriarMROCommand(nome, null, tipo, quantidadeEstoque, precoUnitario);

        // Assert
        assertNotNull(command);
        assertNull(command.getDescricao());
    }

    @Test
    @DisplayName("Deve aceitar quantidade de estoque nula e definir como zero")
    void deveAceitarQuantidadeEstoqueNulaEDefinirComoZero() {
        // Arrange
        String nome = "Óleo Motor 5W30";
        TipoMRO tipo = TipoMRO.INSUMO;
        BigDecimal precoUnitario = new BigDecimal("45.90");

        // Act
        CriarMROCommand command = new CriarMROCommand(nome, null, tipo, null, precoUnitario);

        // Assert
        assertNotNull(command);
        assertEquals(0, command.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve aceitar quantidade de estoque zero")
    void deveAceitarQuantidadeEstoqueZero() {
        // Arrange
        String nome = "Óleo Motor 5W30";
        TipoMRO tipo = TipoMRO.INSUMO;
        BigDecimal precoUnitario = new BigDecimal("45.90");

        // Act
        CriarMROCommand command = new CriarMROCommand(nome, null, tipo, 0, precoUnitario);

        // Assert
        assertNotNull(command);
        assertEquals(0, command.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve criar comando com tipo PECA")
    void deveCriarComandoComTipoPeca() {
        // Arrange
        String nome = "Filtro de Óleo";
        TipoMRO tipo = TipoMRO.PECA;
        BigDecimal precoUnitario = new BigDecimal("25.00");

        // Act
        CriarMROCommand command = new CriarMROCommand(nome, "Filtro para motor", tipo, 50, precoUnitario);

        // Assert
        assertNotNull(command);
        assertEquals(TipoMRO.PECA, command.getTipo());
    }

    @Test
    @DisplayName("Deve criar comando com tipo INSUMO")
    void deveCriarComandoComTipoInsumo() {
        // Arrange
        String nome = "Graxa Automotiva";
        TipoMRO tipo = TipoMRO.INSUMO;
        BigDecimal precoUnitario = new BigDecimal("15.50");

        // Act
        CriarMROCommand command = new CriarMROCommand(nome, "Graxa para lubrificação", tipo, 200, precoUnitario);

        // Assert
        assertNotNull(command);
        assertEquals(TipoMRO.INSUMO, command.getTipo());
    }
}
