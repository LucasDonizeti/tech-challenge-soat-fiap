package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import lombok.Getter;

import java.util.UUID;

@Getter
public class EnviarOrcamentoAoClienteCommand {
    
    private final UUID ordemServicoId;
    
    public EnviarOrcamentoAoClienteCommand(UUID ordemServicoId) {
        validate(ordemServicoId);
        
        this.ordemServicoId = ordemServicoId;
    }
    
    private void validate(UUID ordemServicoId) {
        if (ordemServicoId == null) {
            throw new ValidacaoOrdemServicoException("Ordem de Serviço ID é obrigatório");
        }
    }
}
