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
    private String mroTipo;
    private Integer quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    
    public static ItemMROResponse from(ItemMRO itemMRO) {
        if (itemMRO == null) {
            return null;
        }
        
        String mroNome = itemMRO.getMro() != null ? itemMRO.getMro().getNome() : null;
        String mroTipo = itemMRO.getMro() != null && itemMRO.getMro().getTipo() != null ? 
                itemMRO.getMro().getTipo().name() : null;
        
        return ItemMROResponse.builder()
                .id(itemMRO.getId())
                .mroId(itemMRO.getMro() != null ? itemMRO.getMro().getId() : null)
                .mroNome(mroNome)
                .mroTipo(mroTipo)
                .quantidade(itemMRO.getQuantidade())
                .valorUnitario(itemMRO.getValorUnitario())
                .valorTotal(itemMRO.calcularValorTotal())
                .build();
    }
}
