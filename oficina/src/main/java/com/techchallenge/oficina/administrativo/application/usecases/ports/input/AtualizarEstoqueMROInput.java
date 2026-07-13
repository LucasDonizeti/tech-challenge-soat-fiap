package com.techchallenge.oficina.administrativo.application.usecases.ports.input;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarEstoqueMROCommand;
import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;

public interface AtualizarEstoqueMROInput {
    MROResponse execute(AtualizarEstoqueMROCommand command);
}
