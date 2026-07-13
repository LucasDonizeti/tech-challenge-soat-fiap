package com.techchallenge.oficina.administrativo.application.usecases.ports.input;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarDadosMROCommand;
import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;

public interface AtualizarDadosMROInput {
    MROResponse execute(AtualizarDadosMROCommand command);
}
