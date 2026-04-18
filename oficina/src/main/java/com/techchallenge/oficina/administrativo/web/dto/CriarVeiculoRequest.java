package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.application.usecases.commands.CriarVeiculoCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CriarVeiculoRequest {

    @NotBlank(message = "Placa é obrigatória")
    @Schema(description = "Placa do veículo (formato ABC1234 ou ABC1D23)", example = "ABC1234", required = true)
    private String placa;

    @NotBlank(message = "Marca é obrigatória")
    @Schema(description = "Marca do veículo", example = "Toyota", required = true)
    private String marca;

    @NotBlank(message = "Modelo é obrigatório")
    @Schema(description = "Modelo do veículo", example = "Corolla", required = true)
    private String modelo;

    @NotNull(message = "Ano é obrigatório")
    @Min(value = 1900, message = "Ano deve ser maior ou igual a 1900")
    @Max(value = 2100, message = "Ano deve ser menor ou igual a 2100")
    @Schema(description = "Ano do veículo", example = "2020", required = true)
    private Integer ano;

    @Schema(description = "Cor do veículo", example = "Prata")
    private String cor;

    @NotNull(message = "Cliente ID é obrigatório")
    @Schema(description = "ID do cliente proprietário do veículo", required = true)
    private UUID clienteId;

    public CriarVeiculoCommand toCommand() {
        return new CriarVeiculoCommand(placa, marca, modelo, ano, cor, clienteId);
    }
}
