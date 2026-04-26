package com.techchallenge.oficina.os.application.usecases.responses;

import com.techchallenge.oficina.os.domain.model.entities.ItemMRO;
import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusItemServico;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemServicoResponse {
    
    private UUID id;
    private UUID servicoId;
    private String servicoNome;
    private String servicoDescricao;
    private StatusItemServico status;
    private String observacoes;
    private BigDecimal valorServico;
    private BigDecimal valorMro;
    private BigDecimal valorTotal;
    private List<ItemMROResponse> mros;
    
    public static ItemServicoResponse from(ItemServico itemServico) {
        if (itemServico == null) {
            return null;
        }
        
        List<ItemMROResponse> mroResponses = itemServico.getMrosServicos() != null ?
                itemServico.getMrosServicos().stream()
                        .map(ItemMROResponse::from)
                        .collect(Collectors.toList()) :
                List.of();
        
        return ItemServicoResponse.builder()
                .id(itemServico.getId())
                .servicoId(itemServico.getServicoId())
                .servicoNome(itemServico.getServicoNome())
                .servicoDescricao(itemServico.getServicoDescricao())
                .status(itemServico.getStatus())
                .observacoes(itemServico.getObservacoes())
                .valorServico(itemServico.getValorServico())
                .valorMro(itemServico.getValorMro())
                .valorTotal(itemServico.calcularValorTotal())
                .mros(mroResponses)
                .build();
    }
}
