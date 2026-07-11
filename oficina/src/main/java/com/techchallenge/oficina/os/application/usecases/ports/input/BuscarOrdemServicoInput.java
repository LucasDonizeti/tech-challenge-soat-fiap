package com.techchallenge.oficina.os.application.usecases.ports.input;

import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;

import java.util.UUID;

public interface BuscarOrdemServicoInput {
    OrdemServicoResponse execute(UUID ordemServicoId);
}
