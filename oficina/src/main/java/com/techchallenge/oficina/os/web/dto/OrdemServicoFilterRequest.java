package com.techchallenge.oficina.os.web.dto;

import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class OrdemServicoFilterRequest {
    
    private UUID clienteId;
    private UUID veiculoId;
    private StatusOS status;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
}
