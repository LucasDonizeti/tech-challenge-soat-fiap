package com.techchallenge.oficina.administrativo.application.usecases.ports.input;

import com.techchallenge.oficina.administrativo.application.usecases.commands.CriarMROCommand;
import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;

public interface CriarMROInput {
    MROResponse execute(CriarMROCommand command);
}
