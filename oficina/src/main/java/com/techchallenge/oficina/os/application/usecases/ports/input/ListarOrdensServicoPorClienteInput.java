package com.techchallenge.oficina.os.application.usecases.ports.input;

import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ListarOrdensServicoPorClienteInput {
    Page<OrdemServicoResponse> execute(UUID clienteId, org.springframework.data.domain.Pageable pageable);
}
