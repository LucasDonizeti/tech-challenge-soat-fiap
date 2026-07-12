package com.techchallenge.oficina.administrativo.application.usecases.ports.input;

import com.techchallenge.oficina.administrativo.application.usecases.commands.CriarServicoCommand;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;

public interface CriarServicoInput {
    ServicoResponse execute(CriarServicoCommand command);
}
