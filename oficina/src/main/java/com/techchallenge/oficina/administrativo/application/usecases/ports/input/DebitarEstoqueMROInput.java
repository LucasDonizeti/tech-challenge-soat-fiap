package com.techchallenge.oficina.administrativo.application.usecases.ports.input;

import com.techchallenge.oficina.administrativo.application.usecases.commands.DebitarEstoqueMROCommand;
import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;

public interface DebitarEstoqueMROInput {
    MROResponse execute(DebitarEstoqueMROCommand command);
}
