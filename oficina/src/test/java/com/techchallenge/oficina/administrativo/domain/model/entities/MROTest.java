package com.techchallenge.oficina.administrativo.domain.model.entities;

import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoMROException;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.TipoMRO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - MRO Domain Entity")
class MROTest {

    private UUID id;
    private String nome;
    private String descricao;
    private TipoMRO tipo;
    private Integer quantidadeEstoque;
    private BigDecimal precoUnitario;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        nome = "Óleo Motor 5W30";
        descricao = "Óleo para motor automotivo";
        tipo = TipoMRO.INSUMO;
        quantidadeEstoque = 100;
        precoUnitario = new BigDecimal("45.90");
    }

    @Test
    @DisplayName("Deve criar MRO com sucesso")
    void deveCriarMROComSucesso() {
        // Act
        MRO mro = MRO.criar( nome, "PC001", descricao, tipo, quantidadeEstoque, precoUnitario);

        // Assert
        assertNotNull(mro);
        assertNotNull(mro.getId());
        assertEquals(nome, mro.getNome());
        assertEquals(descricao, mro.getDescricao());
        assertEquals(tipo, mro.getTipo());
        assertEquals(quantidadeEstoque, mro.getQuantidadeEstoque());
        assertEquals(precoUnitario, mro.getPrecoUnitario());
        assertTrue(mro.isAtivo());
        assertNotNull(mro.getCriadoEm());
        assertNotNull(mro.getAtualizadoEm());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é nulo")
    void deveLancarExcecaoQuandoNomeENulo() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> MRO.criar(null, null, descricao, tipo, quantidadeEstoque, precoUnitario)
        );

        assertEquals("Nome não pode ser vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é vazio")
    void deveLancarExcecaoQuandoNomeEVazio() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> MRO.criar("", "", descricao, tipo, quantidadeEstoque, precoUnitario)
        );

        assertEquals("Nome não pode ser vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome é apenas espaços")
    void deveLancarExcecaoQuandoNomeEApenasEspacos() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> MRO.criar("   ", "   ", descricao, tipo, quantidadeEstoque, precoUnitario)
        );

        assertEquals("Nome não pode ser vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço é nulo")
    void deveLancarExcecaoQuandoPrecoENulo() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> MRO.criar("PC001", nome, descricao, tipo, quantidadeEstoque, null)
        );

        assertEquals("Preço deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço é zero")
    void deveLancarExcecaoQuandoPrecoEZero() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> MRO.criar("PC001", nome, descricao, tipo, quantidadeEstoque, BigDecimal.ZERO)
        );

        assertEquals("Preço deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço é negativo")
    void deveLancarExcecaoQuandoPrecoENegativo() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> MRO.criar("PC001", nome, descricao, tipo, quantidadeEstoque, new BigDecimal("-10.00"))
        );

        assertEquals("Preço deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando estoque é negativo")
    void deveLancarExcecaoQuandoEstoqueENegativo() {
        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> MRO.criar("PC001", nome, descricao, tipo, -5, precoUnitario)
        );

        assertEquals("Quantidade de estoque não pode ser negativa", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar estoque nulo e definir como zero")
    void deveAceitarEstoqueNuloEDefinirComoZero() {
        // Act
        MRO mro = MRO.criar("PC001", nome, descricao, tipo, null, precoUnitario);

        // Assert
        assertEquals(0, mro.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve aceitar descrição nula")
    void deveAceitarDescricaoNula() {
        // Act
        MRO mro = MRO.criar("PC001", nome, null, tipo, quantidadeEstoque, precoUnitario);

        // Assert
        assertNull(mro.getDescricao());
    }

    @Test
    @DisplayName("Deve atualizar preço com sucesso")
    void deveAtualizarPrecoComSucesso() {
        // Arrange
        MRO mro = MRO.criar("PC001", nome, descricao, tipo, quantidadeEstoque, precoUnitario);
        BigDecimal novoPreco = new BigDecimal("55.90");

        // Act
        mro.atualizarPreco(novoPreco);

        // Assert
        assertEquals(novoPreco, mro.getPrecoUnitario());
        assertTrue(mro.getAtualizadoEm().isAfter(mro.getCriadoEm()) || mro.getAtualizadoEm().isEqual(mro.getCriadoEm()));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar preço para nulo")
    void deveLancarExcecaoAoAtualizarPrecoParaNulo() {
        // Arrange
        MRO mro = MRO.criar("PC001", nome, descricao, tipo, quantidadeEstoque, precoUnitario);

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> mro.atualizarPreco(null)
        );

        assertEquals("Preço não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar preço para zero")
    void deveLancarExcecaoAoAtualizarPrecoParaZero() {
        // Arrange
        MRO mro = MRO.criar("PC001", nome, descricao, tipo, quantidadeEstoque, precoUnitario);

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> mro.atualizarPreco(BigDecimal.ZERO)
        );

        assertEquals("Preço deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar estoque com sucesso")
    void deveAtualizarEstoqueComSucesso() {
        // Arrange
        MRO mro = MRO.criar("PC001", nome, descricao, tipo, quantidadeEstoque, precoUnitario);
        Integer novaQuantidade = 150;

        // Act
        mro.atualizarEstoque(novaQuantidade);

        // Assert
        assertEquals(novaQuantidade, mro.getQuantidadeEstoque());
        assertTrue(mro.getAtualizadoEm().isAfter(mro.getCriadoEm()) || mro.getAtualizadoEm().isEqual(mro.getCriadoEm()));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar estoque para nulo")
    void deveLancarExcecaoAoAtualizarEstoqueParaNulo() {
        // Arrange
        MRO mro = MRO.criar("PC001", nome, descricao, tipo, quantidadeEstoque, precoUnitario);

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> mro.atualizarEstoque(null)
        );

        assertEquals("Quantidade de estoque não pode ser nula", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar estoque para negativo")
    void deveLancarExcecaoAoAtualizarEstoqueParaNegativo() {
        // Arrange
        MRO mro = MRO.criar("PC001", nome, descricao, tipo, quantidadeEstoque, precoUnitario);

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> mro.atualizarEstoque(-10)
        );

        assertEquals("Quantidade de estoque não pode ser negativa", exception.getMessage());
    }

    @Test
    @DisplayName("Deve debitar estoque com sucesso")
    void deveDebitarEstoqueComSucesso() {
        // Arrange
        MRO mro = MRO.criar("PC001", nome, descricao, tipo, quantidadeEstoque, precoUnitario);
        Integer quantidadeDebitar = 10;

        // Act
        mro.debitarEstoque(quantidadeDebitar);

        // Assert
        assertEquals(quantidadeEstoque - quantidadeDebitar, mro.getQuantidadeEstoque());
        assertTrue(mro.getAtualizadoEm().isAfter(mro.getCriadoEm()) || mro.getAtualizadoEm().isEqual(mro.getCriadoEm()));
    }

    @Test
    @DisplayName("Deve lançar exceção ao debitar estoque nulo")
    void deveLancarExcecaoAoDebitarEstoqueNulo() {
        // Arrange
        MRO mro = MRO.criar("PC001", nome, descricao, tipo, quantidadeEstoque, precoUnitario);

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> mro.debitarEstoque(null)
        );

        assertEquals("Quantidade a debitar não pode ser nula", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao debitar estoque zero")
    void deveLancarExcecaoAoDebitarEstoqueZero() {
        // Arrange
        MRO mro = MRO.criar("PC001", nome, descricao, tipo, quantidadeEstoque, precoUnitario);

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> mro.debitarEstoque(0)
        );

        assertEquals("Quantidade a debitar deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao debitar estoque negativo")
    void deveLancarExcecaoAoDebitarEstoqueNegativo() {
        // Arrange
        MRO mro = MRO.criar("PC001", nome, descricao, tipo, quantidadeEstoque, precoUnitario);

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> mro.debitarEstoque(-5)
        );

        assertEquals("Quantidade a debitar deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao debitar estoque insuficiente")
    void deveLancarExcecaoAoDebitarEstoqueInsuficiente() {
        // Arrange
        MRO mro = MRO.criar("PC001", nome, descricao, tipo, 5, precoUnitario);

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> mro.debitarEstoque(10)
        );

        assertTrue(exception.getMessage().contains("Estoque insuficiente"));
    }

    @Test
    @DisplayName("Deve repor estoque com sucesso")
    void deveReporEstoqueComSucesso() {
        // Arrange
        MRO mro = MRO.criar("PC001", nome, descricao, tipo, quantidadeEstoque, precoUnitario);
        Integer quantidadeRepor = 50;

        // Act
        mro.reporEstoque(quantidadeRepor);

        // Assert
        assertEquals(quantidadeEstoque + quantidadeRepor, mro.getQuantidadeEstoque());
        assertTrue(mro.getAtualizadoEm().isAfter(mro.getCriadoEm()) || mro.getAtualizadoEm().isEqual(mro.getCriadoEm()));
    }

    @Test
    @DisplayName("Deve lançar exceção ao repor estoque nulo")
    void deveLancarExcecaoAoReporEstoqueNulo() {
        // Arrange
        MRO mro = MRO.criar("PC001", nome, descricao, tipo, quantidadeEstoque, precoUnitario);

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> mro.reporEstoque(null)
        );

        assertEquals("Quantidade a repor não pode ser nula", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao repor estoque zero")
    void deveLancarExcecaoAoReporEstoqueZero() {
        // Arrange
        MRO mro = MRO.criar("PC001", nome, descricao, tipo, quantidadeEstoque, precoUnitario);

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> mro.reporEstoque(0)
        );

        assertEquals("Quantidade a repor deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao repor estoque negativo")
    void deveLancarExcecaoAoReporEstoqueNegativo() {
        // Arrange
        MRO mro = MRO.criar("PC001", nome, descricao, tipo, quantidadeEstoque, precoUnitario);

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> mro.reporEstoque(-10)
        );

        assertEquals("Quantidade a repor deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar dados com sucesso")
    void deveAtualizarDadosComSucesso() {
        // Arrange
        MRO mro = MRO.criar("PC001", nome, descricao, tipo, quantidadeEstoque, precoUnitario);
        String novoNome = "Óleo Motor 10W40";
        String novaDescricao = "Óleo para motor diesel";
        TipoMRO novoTipo = TipoMRO.INSUMO;

        // Act
        mro.atualizarDados(novoNome, novaDescricao, novoTipo);

        // Assert
        assertEquals(novoNome, mro.getNome());
        assertEquals(novaDescricao, mro.getDescricao());
        assertEquals(novoTipo, mro.getTipo());
        assertTrue(mro.getAtualizadoEm().isAfter(mro.getCriadoEm()) || mro.getAtualizadoEm().isEqual(mro.getCriadoEm()));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar dados com nome nulo")
    void deveLancarExcecaoAoAtualizarDadosComNomeNulo() {
        // Arrange
        MRO mro = MRO.criar("PC001", nome, descricao, tipo, quantidadeEstoque, precoUnitario);

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> mro.atualizarDados(null, descricao, tipo)
        );

        assertEquals("Nome não pode ser vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar dados com nome vazio")
    void deveLancarExcecaoAoAtualizarDadosComNomeVazio() {
        // Arrange
        MRO mro = MRO.criar("PC001", nome, descricao, tipo, quantidadeEstoque, precoUnitario);

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> mro.atualizarDados("", descricao, tipo)
        );

        assertEquals("Nome não pode ser vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve ativar MRO com sucesso")
    void deveAtivarMROComSucesso() {
        // Arrange
        MRO mro = MRO.reconstruir(id, "PC001", nome, descricao, tipo, quantidadeEstoque, precoUnitario, false, LocalDateTime.now(), LocalDateTime.now());

        // Act
        mro.ativar();

        // Assert
        assertTrue(mro.isAtivo());
        assertTrue(mro.getAtualizadoEm().isAfter(mro.getCriadoEm()) || mro.getAtualizadoEm().isEqual(mro.getCriadoEm()));
    }

    @Test
    @DisplayName("Deve lançar exceção ao ativar MRO já ativo")
    void deveLancarExcecaoAoAtivarMROJaAtivo() {
        // Arrange
        MRO mro = MRO.criar("PC001", nome, descricao, tipo, quantidadeEstoque, precoUnitario);

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> mro.ativar()
        );

        assertEquals("MRO já está ativo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve desativar MRO com sucesso")
    void deveDesativarMROComSucesso() {
        // Arrange
        MRO mro = MRO.criar("PC001", nome, descricao, tipo, quantidadeEstoque, precoUnitario);

        // Act
        mro.desativar();

        // Assert
        assertFalse(mro.isAtivo());
        assertTrue(mro.getAtualizadoEm().isAfter(mro.getCriadoEm()) || mro.getAtualizadoEm().isEqual(mro.getCriadoEm()));
    }

    @Test
    @DisplayName("Deve lançar exceção ao desativar MRO já inativo")
    void deveLancarExcecaoAoDesativarMROJaInativo() {
        // Arrange
        MRO mro = MRO.reconstruir(id, "PC001", nome, descricao, tipo, quantidadeEstoque, precoUnitario, false, LocalDateTime.now(), LocalDateTime.now());

        // Act & Assert
        ValidacaoMROException exception = assertThrows(
                ValidacaoMROException.class,
                () -> mro.desativar()
        );

        assertEquals("MRO já está inativo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve reconstruir MRO com sucesso")
    void deveReconstruirMROComSucesso() {
        // Arrange
        LocalDateTime criadoEm = LocalDateTime.now().minusDays(1);
        LocalDateTime atualizadoEm = LocalDateTime.now();

        // Act
        MRO mro = MRO.reconstruir(id, nome, "PC001", descricao, tipo, quantidadeEstoque, precoUnitario, true, criadoEm, atualizadoEm);

        // Assert
        assertEquals(id, mro.getId());
        assertEquals(nome, mro.getNome());
        assertEquals(descricao, mro.getDescricao());
        assertEquals(tipo, mro.getTipo());
        assertEquals(quantidadeEstoque, mro.getQuantidadeEstoque());
        assertEquals(precoUnitario, mro.getPrecoUnitario());
        assertTrue(mro.isAtivo());
        assertEquals(criadoEm, mro.getCriadoEm());
        assertEquals(atualizadoEm, mro.getAtualizadoEm());
    }

    @Test
    @DisplayName("Deve verificar se é peça")
    void deveVerificarSeEPeca() {
        // Arrange
        MRO mroPeca = MRO.criar("PC001", nome, descricao, TipoMRO.PECA, quantidadeEstoque, precoUnitario);
        MRO mroInsumo = MRO.criar("INC001", nome, descricao, TipoMRO.INSUMO, quantidadeEstoque, precoUnitario);

        // Assert
        assertTrue(mroPeca.isPeca());
        assertFalse(mroInsumo.isPeca());
    }

    @Test
    @DisplayName("Deve verificar se é insumo")
    void deveVerificarSeEInsumo() {
        // Arrange
        MRO mroPeca = MRO.criar("PC001", nome, descricao, TipoMRO.PECA, quantidadeEstoque, precoUnitario);
        MRO mroInsumo = MRO.criar("INC001", nome, descricao, TipoMRO.INSUMO, quantidadeEstoque, precoUnitario);

        // Assert
        assertTrue(mroInsumo.isInsumo());
        assertFalse(mroPeca.isInsumo());
    }

    @Test
    @DisplayName("Deve verificar se tem estoque suficiente")
    void deveVerificarSeTemEstoqueSuficiente() {
        // Arrange
        MRO mro = MRO.criar("PC001", nome, descricao, tipo, quantidadeEstoque, precoUnitario);

        // Assert
        assertTrue(mro.temEstoqueSuficiente(50));
        assertTrue(mro.temEstoqueSuficiente(100));
        assertFalse(mro.temEstoqueSuficiente(101));
    }
}
