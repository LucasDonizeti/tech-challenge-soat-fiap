package com.techchallenge.oficina.administrativo.domain.events;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class MROInativadoEvent {
    
    private final UUID mroId;
    private final LocalDateTime ocorridoEm;
    
    public MROInativadoEvent(UUID mroId) {
        this.mroId = mroId;
        this.ocorridoEm = LocalDateTime.now();
    }
}
