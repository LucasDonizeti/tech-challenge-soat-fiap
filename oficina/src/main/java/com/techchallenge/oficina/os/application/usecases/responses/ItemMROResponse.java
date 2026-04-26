package com.techchallenge.oficina.os.application.usecases.responses;

import com.techchallenge.oficina.os.domain.model.entities.ItemMRO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemMROResponse {
    
    private UUID id;
    private UUID mroId;
    private String mroNome;
    private String mroDescricao;
    private Integer quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    
    public static ItemMROResponse from(ItemMRO itemMRO) {
        if (itemMRO == null) {
            return null;
        }
        
        return ItemMROResponse.builder()
                .id(itemMRO.getId())
                .mroId(itemMRO.getMroId())
                .mroNome(itemMRO.getMroNome())
                .mroDescricao(itemMRO.getMroDescricao())
                .quantidade(itemMRO.getQuantidade())
                .valorUnitario(itemMRO.getValorUnitario())
                .valorTotal(itemMRO.calcularValorTotal())
                .build();
    }
}
