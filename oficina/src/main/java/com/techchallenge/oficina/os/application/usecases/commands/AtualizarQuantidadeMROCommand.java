package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AtualizarQuantidadeMROCommand {
    
    private final UUID ordemServicoId;
    private final UUID itemServicoId;
    private final UUID itemMroId;
    private final Integer novaQuantidade;
    
    public AtualizarQuantidadeMROCommand(UUID ordemServicoId, UUID itemServicoId, UUID itemMroId, Integer novaQuantidade) {
        validate(ordemServicoId, itemServicoId, itemMroId, novaQuantidade);
        
        this.ordemServicoId = ordemServicoId;
        this.itemServicoId = itemServicoId;
        this.itemMroId = itemMroId;
        this.novaQuantidade = novaQuantidade;
    }
    
    private void validate(UUID ordemServicoId, UUID itemServicoId, UUID itemMroId, Integer novaQuantidade) {
        if (ordemServicoId == null) {
            throw new ValidacaoOrdemServicoException("Ordem de Serviço ID é obrigatório");
        }
        if (itemServicoId == null) {
            throw new ValidacaoOrdemServicoException("Item de Serviço ID é obrigatório");
        }
        if (itemMroId == null) {
            throw new ValidacaoOrdemServicoException("Item de MRO ID é obrigatório");
        }
        if (novaQuantidade == null || novaQuantidade <= 0) {
            throw new ValidacaoOrdemServicoException("Quantidade deve ser maior que zero");
        }
    }
}
