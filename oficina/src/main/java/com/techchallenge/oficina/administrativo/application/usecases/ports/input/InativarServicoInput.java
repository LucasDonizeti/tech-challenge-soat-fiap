package com.techchallenge.oficina.administrativo.application.usecases.ports.input;

import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;

import java.util.UUID;

public interface InativarServicoInput {
    ServicoResponse execute(UUID servicoId);
}
