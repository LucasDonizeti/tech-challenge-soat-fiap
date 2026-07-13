package com.techchallenge.oficina.administrativo.application.usecases.ports.input;

import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;

import java.util.UUID;

public interface BuscarMROInput {
    MROResponse execute(UUID id);
}
