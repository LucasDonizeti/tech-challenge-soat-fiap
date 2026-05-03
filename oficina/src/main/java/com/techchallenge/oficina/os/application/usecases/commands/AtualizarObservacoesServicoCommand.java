package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AtualizarObservacoesServicoCommand {
    
    private final UUID ordemServicoId;
    private final UUID itemServicoId;
    private final String observacoes;
    
    public AtualizarObservacoesServicoCommand(UUID ordemServicoId, UUID itemServicoId, String observacoes) {
        validate(ordemServicoId, itemServicoId);
        
        this.ordemServicoId = ordemServicoId;
        this.itemServicoId = itemServicoId;
        this.observacoes = observacoes;
    }
    
    private void validate(UUID ordemServicoId, UUID itemServicoId) {
        if (ordemServicoId == null) {
            throw new ValidacaoOrdemServicoException("Ordem de Serviço ID é obrigatório");
        }
        if (itemServicoId == null) {
            throw new ValidacaoOrdemServicoException("Item de Serviço ID é obrigatório");
        }
    }
}
