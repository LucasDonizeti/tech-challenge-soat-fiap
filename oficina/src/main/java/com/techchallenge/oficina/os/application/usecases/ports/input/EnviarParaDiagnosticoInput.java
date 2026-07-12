package com.techchallenge.oficina.os.application.usecases.ports.input;

import com.techchallenge.oficina.os.application.usecases.commands.EnviarParaDiagnosticoCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;

public interface EnviarParaDiagnosticoInput {
    OrdemServicoResponse execute(EnviarParaDiagnosticoCommand command);
}
