package com.techchallenge.oficina.administrativo.application.usecases.ports.input;

import com.techchallenge.oficina.administrativo.application.usecases.commands.CriarClienteCommand;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;

public interface CriarClienteInput {
    ClienteResponse execute(CriarClienteCommand command);
}
