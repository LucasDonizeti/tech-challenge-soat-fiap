package com.techchallenge.oficina.sharedkernel.application.usecases.ports.input;

import com.techchallenge.oficina.sharedkernel.application.usecases.commands.AutenticarUsuarioCommand;
import com.techchallenge.oficina.sharedkernel.application.usecases.responses.AuthResponse;

public interface AutenticarUsuarioInput {

    AuthResponse execute(AutenticarUsuarioCommand command);
}
