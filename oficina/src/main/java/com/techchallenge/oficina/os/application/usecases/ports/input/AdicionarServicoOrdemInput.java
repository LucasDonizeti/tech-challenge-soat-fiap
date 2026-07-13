package com.techchallenge.oficina.os.application.usecases.ports.input;

import com.techchallenge.oficina.os.application.usecases.commands.AdicionarServicoOrdemCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;

public interface AdicionarServicoOrdemInput {
    OrdemServicoResponse execute(AdicionarServicoOrdemCommand command);
}
