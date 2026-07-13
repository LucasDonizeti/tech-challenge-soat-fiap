package com.techchallenge.oficina.os.application.usecases.ports.input;

import com.techchallenge.oficina.os.application.usecases.commands.IniciarServicoCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;

public interface IniciarServicoInput {
    OrdemServicoResponse execute(IniciarServicoCommand command);
}
