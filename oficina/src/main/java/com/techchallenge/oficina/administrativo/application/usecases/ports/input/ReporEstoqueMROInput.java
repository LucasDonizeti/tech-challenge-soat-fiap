package com.techchallenge.oficina.administrativo.application.usecases.ports.input;

import com.techchallenge.oficina.administrativo.application.usecases.commands.ReporEstoqueMROCommand;
import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;

public interface ReporEstoqueMROInput {
    MROResponse execute(ReporEstoqueMROCommand command);
}
