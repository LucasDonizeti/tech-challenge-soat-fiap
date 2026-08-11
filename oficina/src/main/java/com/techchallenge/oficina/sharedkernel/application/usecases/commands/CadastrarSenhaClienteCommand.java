package com.techchallenge.oficina.sharedkernel.application.usecases.commands;

public class CadastrarSenhaClienteCommand {

    private final String documento;
    private final String senha;

    public CadastrarSenhaClienteCommand(String documento, String senha) {
        this.documento = documento;
        this.senha = senha;
    }

    public String getDocumento() {
        return documento;
    }

    public String getSenha() {
        return senha;
    }
}
