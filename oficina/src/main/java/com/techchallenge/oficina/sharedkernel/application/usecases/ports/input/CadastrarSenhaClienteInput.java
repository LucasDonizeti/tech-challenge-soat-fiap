package com.techchallenge.oficina.sharedkernel.application.usecases.ports.input;

import com.techchallenge.oficina.sharedkernel.application.usecases.commands.CadastrarSenhaClienteCommand;

public interface CadastrarSenhaClienteInput {

    void execute(CadastrarSenhaClienteCommand command);
}
