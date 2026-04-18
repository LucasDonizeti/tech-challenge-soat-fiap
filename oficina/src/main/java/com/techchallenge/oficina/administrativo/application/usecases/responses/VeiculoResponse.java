package com.techchallenge.oficina.administrativo.application.usecases.responses;

import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusVeiculo;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class VeiculoResponse {

    private final UUID id;
    private final String placa;
    private final String marca;
    private final String modelo;
    private final Integer ano;
    private final String cor;
    private final StatusVeiculo status;
    private final UUID clienteId;
    private final String clienteNome;
    private final LocalDateTime criadoEm;
    private final LocalDateTime atualizadoEm;

    public static VeiculoResponse from(Veiculo veiculo) {
        if (veiculo == null) {
            return null;
        }

        return VeiculoResponse.builder()
                .id(veiculo.getId())
                .placa(veiculo.getPlaca().getFormatada())
                .marca(veiculo.getMarca())
                .modelo(veiculo.getModelo())
                .ano(veiculo.getAno())
                .cor(veiculo.getCor())
                .status(veiculo.getStatus())
                .clienteId(veiculo.getCliente() != null ? veiculo.getCliente().getId() : null)
                .clienteNome(veiculo.getCliente() != null ? veiculo.getCliente().getNome().getValor() : null)
                .criadoEm(veiculo.getCriadoEm())
                .atualizadoEm(veiculo.getAtualizadoEm())
                .build();
    }

    public String getDescricaoCompleta() {
        return String.format("%s %s %d (%s)", marca, modelo, ano, placa);
    }
}
