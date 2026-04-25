package com.techchallenge.oficina.os.web.dto;

import com.techchallenge.oficina.os.application.usecases.commands.CriarOrdemServicoCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CriarOrdemServicoRequest {
    
    @NotNull(message = "Cliente ID é obrigatório")
    private UUID clienteId;
    
    @NotNull(message = "Veículo ID é obrigatório")
    private UUID veiculoId;
    
    public CriarOrdemServicoCommand toCommand() {
        return new CriarOrdemServicoCommand(clienteId, veiculoId);
    }
}
