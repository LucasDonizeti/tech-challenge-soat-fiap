package com.techchallenge.oficina.administrativo.application.usecases.ports.input;

import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.administrativo.web.dto.ClienteFilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BuscarClientesPorFiltroInput {
    Page<ClienteResponse> execute(ClienteFilterRequest filter, Pageable pageable);
}
