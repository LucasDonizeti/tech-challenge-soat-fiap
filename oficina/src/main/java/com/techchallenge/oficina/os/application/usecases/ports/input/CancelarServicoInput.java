package com.techchallenge.oficina.os.application.usecases.ports.input;

import com.techchallenge.oficina.os.application.usecases.commands.CancelarServicoCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;

public interface CancelarServicoInput {
    OrdemServicoResponse execute(CancelarServicoCommand command);
}
