package com.techchallenge.oficina.os.web.dto;

import com.techchallenge.oficina.os.application.usecases.commands.AdicionarMROServicoCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class AdicionarMROServicoRequest {
    
    @NotNull(message = "Item de Serviço ID é obrigatório")
    private UUID itemServicoId;
    
    @NotNull(message = "MRO ID é obrigatório")
    private UUID mroId;
    
    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser maior que zero")
    private Integer quantidade;
    
    public AdicionarMROServicoCommand toCommand(UUID ordemServicoId) {
        return new AdicionarMROServicoCommand(ordemServicoId, itemServicoId, mroId, quantidade);
    }
}
