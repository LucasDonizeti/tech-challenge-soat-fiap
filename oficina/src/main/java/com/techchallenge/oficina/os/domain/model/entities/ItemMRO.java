package com.techchallenge.oficina.os.domain.model.entities;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import lombok.Getter;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "itens_mro")
@Getter
public class ItemMRO {
    
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mro_id", nullable = false)
    private MRO mro;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_servico_id", nullable = false)
    private ItemServico itemServico;
    
    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;
    
    @Column(name = "valor_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorUnitario;
    
    // Construtor padrão para JPA
    protected ItemMRO() {}
    
    // Factory method para criação
    public static ItemMRO criar(MRO mro, Integer quantidade) {
        if (mro == null) {
            throw new ValidacaoOrdemServicoException("MRO não pode ser nulo");
        }
        if (quantidade == null || quantidade <= 0) {
            throw new ValidacaoOrdemServicoException("Quantidade deve ser maior que zero");
        }
        
        ItemMRO itemMRO = new ItemMRO();
        itemMRO.id = UUID.randomUUID();
        itemMRO.mro = mro;
        itemMRO.quantidade = quantidade;
        itemMRO.valorUnitario = mro.getPrecoUnitario();
        
        return itemMRO;
    }
    
    // Factory method para reconstrução a partir de dados persistidos
    public static ItemMRO reconstruir(UUID id, MRO mro, Integer quantidade, BigDecimal valorUnitario) {
        ItemMRO itemMRO = new ItemMRO();
        itemMRO.id = id;
        itemMRO.mro = mro;
        itemMRO.quantidade = quantidade;
        itemMRO.valorUnitario = valorUnitario;
        
        return itemMRO;
    }
    
    // Comportamentos de negócio
    public void atualizarQuantidade(Integer novaQuantidade) {
        if (novaQuantidade == null || novaQuantidade <= 0) {
            throw new ValidacaoOrdemServicoException("Quantidade deve ser maior que zero");
        }
        this.quantidade = novaQuantidade;
    }
    
    public BigDecimal calcularValorTotal() {
        return valorUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
    
    // Equals e hashCode baseados na identidade
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemMRO itemMRO = (ItemMRO) o;
        return Objects.equals(id, itemMRO.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
