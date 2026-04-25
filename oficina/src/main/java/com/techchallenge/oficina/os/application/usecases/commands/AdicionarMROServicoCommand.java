package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AdicionarMROServicoCommand {
    
    private final UUID ordemServicoId;
    private final UUID itemServicoId;
    private final UUID mroId;
    private final Integer quantidade;
    
    public AdicionarMROServicoCommand(UUID ordemServicoId, UUID itemServicoId, UUID mroId, Integer quantidade) {
        validate(ordemServicoId, itemServicoId, mroId, quantidade);
        
        this.ordemServicoId = ordemServicoId;
        this.itemServicoId = itemServicoId;
        this.mroId = mroId;
        this.quantidade = quantidade;
    }
    
    private void validate(UUID ordemServicoId, UUID itemServicoId, UUID mroId, Integer quantidade) {
        if (ordemServicoId == null) {
            throw new ValidacaoOrdemServicoException("Ordem de Serviço ID é obrigatório");
        }
        if (itemServicoId == null) {
            throw new ValidacaoOrdemServicoException("Item de Serviço ID é obrigatório");
        }
        if (mroId == null) {
            throw new ValidacaoOrdemServicoException("MRO ID é obrigatório");
        }
        if (quantidade == null || quantidade <= 0) {
            throw new ValidacaoOrdemServicoException("Quantidade deve ser maior que zero");
        }
    }
}
