package com.techchallenge.oficina.os.application.usecases.ports.input;

import com.techchallenge.oficina.os.application.usecases.commands.RemoverMROServicoCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;

public interface RemoverMROServicoInput {
    OrdemServicoResponse execute(RemoverMROServicoCommand command);
}
