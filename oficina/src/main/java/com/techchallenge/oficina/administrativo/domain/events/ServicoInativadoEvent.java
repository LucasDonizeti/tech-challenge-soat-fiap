package com.techchallenge.oficina.administrativo.domain.events;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ServicoInativadoEvent {
    
    private final UUID servicoId;
    private final LocalDateTime ocorridoEm;
    
    public ServicoInativadoEvent(UUID servicoId) {
        this.servicoId = servicoId;
        this.ocorridoEm = LocalDateTime.now();
    }
}
