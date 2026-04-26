package com.techchallenge.oficina.administrativo.domain.model.entities;

import com.techchallenge.oficina.administrativo.domain.events.ServicoCriadoEvent;
import com.techchallenge.oficina.administrativo.domain.events.ServicoAtualizadoEvent;
import com.techchallenge.oficina.administrativo.domain.events.ServicoInativadoEvent;
import com.techchallenge.oficina.administrativo.domain.events.ServicoAtivadoEvent;
import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoServicoException;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "servicos")
@Getter
public class Servico extends AbstractAggregateRoot<Servico> {
    
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;
    
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;
    
    @Column(name = "descricao", length = 500)
    private String descricao;
    
    @Column(name = "preco", nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;
    
    @Column(name = "ativo", nullable = false)
    private Boolean ativo;
    
    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;
    
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;
    
    // Construtor padrão para JPA
    protected Servico() {}
    
    // Factory method para criação
    public static Servico criar(String nome, String descricao, BigDecimal preco) {
        Servico servico = new Servico();
        servico.id = UUID.randomUUID();
        servico.nome = nome;
        servico.descricao = descricao;
        servico.preco = preco;
        servico.ativo = true;
        servico.criadoEm = LocalDateTime.now();
        servico.atualizadoEm = LocalDateTime.now();
        
        // Validações
        servico.validarNome();
        servico.validarPreco();
        
        // Domain Event
        servico.registerEvent(new ServicoCriadoEvent(
            servico.id,
            servico.nome,
            servico.preco
        ));
        
        return servico;
    }
    
    // Factory method para reconstrução a partir de dados persistidos (usado pelo mapper)
    public static Servico reconstruir(UUID id, String nome, String descricao, BigDecimal preco, Boolean ativo, LocalDateTime criadoEm, LocalDateTime atualizadoEm) {
        Servico servico = new Servico();
        servico.id = id;
        servico.nome = nome;
        servico.descricao = descricao;
        servico.preco = preco;
        servico.ativo = ativo;
        servico.criadoEm = criadoEm;
        servico.atualizadoEm = atualizadoEm;
        
        return servico;
    }
    
    // Comportamentos de negócio
    public void atualizarPreco(BigDecimal novoPreco) {
        if (novoPreco == null) {
            throw new ValidacaoServicoException("Preço não pode ser nulo");
        }
        if (novoPreco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoServicoException("Preço deve ser maior que zero");
        }
        this.preco = novoPreco;
        this.atualizadoEm = LocalDateTime.now();
        
        registerEvent(new ServicoAtualizadoEvent(this.id, this.nome));
    }
    
    public void atualizarDados(String nome, String descricao) {
        if (nome == null || nome.isBlank()) {
            throw new ValidacaoServicoException("Nome não pode ser vazio");
        }
        this.nome = nome;
        this.descricao = descricao;
        this.atualizadoEm = LocalDateTime.now();
        
        registerEvent(new ServicoAtualizadoEvent(this.id, this.nome));
    }
    
    public void ativar() {
        if (this.ativo) {
            throw new ValidacaoServicoException("Serviço já está ativo");
        }
        this.ativo = true;
        this.atualizadoEm = LocalDateTime.now();
        
        registerEvent(new ServicoAtivadoEvent(this.id));
    }
    
    public void desativar() {
        if (!this.ativo) {
            throw new ValidacaoServicoException("Serviço já está inativo");
        }
        this.ativo = false;
        this.atualizadoEm = LocalDateTime.now();
        
        registerEvent(new ServicoInativadoEvent(this.id));
    }
    
    // Validações
    private void validarNome() {
        if (this.nome == null || this.nome.isBlank()) {
            throw new ValidacaoServicoException("Nome não pode ser vazio");
        }
    }
    
    private void validarPreco() {
        if (this.preco == null || this.preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoServicoException("Preço deve ser maior que zero");
        }
    }
    
    // Métodos de consulta
    public boolean isAtivo() {
        return this.ativo;
    }
}
