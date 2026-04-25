package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AdicionarServicoOrdemCommand {
    
    private final UUID ordemServicoId;
    private final UUID servicoId;
    
    public AdicionarServicoOrdemCommand(UUID ordemServicoId, UUID servicoId) {
        validate(ordemServicoId, servicoId);
        
        this.ordemServicoId = ordemServicoId;
        this.servicoId = servicoId;
    }
    
    private void validate(UUID ordemServicoId, UUID servicoId) {
        if (ordemServicoId == null) {
            throw new ValidacaoOrdemServicoException("Ordem de Serviço ID é obrigatório");
        }
        if (servicoId == null) {
            throw new ValidacaoOrdemServicoException("Serviço ID é obrigatório");
        }
    }
}
