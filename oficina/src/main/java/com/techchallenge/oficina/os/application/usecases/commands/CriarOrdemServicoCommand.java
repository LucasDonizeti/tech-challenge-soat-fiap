package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CriarOrdemServicoCommand {
    
    private final UUID clienteId;
    private final UUID veiculoId;
    
    public CriarOrdemServicoCommand(UUID clienteId, UUID veiculoId) {
        validate(clienteId, veiculoId);
        
        this.clienteId = clienteId;
        this.veiculoId = veiculoId;
    }
    
    private void validate(UUID clienteId, UUID veiculoId) {
        if (clienteId == null) {
            throw new ValidacaoOrdemServicoException("Cliente ID é obrigatório");
        }
        if (veiculoId == null) {
            throw new ValidacaoOrdemServicoException("Veículo ID é obrigatório");
        }
    }
}
