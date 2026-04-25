package com.techchallenge.oficina.os.domain.model.entities;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusItemServico;
import lombok.Getter;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "itens_servico")
@Getter
public class ItemServico {
    
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusItemServico status;
    
    @Column(name = "observacoes", length = 500)
    private String observacoes;
    
    @Column(name = "valor_servico", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorServico;
    
    @Column(name = "valor_mro", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorMro;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    private OrdemServico ordemServico;
    
    @OneToMany(mappedBy = "itemServico", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemMRO> mrosServicos = new ArrayList<>();
    
    // Construtor padrão para JPA
    protected ItemServico() {}
    
    // Factory method para criação
    public static ItemServico criar(Servico servico) {
        if (servico == null) {
            throw new ValidacaoOrdemServicoException("Serviço não pode ser nulo");
        }
        
        ItemServico itemServico = new ItemServico();
        itemServico.id = UUID.randomUUID();
        itemServico.servico = servico;
        itemServico.status = StatusItemServico.PENDENTE;
        itemServico.observacoes = null;
        itemServico.valorServico = servico.getPreco();
        itemServico.valorMro = BigDecimal.ZERO;
        
        return itemServico;
    }
    
    // Factory method para reconstrução a partir de dados persistidos
    public static ItemServico reconstruir(UUID id, Servico servico, StatusItemServico status, 
                                          String observacoes, BigDecimal valorServico, BigDecimal valorMro) {
        ItemServico itemServico = new ItemServico();
        itemServico.id = id;
        itemServico.servico = servico;
        itemServico.status = status;
        itemServico.observacoes = observacoes;
        itemServico.valorServico = valorServico;
        itemServico.valorMro = valorMro;
        
        return itemServico;
    }
    
    // Comportamentos de negócio
    public void adicionarMRO(MRO mro, Integer quantidade) {
        if (mro == null) {
            throw new ValidacaoOrdemServicoException("MRO não pode ser nulo");
        }
        if (quantidade == null || quantidade <= 0) {
            throw new ValidacaoOrdemServicoException("Quantidade deve ser maior que zero");
        }
        
        ItemMRO itemMRO = ItemMRO.criar(mro, quantidade);
        mrosServicos.add(itemMRO);
        atualizarValorMRO();
    }
    
    // Método para adicionar ItemMRO já construído (usado em reconstrução)
    public void adicionarMRO(ItemMRO itemMRO) {
        if (itemMRO == null) {
            throw new ValidacaoOrdemServicoException("ItemMRO não pode ser nulo");
        }
        mrosServicos.add(itemMRO);
        atualizarValorMRO();
    }
    
    public void removerMRO(UUID itemMroId) {
        mrosServicos.removeIf(item -> item.getId().equals(itemMroId));
        atualizarValorMRO();
    }
    
    public void atualizarStatus(StatusItemServico novoStatus) {
        if (novoStatus == null) {
            throw new ValidacaoOrdemServicoException("Status não pode ser nulo");
        }
        this.status = novoStatus;
    }
    
    public void atualizarObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
    
    public void atualizarValorMRO() {
        BigDecimal totalMro = mrosServicos.stream()
                .map(ItemMRO::calcularValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.valorMro = totalMro;
    }
    
    public void atualizarValorServico() {
        if (servico != null) {
            this.valorServico = servico.getPreco();
        }
    }
    
    public BigDecimal calcularValorTotal() {
        return valorServico.add(valorMro);
    }
    
    // Setter para JPA e uso interno
    public void setOrdemServico(OrdemServico ordemServico) {
        this.ordemServico = ordemServico;
    }
    
    // Equals e hashCode baseados na identidade
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemServico that = (ItemServico) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
