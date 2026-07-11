package com.techchallenge.oficina.os.application.usecases.ports.input;

import com.techchallenge.oficina.os.application.usecases.commands.CriarOrdemServicoCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;

public interface CriarOrdemServicoInpuit {
    OrdemServicoResponse execute(CriarOrdemServicoCommand command);
}
