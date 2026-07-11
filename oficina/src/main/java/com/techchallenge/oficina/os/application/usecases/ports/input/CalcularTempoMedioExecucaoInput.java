package com.techchallenge.oficina.os.application.usecases.ports.input;

import com.techchallenge.oficina.os.web.dto.TempoMedioExecucaoResponseDto;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CalcularTempoMedioExecucaoInput {
    TempoMedioExecucaoResponseDto execute(UUID servicoId, LocalDateTime dataInicio, LocalDateTime dataFim);
}
