package com.techchallenge.oficina.os.web.dto;

import com.techchallenge.oficina.os.application.usecases.commands.AdicionarServicoOrdemCommand;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AdicionarServicoOrdemRequest {
    
    @NotNull(message = "Serviço ID é obrigatório")
    private UUID servicoId;
    
    public AdicionarServicoOrdemCommand toCommand(UUID ordemServicoId) {
        return new AdicionarServicoOrdemCommand(ordemServicoId, servicoId);
    }
}
