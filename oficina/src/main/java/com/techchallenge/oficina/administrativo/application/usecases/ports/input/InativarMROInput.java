package com.techchallenge.oficina.administrativo.application.usecases.ports.input;

import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;

import java.util.UUID;

public interface InativarMROInput {
    MROResponse execute(UUID id);
}
