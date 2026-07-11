package com.techchallenge.oficina.administrativo.application.usecases.ports.input;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarDadosServicoCommand;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;

import java.util.UUID;

public interface AtualizarDadosServicoInput {
    ServicoResponse execute(UUID servicoId, AtualizarDadosServicoCommand command);
}
