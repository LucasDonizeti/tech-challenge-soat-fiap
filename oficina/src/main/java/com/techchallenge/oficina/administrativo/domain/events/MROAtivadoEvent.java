package com.techchallenge.oficina.administrativo.domain.events;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class MROAtivadoEvent {
    
    private final UUID mroId;
    private final LocalDateTime ocorridoEm;
    
    public MROAtivadoEvent(UUID mroId) {
        this.mroId = mroId;
        this.ocorridoEm = LocalDateTime.now();
    }
}
