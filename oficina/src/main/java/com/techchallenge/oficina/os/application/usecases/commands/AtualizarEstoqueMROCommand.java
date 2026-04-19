package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoMROException;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AtualizarEstoqueMROCommand {
    
    private final UUID id;
    private final Integer quantidadeEstoque;
    
    public AtualizarEstoqueMROCommand(UUID id, Integer quantidadeEstoque) {
        if (id == null) {
            throw new ValidacaoMROException("ID é obrigatório");
        }
        if (quantidadeEstoque == null) {
            throw new ValidacaoMROException("Quantidade de estoque é obrigatória");
        }
        if (quantidadeEstoque < 0) {
            throw new ValidacaoMROException("Quantidade de estoque não pode ser negativa");
        }
        
        this.id = id;
        this.quantidadeEstoque = quantidadeEstoque;
    }
}
