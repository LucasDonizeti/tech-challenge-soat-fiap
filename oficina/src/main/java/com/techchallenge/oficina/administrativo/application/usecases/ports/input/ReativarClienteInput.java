package com.techchallenge.oficina.administrativo.application.usecases.ports.input;

import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;

import java.util.UUID;

public interface ReativarClienteInput {
    ClienteResponse execute(UUID clienteId);
}
