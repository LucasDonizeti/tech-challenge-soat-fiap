package com.techchallenge.oficina.administrativo.domain.model.entities;

import com.techchallenge.oficina.administrativo.domain.events.*;
import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoMROException;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.TipoMRO;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class MRO extends AbstractAggregateRoot<MRO> {

    private UUID id;

    private String nome;

    private String descricao;

    private TipoMRO tipo;

    private Integer quantidadeEstoque;

    private BigDecimal precoUnitario;

    private Boolean ativo;

    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;
    
    // Construtor padrão para JPA
    protected MRO() {}
    
    // Factory method para criação
    public static MRO criar(String nome, String descricao, TipoMRO tipo, Integer quantidadeEstoque, BigDecimal precoUnitario) {
        MRO mro = new MRO();
        mro.id = UUID.randomUUID();
        mro.nome = nome;
        mro.descricao = descricao;
        mro.tipo = tipo;
        mro.quantidadeEstoque = quantidadeEstoque != null ? quantidadeEstoque : 0;
        mro.precoUnitario = precoUnitario;
        mro.ativo = true;
        mro.criadoEm = LocalDateTime.now();
        mro.atualizadoEm = LocalDateTime.now();
        
        // Validações
        mro.validarNome();
        mro.validarPreco();
        mro.validarEstoque();
        
        // Domain Event
        mro.registerEvent(new MROCriadoEvent(
            mro.id,
            mro.nome,
            mro.tipo.name(),
            mro.quantidadeEstoque,
            mro.precoUnitario
        ));
        
        return mro;
    }
    
    // Factory method para reconstrução a partir de dados persistidos (usado pelo mapper)
    public static MRO reconstruir(UUID id, String nome, String descricao, TipoMRO tipo, Integer quantidadeEstoque, BigDecimal precoUnitario, Boolean ativo, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        MRO mro = new MRO();
        mro.id = id;
        mro.nome = nome;
        mro.descricao = descricao;
        mro.tipo = tipo;
        mro.quantidadeEstoque = quantidadeEstoque;
        mro.precoUnitario = precoUnitario;
        mro.ativo = ativo;
        mro.criadoEm = criadoEm;
        mro.atualizadoEm = atualizadoEm;
        
        return mro;
    }
    
    // Comportamentos de negócio
    public void atualizarPreco(BigDecimal novoPreco) {
        if (novoPreco == null) {
            throw new ValidacaoMROException("Preço não pode ser nulo");
        }
        if (novoPreco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoMROException("Preço deve ser maior que zero");
        }
        this.precoUnitario = novoPreco;
        this.atualizadoEm = LocalDateTime.now();
        
        registerEvent(new MROAtualizadoEvent(this.id, this.nome));
    }
    
    public void atualizarEstoque(Integer novaQuantidade) {
        if (novaQuantidade == null) {
            throw new ValidacaoMROException("Quantidade de estoque não pode ser nula");
        }
        if (novaQuantidade < 0) {
            throw new ValidacaoMROException("Quantidade de estoque não pode ser negativa");
        }
        this.quantidadeEstoque = novaQuantidade;
        this.atualizadoEm = LocalDateTime.now();
        
        registerEvent(new MROAtualizadoEvent(this.id, this.nome));
    }

    public void debitarEstoque(Integer quantidade) {
        if (quantidade == null) {
            throw new ValidacaoMROException("Quantidade a debitar não pode ser nula");
        }
        if (quantidade <= 0) {
            throw new ValidacaoMROException("Quantidade a debitar deve ser maior que zero");
        }
        if (this.quantidadeEstoque < quantidade) {
            throw new ValidacaoMROException("Estoque insuficiente. Estoque atual: " + this.quantidadeEstoque + ", Quantidade solicitada: " + quantidade);
        }

        this.quantidadeEstoque -= quantidade;
        this.atualizadoEm = LocalDateTime.now();

        registerEvent(new EstoqueDebitadoEvent(this.id, this.nome, quantidade, this.quantidadeEstoque));
    }
    
    public void reporEstoque(Integer quantidade) {
        if (quantidade == null) {
            throw new ValidacaoMROException("Quantidade a repor não pode ser nula");
        }
        if (quantidade <= 0) {
            throw new ValidacaoMROException("Quantidade a repor deve ser maior que zero");
        }
        
        this.quantidadeEstoque += quantidade;
        this.atualizadoEm = LocalDateTime.now();
        
        registerEvent(new EstoqueRepostoEvent(this.id, this.nome, quantidade, this.quantidadeEstoque));
    }
    
    public void atualizarDados(String nome, String descricao, TipoMRO tipo) {
        if (nome == null || nome.isBlank()) {
            throw new ValidacaoMROException("Nome não pode ser vazio");
        }
        this.nome = nome;
        this.descricao = descricao;
        this.tipo = tipo;
        this.atualizadoEm = LocalDateTime.now();
        
        registerEvent(new MROAtualizadoEvent(this.id, this.nome));
    }
    
    public void ativar() {
        if (this.ativo) {
            throw new ValidacaoMROException("MRO já está ativo");
        }
        this.ativo = true;
        this.atualizadoEm = LocalDateTime.now();
        
        registerEvent(new MROAtivadoEvent(this.id));
    }
    
    public void desativar() {
        if (!this.ativo) {
            throw new ValidacaoMROException("MRO já está inativo");
        }
        this.ativo = false;
        this.atualizadoEm = LocalDateTime.now();
        
        registerEvent(new MROInativadoEvent(this.id));
    }
    
    // Validações
    private void validarNome() {
        if (this.nome == null || this.nome.isBlank()) {
            throw new ValidacaoMROException("Nome não pode ser vazio");
        }
    }
    
    private void validarPreco() {
        if (this.precoUnitario == null || this.precoUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoMROException("Preço deve ser maior que zero");
        }
    }
    
    private void validarEstoque() {
        if (this.quantidadeEstoque == null || this.quantidadeEstoque < 0) {
            throw new ValidacaoMROException("Quantidade de estoque não pode ser negativa");
        }
    }
    
    // Métodos de consulta
    public boolean isAtivo() {
        return this.ativo;
    }
    
    public boolean isPeca() {
        return this.tipo == TipoMRO.PECA;
    }
    
    public boolean isInsumo() {
        return this.tipo == TipoMRO.INSUMO;
    }
    
    public boolean temEstoqueSuficiente(Integer quantidade) {
        return this.quantidadeEstoque >= quantidade;
    }
}
