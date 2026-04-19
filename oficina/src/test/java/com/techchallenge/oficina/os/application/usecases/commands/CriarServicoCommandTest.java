package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoServicoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - CriarServicoCommand")
class CriarServicoCommandTest {

    @Test
    @DisplayName("Deve criar comando com sucesso")
    void deveCriarCommandComSucesso() {
        // Arrange
        String nome = "Troca de Óleo";
        String descricao = "Troca de óleo do motor";
        BigDecimal preco = new BigDecimal("150.00");

        // Act
        CriarServicoCommand command = new CriarServicoCommand(nome, descricao, preco);

        // Assert
        assertNotNull(command);
        assertEquals(nome, command.getNome());
        assertEquals(descricao, command.getDescricao());
        assertEquals(preco, command.getPreco());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é vazio")
    void deveLancarExcecaoQuandoNomeVazio() {
        // Arrange
        String nome = "";
        String descricao = "Descrição";
        BigDecimal preco = new BigDecimal("100.00");

        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> new CriarServicoCommand(nome, descricao, preco)
        );
        
        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é nulo")
    void deveLancarExcecaoQuandoNomeNulo() {
        // Arrange
        String nome = null;
        String descricao = "Descrição";
        BigDecimal preco = new BigDecimal("100.00");

        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> new CriarServicoCommand(nome, descricao, preco)
        );
        
        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é apenas espaços")
    void deveLancarExcecaoQuandoNomeApenasEspacos() {
        // Arrange
        String nome = "   ";
        String descricao = "Descrição";
        BigDecimal preco = new BigDecimal("100.00");

        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> new CriarServicoCommand(nome, descricao, preco)
        );
        
        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço é zero")
    void deveLancarExcecaoQuandoPrecoZero() {
        // Arrange
        String nome = "Serviço Teste";
        String descricao = "Descrição";
        BigDecimal preco = BigDecimal.ZERO;

        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> new CriarServicoCommand(nome, descricao, preco)
        );
        
        assertEquals("Preço deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço é negativo")
    void deveLancarExcecaoQuandoPrecoNegativo() {
        // Arrange
        String nome = "Serviço Teste";
        String descricao = "Descrição";
        BigDecimal preco = new BigDecimal("-50.00");

        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> new CriarServicoCommand(nome, descricao, preco)
        );
        
        assertEquals("Preço deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço é nulo")
    void deveLancarExcecaoQuandoPrecoNulo() {
        // Arrange
        String nome = "Serviço Teste";
        String descricao = "Descrição";
        BigDecimal preco = null;

        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> new CriarServicoCommand(nome, descricao, preco)
        );
        
        assertEquals("Preço deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar descrição nula")
    void deveAceitarDescricaoNula() {
        // Arrange
        String nome = "Serviço Teste";
        String descricao = null;
        BigDecimal preco = new BigDecimal("100.00");

        // Act
        CriarServicoCommand command = new CriarServicoCommand(nome, descricao, preco);

        // Assert
        assertNotNull(command);
        assertEquals(nome, command.getNome());
        assertNull(command.getDescricao());
        assertEquals(preco, command.getPreco());
    }

    @Test
    @DisplayName("Deve aceitar descrição vazia")
    void deveAceitarDescricaoVazia() {
        // Arrange
        String nome = "Serviço Teste";
        String descricao = "";
        BigDecimal preco = new BigDecimal("100.00");

        // Act
        CriarServicoCommand command = new CriarServicoCommand(nome, descricao, preco);

        // Assert
        assertNotNull(command);
        assertEquals(nome, command.getNome());
        assertEquals(descricao, command.getDescricao());
        assertEquals(preco, command.getPreco());
    }
}
