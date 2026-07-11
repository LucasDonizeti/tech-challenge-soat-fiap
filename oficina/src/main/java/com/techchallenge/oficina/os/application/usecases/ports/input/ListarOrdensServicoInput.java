package com.techchallenge.oficina.os.application.usecases.ports.input;

import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ListarOrdensServicoInput {
    Page<OrdemServicoResponse> execute(UUID clienteId, UUID veiculoId, StatusOS status,
                                       LocalDateTime dataInicio, LocalDateTime dataFim,
                                       Pageable pageable);
}
