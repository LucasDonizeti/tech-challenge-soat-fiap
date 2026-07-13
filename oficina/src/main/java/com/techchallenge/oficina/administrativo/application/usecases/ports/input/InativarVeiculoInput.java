package com.techchallenge.oficina.administrativo.application.usecases.ports.input;

import com.techchallenge.oficina.administrativo.application.usecases.responses.VeiculoResponse;

import java.util.UUID;

public interface InativarVeiculoInput {
    VeiculoResponse execute(UUID veiculoId);
}
