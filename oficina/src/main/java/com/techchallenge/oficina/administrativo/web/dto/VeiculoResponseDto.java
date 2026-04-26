package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.application.usecases.responses.VeiculoResponse;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusVeiculo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(description = "Resposta de Veículo")
public class VeiculoResponseDto {

    @Schema(description = "UUID do veículo", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;
    
    @Schema(description = "Placa do veículo", example = "ABC1234")
    private String placa;
    
    @Schema(description = "Marca do veículo", example = "Toyota")
    private String marca;
    
    @Schema(description = "Modelo do veículo", example = "Corolla")
    private String modelo;
    
    @Schema(description = "Ano do veículo", example = "2020")
    private Integer ano;
    
    @Schema(description = "Cor do veículo", example = "Prata")
    private String cor;
    
    @Schema(description = "Status do veículo (ATIVO, INATIVO)", allowableValues = {"ATIVO", "INATIVO"}, example = "ATIVO")
    private StatusVeiculo status;
    
    @Schema(description = "UUID do cliente", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID clienteId;
    
    @Schema(description = "Nome do cliente", example = "João Silva")
    private String clienteNome;
    
    @Schema(description = "Data de criação", example = "2024-01-15T10:30:00")
    private LocalDateTime criadoEm;
    
    @Schema(description = "Data da última atualização", example = "2024-01-20T14:45:00")
    private LocalDateTime atualizadoEm;
    
    @Schema(description = "Descrição completa do veículo", example = "Toyota Corolla 2020 - Prata")
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
