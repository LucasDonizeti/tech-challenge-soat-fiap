package com.techchallenge.oficina.administrativo.application.usecases.ports.input;

import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListarServicosInput {
    Page<ServicoResponse> execute(Pageable pageable);
}
