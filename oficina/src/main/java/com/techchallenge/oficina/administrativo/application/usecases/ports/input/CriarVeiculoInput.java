package com.techchallenge.oficina.administrativo.application.usecases.ports.input;

import com.techchallenge.oficina.administrativo.application.usecases.commands.CriarVeiculoCommand;
import com.techchallenge.oficina.administrativo.application.usecases.responses.VeiculoResponse;

public interface CriarVeiculoInput {
    VeiculoResponse execute(CriarVeiculoCommand command);
}
