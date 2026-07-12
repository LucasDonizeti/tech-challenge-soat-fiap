package com.techchallenge.oficina.os.application.usecases.ports.input;

import com.techchallenge.oficina.os.application.usecases.commands.RemoverServicoOrdemCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;

public interface RemoverServicoOrdemInput {
    OrdemServicoResponse execute(RemoverServicoOrdemCommand command);
}
