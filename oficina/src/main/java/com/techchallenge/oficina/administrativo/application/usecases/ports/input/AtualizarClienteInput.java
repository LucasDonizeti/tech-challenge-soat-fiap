package com.techchallenge.oficina.administrativo.application.usecases.ports.input;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarClienteCommand;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;

import java.util.UUID;

public interface AtualizarClienteInput {
    ClienteResponse execute(UUID clienteId, AtualizarClienteCommand command);
}
