package com.techchallenge.oficina.os.application.usecases.ports.input;

import com.techchallenge.oficina.os.application.usecases.commands.EntregarOrdemServicoCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;

public interface EntregarOrdemServicoInput {
    OrdemServicoResponse execute(EntregarOrdemServicoCommand command);
}
