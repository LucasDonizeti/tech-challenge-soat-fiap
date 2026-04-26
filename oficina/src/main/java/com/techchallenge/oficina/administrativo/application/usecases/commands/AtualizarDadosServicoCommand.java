package com.techchallenge.oficina.administrativo.application.usecases.commands;

import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoServicoException;
import lombok.Getter;

@Getter
public class AtualizarDadosServicoCommand {
    
    private final String nome;
    private final String descricao;
    
    public AtualizarDadosServicoCommand(String nome, String descricao) {
        if (nome == null || nome.isBlank()) {
            throw new ValidacaoServicoException("Nome é obrigatório");
        }
        this.nome = nome;
        this.descricao = descricao;
    }
}
