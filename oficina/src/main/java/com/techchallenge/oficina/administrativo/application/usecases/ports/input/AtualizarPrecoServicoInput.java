package com.techchallenge.oficina.administrativo.application.usecases.ports.input;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarPrecoServicoCommand;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;

import java.util.UUID;

public interface AtualizarPrecoServicoInput {
    ServicoResponse execute(UUID servicoId, AtualizarPrecoServicoCommand command);
}
