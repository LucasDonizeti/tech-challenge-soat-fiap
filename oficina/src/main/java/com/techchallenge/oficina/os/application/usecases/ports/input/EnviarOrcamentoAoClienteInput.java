package com.techchallenge.oficina.os.application.usecases.ports.input;

import com.techchallenge.oficina.os.application.usecases.commands.EnviarOrcamentoAoClienteCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;

public interface EnviarOrcamentoAoClienteInput {
    OrdemServicoResponse execute(EnviarOrcamentoAoClienteCommand command);
}
