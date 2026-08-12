package com.techchallenge.oficina.os.application.usecases.ports.input;

import com.techchallenge.oficina.os.application.usecases.commands.RecusarOrcamentoCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;

public interface RecusarOrcamentoInput {
    OrdemServicoResponse execute(RecusarOrcamentoCommand command);
}
