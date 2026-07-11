package com.techchallenge.oficina.administrativo.application.usecases.ports.input;

import com.techchallenge.oficina.administrativo.application.usecases.responses.VeiculoResponse;

import java.util.List;
import java.util.UUID;

public interface BuscarVeiculosPorClienteInput {
    List<VeiculoResponse> execute(UUID clienteId);
}
