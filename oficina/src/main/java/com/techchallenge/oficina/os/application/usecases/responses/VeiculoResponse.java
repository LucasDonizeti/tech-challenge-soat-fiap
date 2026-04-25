package com.techchallenge.oficina.os.application.usecases.responses;

import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VeiculoResponse {
    
    private UUID id;
    private String placa;
    private String marca;
    private String modelo;
    private Integer ano;
    private String cor;
    private String status;
    
    public static VeiculoResponse from(Veiculo veiculo) {
        if (veiculo == null) {
            return null;
        }
        
        return VeiculoResponse.builder()
                .id(veiculo.getId())
                .placa(veiculo.getPlaca() != null ? veiculo.getPlaca().getFormatada() : null)
                .marca(veiculo.getMarca())
                .modelo(veiculo.getModelo())
                .ano(veiculo.getAno())
                .cor(veiculo.getCor())
                .status(veiculo.getStatus() != null ? veiculo.getStatus().name() : null)
                .build();
    }
}
