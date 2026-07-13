package com.techchallenge.oficina.administrativo.application.usecases.ports.input;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarPrecoMROCommand;
import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;

public interface AtualizarPrecoMROInput {
    MROResponse execute(AtualizarPrecoMROCommand command);
}
