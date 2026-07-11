package com.techchallenge.oficina.administrativo.application.usecases.ports.input;

import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarMROsInput {
    Page<MROResponse> execute(Pageable pageable);
}
