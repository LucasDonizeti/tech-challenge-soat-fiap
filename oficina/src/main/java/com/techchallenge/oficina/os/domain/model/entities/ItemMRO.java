package com.techchallenge.oficina.os.domain.model.entities;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
public class ItemMRO {
    
    private UUID id;
    private UUID mroId;
    private String mroNome;
    private String mroDescricao;
    private Integer quantidade;
    private BigDecimal valorUnitario;
    
    // Construtor padrão para JPA
    protected ItemMRO() {}
    
    // Factory method para criação com dados do MRO (ACL)
    public static ItemMRO criarComDados(UUID mroId, String nome, String descricao, BigDecimal precoUnitario, Integer quantidade) {
        if (mroId == null) {
            throw new ValidacaoOrdemServicoException("ID do MRO não pode ser nulo");
        }
        if (quantidade == null || quantidade <= 0) {
            throw new ValidacaoOrdemServicoException("Quantidade deve ser maior que zero");
        }
        
        ItemMRO itemMRO = new ItemMRO();
        itemMRO.id = UUID.randomUUID();
        itemMRO.mroId = mroId;
        itemMRO.mroNome = nome;
        itemMRO.mroDescricao = descricao;
        itemMRO.quantidade = quantidade;
        itemMRO.valorUnitario = precoUnitario;
        
        return itemMRO;
    }
    
    // Factory method para reconstrução a partir de dados persistidos
    public static ItemMRO reconstruir(UUID id, UUID mroId, String mroNome, String mroDescricao, 
                                      Integer quantidade, BigDecimal valorUnitario) {
        ItemMRO itemMRO = new ItemMRO();
        itemMRO.id = id;
        itemMRO.mroId = mroId;
        itemMRO.mroNome = mroNome;
        itemMRO.mroDescricao = mroDescricao;
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
