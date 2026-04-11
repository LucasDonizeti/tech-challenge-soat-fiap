package com.techchallenge.oficina.administrativo.application.usecases.commands;

import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import lombok.Getter;

@Getter
public class AtualizarClienteCommand {
    
    private final Nome nome;
    private final Email email;
    
    public AtualizarClienteCommand(String nome, String email) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        
        this.nome = Nome.of(nome);
        this.email = Email.of(email);
    }
}
