package com.techchallenge.oficina.os.infrastructure.acl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO de integração para MRO (Anti-Corruption Layer)
 * Usado pelo contexto OS para consumir MRO do contexto administrativo
 * sem dependência direta do domínio externo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MROIntegrationDto {
    
    private UUID id;
    private String nome;
    private String descricao;
    private String tipo; // PECA ou INSUMO
    private Integer quantidadeEstoque;
    private BigDecimal precoUnitario;
    private Boolean ativo;
    
    public boolean isPeca() {
        return "PECA".equals(tipo);
    }
    
    public boolean isInsumo() {
        return "INSUMO".equals(tipo);
    }
    
    public boolean temEstoqueSuficiente(Integer quantidade) {
        return quantidadeEstoque != null && quantidadeEstoque >= quantidade;
    }
}
