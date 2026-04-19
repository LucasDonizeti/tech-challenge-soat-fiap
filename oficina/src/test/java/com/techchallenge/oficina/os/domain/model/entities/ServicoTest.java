package com.techchallenge.oficina.os.domain.model.entities;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoServicoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - Servico")
class ServicoTest {

    @Test
    @DisplayName("Deve criar serviço com dados válidos")
    void deveCriarServicoComSucesso() {
        // Arrange
        String nome = "Troca de Óleo";
        String descricao = "Troca de óleo do motor";
        BigDecimal preco = new BigDecimal("150.00");

        // Act
        Servico servico = Servico.criar(nome, descricao, preco);

        // Assert
        assertNotNull(servico);
        assertNotNull(servico.getId());
        assertEquals(nome, servico.getNome());
        assertEquals(descricao, servico.getDescricao());
        assertEquals(preco, servico.getPreco());
        assertTrue(servico.getAtivo());
        assertNotNull(servico.getCriadoEm());
        assertNotNull(servico.getAtualizadoEm());
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
                () -> Servico.criar(nome, descricao, preco)
        );
        
        assertEquals("Nome não pode ser vazio", exception.getMessage());
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
                () -> Servico.criar(nome, descricao, preco)
        );
        
        assertEquals("Nome não pode ser vazio", exception.getMessage());
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
                () -> Servico.criar(nome, descricao, preco)
        );
        
        assertEquals("Nome não pode ser vazio", exception.getMessage());
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
                () -> Servico.criar(nome, descricao, preco)
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
                () -> Servico.criar(nome, descricao, preco)
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
                () -> Servico.criar(nome, descricao, preco)
        );
        
        assertEquals("Preço deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar preço com sucesso")
    void deveAtualizarPrecoComSucesso() {
        // Arrange
        Servico servico = Servico.criar("Serviço", "Descrição", new BigDecimal("100.00"));
        BigDecimal novoPreco = new BigDecimal("150.00");

        // Act
        servico.atualizarPreco(novoPreco);

        // Assert
        assertEquals(novoPreco, servico.getPreco());
        assertNotNull(servico.getAtualizadoEm());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar preço para zero")
    void deveLancarExcecaoQuandoAtualizarPrecoParaZero() {
        // Arrange
        Servico servico = Servico.criar("Serviço", "Descrição", new BigDecimal("100.00"));

        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> servico.atualizarPreco(BigDecimal.ZERO)
        );
        
        assertEquals("Preço deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar preço para nulo")
    void deveLancarExcecaoQuandoAtualizarPrecoNulo() {
        // Arrange
        Servico servico = Servico.criar("Serviço", "Descrição", new BigDecimal("100.00"));

        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> servico.atualizarPreco(null)
        );
        
        assertEquals("Preço não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar dados com sucesso")
    void deveAtualizarDadosComSucesso() {
        // Arrange
        Servico servico = Servico.criar("Serviço Antigo", "Descrição Antiga", new BigDecimal("100.00"));
        String novoNome = "Serviço Novo";
        String novaDescricao = "Descrição Nova";

        // Act
        servico.atualizarDados(novoNome, novaDescricao);

        // Assert
        assertEquals(novoNome, servico.getNome());
        assertEquals(novaDescricao, servico.getDescricao());
        assertNotNull(servico.getAtualizadoEm());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar dados com nome vazio")
    void deveLancarExcecaoQuandoAtualizarDadosComNomeVazio() {
        // Arrange
        Servico servico = Servico.criar("Serviço", "Descrição", new BigDecimal("100.00"));

        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> servico.atualizarDados("", "Nova Descrição")
        );
        
        assertEquals("Nome não pode ser vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar dados com nome nulo")
    void deveLancarExcecaoQuandoAtualizarDadosComNomeNulo() {
        // Arrange
        Servico servico = Servico.criar("Serviço", "Descrição", new BigDecimal("100.00"));

        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> servico.atualizarDados(null, "Nova Descrição")
        );
        
        assertEquals("Nome não pode ser vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve ativar serviço com sucesso")
    void deveAtivarServicoComSucesso() {
        // Arrange
        Servico servico = Servico.criar("Serviço", "Descrição", new BigDecimal("100.00"));
        servico.desativar();

        // Act
        servico.ativar();

        // Assert
        assertTrue(servico.getAtivo());
    }

    @Test
    @DisplayName("Deve lançar exceção ao ativar serviço já ativo")
    void deveLancarExcecaoQuandoAtivarServicoJaAtivo() {
        // Arrange
        Servico servico = Servico.criar("Serviço", "Descrição", new BigDecimal("100.00"));

        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> servico.ativar()
        );
        
        assertEquals("Serviço já está ativo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve desativar serviço com sucesso")
    void deveDesativarServicoComSucesso() {
        // Arrange
        Servico servico = Servico.criar("Serviço", "Descrição", new BigDecimal("100.00"));

        // Act
        servico.desativar();

        // Assert
        assertFalse(servico.getAtivo());
    }

    @Test
    @DisplayName("Deve lançar exceção ao desativar serviço já inativo")
    void deveLancarExcecaoQuandoDesativarServicoJaInativo() {
        // Arrange
        Servico servico = Servico.criar("Serviço", "Descrição", new BigDecimal("100.00"));
        servico.desativar();

        // Act & Assert
        ValidacaoServicoException exception = assertThrows(
                ValidacaoServicoException.class,
                () -> servico.desativar()
        );
        
        assertEquals("Serviço já está inativo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve restaurar serviço a partir de parâmetros")
    void deveRestaurarServicoAPartirDeParametros() {
        // Arrange
        UUID id = UUID.randomUUID();
        String nome = "Troca de Óleo";
        String descricao = "Troca de óleo do motor";
        BigDecimal preco = new BigDecimal("150.00");
        Boolean ativo = true;
        java.time.LocalDateTime criadoEm = java.time.LocalDateTime.now().minusDays(10);
        java.time.LocalDateTime atualizadoEm = java.time.LocalDateTime.now().minusDays(5);

        // Act
        Servico servico = Servico.reconstruir(id, nome, descricao, preco, ativo, criadoEm, atualizadoEm);

        // Assert
        assertEquals(id, servico.getId());
        assertEquals(nome, servico.getNome());
        assertEquals(descricao, servico.getDescricao());
        assertEquals(preco, servico.getPreco());
        assertEquals(ativo, servico.getAtivo());
        assertEquals(criadoEm, servico.getCriadoEm());
        assertEquals(atualizadoEm, servico.getAtualizadoEm());
    }
}
