package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoMROException;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DebitarEstoqueMROCommand {
    
    private final UUID id;
    private final Integer quantidade;
    
    public DebitarEstoqueMROCommand(UUID id, Integer quantidade) {
        if (id == null) {
            throw new ValidacaoMROException("ID é obrigatório");
        }
        if (quantidade == null) {
            throw new ValidacaoMROException("Quantidade é obrigatória");
        }
        if (quantidade <= 0) {
            throw new ValidacaoMROException("Quantidade deve ser maior que zero");
        }
        
        this.id = id;
        this.quantidade = quantidade;
    }
}
