package com.techchallenge.oficina.os.application.usecases.responses;

import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServicoResponse {
    
    private UUID id;
    private ClienteResponse cliente;
    private VeiculoResponse veiculo;
    private String status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataInicioExecucao;
    private LocalDateTime dataFinalizacao;
    private BigDecimal valorTotal;
    private List<ItemServicoResponse> itensServico;
    
    public static OrdemServicoResponse from(OrdemServico ordemServico) {
        if (ordemServico == null) {
            return null;
        }
        
        ClienteResponse clienteResponse = ClienteResponse.from(ordemServico.getCliente());
        VeiculoResponse veiculoResponse = VeiculoResponse.from(ordemServico.getVeiculo());
        
        List<ItemServicoResponse> itensServicoResponses = ordemServico.getItensServico() != null ?
                ordemServico.getItensServico().stream()
                        .map(ItemServicoResponse::from)
                        .collect(Collectors.toList()) :
                List.of();
        
        return OrdemServicoResponse.builder()
                .id(ordemServico.getId())
                .cliente(clienteResponse)
                .veiculo(veiculoResponse)
                .status(ordemServico.getStatus() != null ? ordemServico.getStatus().name() : null)
                .dataCriacao(ordemServico.getDataCriacao())
                .dataInicioExecucao(ordemServico.getDataInicioExecucao())
                .dataFinalizacao(ordemServico.getDataFinalizacao())
                .valorTotal(ordemServico.calcularValorTotal())
                .itensServico(itensServicoResponses)
                .build();
    }
}
