package com.techchallenge.oficina.administrativo.domain.events;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ServicoAtivadoEvent {
    
    private final UUID servicoId;
    private final LocalDateTime ocorridoEm;
    
    public ServicoAtivadoEvent(UUID servicoId) {
        this.servicoId = servicoId;
        this.ocorridoEm = LocalDateTime.now();
    }
}
