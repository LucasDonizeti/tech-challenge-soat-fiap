package com.techchallenge.oficina.os.application.usecases.ports.input;

import com.techchallenge.oficina.os.application.usecases.commands.ConcluirServicoCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;

public interface ConcluirServicoInput {
    OrdemServicoResponse execute(ConcluirServicoCommand command);
}
