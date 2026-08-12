package com.techchallenge.oficina.administrativo.application.usecases.commands;

import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoServicoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - CriarServicoCommand")
class CriarServicoCommandTest {

    @Test
    @DisplayName("Deve criar comando com dados completos")
    void deveCriarComandoComDadosCompletos() {
        // Act
        CriarServicoCommand command = new CriarServicoCommand(
                "Troca de Óleo",
                "SVC001",
                "Troca completa de óleo",
                new BigDecimal("150.00")
        );

        // Assert
        assertNotNull(command);
        assertEquals("Troca de Óleo", command.getNome());
        assertEquals("Troca completa de óleo", command.getDescricao());
        assertEquals(new BigDecimal("150.00"), command.getPreco());
    }

    @Test
    @DisplayName("Deve criar comando sem descrição")
    void deveCriarComandoSemDescricao() {
        // Act
        CriarServicoCommand command = new CriarServicoCommand(
                "Alinhamento",
                "SVC001",
                null,
                new BigDecimal("100.00")
        );

        // Assert
        assertNotNull(command);
        assertEquals("Alinhamento", command.getNome());
        assertNull(command.getDescricao());
        assertEquals(new BigDecimal("100.00"), command.getPreco());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é nulo")
    void deveLancarExcecaoQuandoNomeENulo() {
        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> new CriarServicoCommand(null, null, "Descrição", new BigDecimal("100.00"))
        );

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é vazio")
    void deveLancarExcecaoQuandoNomeEVazio() {
        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> new CriarServicoCommand("", "", "Descrição", new BigDecimal("100.00"))
        );

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço é nulo")
    void deveLancarExcecaoQuandoPrecoENulo() {
        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> new CriarServicoCommand("SVC001", "Nome", "Descrição", null)
        );

        assertEquals("Preço deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço é zero")
    void deveLancarExcecaoQuandoPrecoEZero() {
        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> new CriarServicoCommand("SVC001", "Nome", "Descrição", BigDecimal.ZERO)
        );

        assertEquals("Preço deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço é negativo")
    void deveLancarExcecaoQuandoPrecoENegativo() {
        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> new CriarServicoCommand("SVC001", "Nome", "Descrição", new BigDecimal("-50.00"))
        );

        assertEquals("Preço deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve criar comando com preço decimal")
    void deveCriarComandoComPrecoDecimal() {
        // Act
        CriarServicoCommand command = new CriarServicoCommand("SVC001", "Serviço", "Descrição", new BigDecimal("125.50"));

        // Assert
        assertNotNull(command);
        assertEquals(new BigDecimal("125.50"), command.getPreco());
    }

    @Test
    @DisplayName("Deve criar comando com preço alto")
    void deveCriarComandoComPrecoAlto() {
        // Act
        CriarServicoCommand command = new CriarServicoCommand("SVC001", "Serviço", "Descrição", new BigDecimal("5000.00"));

        // Assert
        assertNotNull(command);
        assertEquals(new BigDecimal("5000.00"), command.getPreco());
    }

    @Test
    @DisplayName("Deve criar comando com descrição vazia")
    void deveCriarComandoComDescricaoVazia() {
        // Act
        CriarServicoCommand command = new CriarServicoCommand("SVC001", "Serviço", "", new BigDecimal("100.00"));

        // Assert
        assertNotNull(command);
        assertEquals("", command.getDescricao());
    }

    @Test
    @DisplayName("Deve criar comando com nome longo")
    void deveCriarComandoComNomeLongo() {
        // Arrange
        String nomeLongo = "Troca Completa de Óleo Sintético com Filtro e Verificação";

        // Act
        CriarServicoCommand command = new CriarServicoCommand(nomeLongo, "SVC001", "Descrição", new BigDecimal("200.00"));

        // Assert
        assertNotNull(command);
        assertEquals(nomeLongo, command.getNome());
    }
}
