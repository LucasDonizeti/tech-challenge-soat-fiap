package com.techchallenge.oficina.os.application.usecases.ports.input;

import com.techchallenge.oficina.os.application.usecases.commands.AdicionarMROServicoCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;

public interface AdicionarMROServicoInput {
    OrdemServicoResponse execute(AdicionarMROServicoCommand command);
}
