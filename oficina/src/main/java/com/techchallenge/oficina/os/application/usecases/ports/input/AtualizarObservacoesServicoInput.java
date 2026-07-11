package com.techchallenge.oficina.os.application.usecases.ports.input;

import com.techchallenge.oficina.os.application.usecases.commands.AtualizarObservacoesServicoCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;

public interface AtualizarObservacoesServicoInput {
    OrdemServicoResponse execute(AtualizarObservacoesServicoCommand command);
}
