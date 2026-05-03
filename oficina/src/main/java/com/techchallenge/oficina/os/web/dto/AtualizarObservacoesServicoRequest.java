package com.techchallenge.oficina.os.web.dto;

import com.techchallenge.oficina.os.application.usecases.commands.AtualizarObservacoesServicoCommand;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtualizarObservacoesServicoRequest {
    
    @NotNull(message = "Item de Serviço ID é obrigatório")
    private UUID itemServicoId;
    
    private String observacoes;
    
    public AtualizarObservacoesServicoCommand toCommand(UUID ordemServicoId) {
        return new AtualizarObservacoesServicoCommand(ordemServicoId, itemServicoId, observacoes);
    }
}
