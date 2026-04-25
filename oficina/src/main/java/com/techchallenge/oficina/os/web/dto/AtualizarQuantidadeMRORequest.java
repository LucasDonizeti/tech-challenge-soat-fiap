package com.techchallenge.oficina.os.web.dto;

import com.techchallenge.oficina.os.application.usecases.commands.AtualizarQuantidadeMROCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class AtualizarQuantidadeMRORequest {
    
    @NotNull(message = "Item de Serviço ID é obrigatório")
    private UUID itemServicoId;
    
    @NotNull(message = "Item de MRO ID é obrigatório")
    private UUID itemMroId;
    
    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser maior que zero")
    private Integer quantidade;
    
    public AtualizarQuantidadeMROCommand toCommand(UUID ordemServicoId) {
        return new AtualizarQuantidadeMROCommand(ordemServicoId, itemServicoId, itemMroId, quantidade);
    }
}
