package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.application.usecases.responses.VeiculoResponse;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusVeiculo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class VeiculoResponseDto {

    private UUID id;
    private String placa;
    private String marca;
    private String modelo;
    private Integer ano;
    private String cor;
    private StatusVeiculo status;
    private UUID clienteId;
    private String clienteNome;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private String descricaoCompleta;

    public static VeiculoResponseDto from(VeiculoResponse response) {
        if (response == null) {
            return null;
        }

        VeiculoResponseDto dto = new VeiculoResponseDto();
        dto.setId(response.getId());
        dto.setPlaca(response.getPlaca());
        dto.setMarca(response.getMarca());
        dto.setModelo(response.getModelo());
        dto.setAno(response.getAno());
        dto.setCor(response.getCor());
        dto.setStatus(response.getStatus());
        dto.setClienteId(response.getClienteId());
        dto.setClienteNome(response.getClienteNome());
        dto.setCriadoEm(response.getCriadoEm());
        dto.setAtualizadoEm(response.getAtualizadoEm());
        dto.setDescricaoCompleta(response.getDescricaoCompleta());

        return dto;
    }
}
