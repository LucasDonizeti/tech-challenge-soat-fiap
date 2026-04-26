package com.techchallenge.oficina.administrativo.application.usecases.commands;

import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoMROException;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.TipoMRO;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AtualizarDadosMROCommand {
    
    private final UUID id;
    private final String nome;
    private final String descricao;
    private final TipoMRO tipo;
    
    public AtualizarDadosMROCommand(UUID id, String nome, String descricao, TipoMRO tipo) {
        if (id == null) {
            throw new ValidacaoMROException("ID é obrigatório");
        }
        if (nome == null || nome.isBlank()) {
            throw new ValidacaoMROException("Nome é obrigatório");
        }
        
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.tipo = tipo;
    }
}
