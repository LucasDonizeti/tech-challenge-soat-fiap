package com.techchallenge.oficina.os.application.usecases.ports.input;

import com.techchallenge.oficina.os.application.usecases.commands.AprovarOrcamentoCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;

public interface AprovarOrcamentoInput {
    OrdemServicoResponse execute(AprovarOrcamentoCommand command);
}
