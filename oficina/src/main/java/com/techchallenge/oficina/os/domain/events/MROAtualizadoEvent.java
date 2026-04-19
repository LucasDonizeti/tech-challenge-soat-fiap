package com.techchallenge.oficina.os.domain.events;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class MROAtualizadoEvent {
    
    private final UUID mroId;
    private final String nome;
    private final LocalDateTime ocorridoEm;
    
    public MROAtualizadoEvent(UUID mroId, String nome) {
        this.mroId = mroId;
        this.nome = nome;
        this.ocorridoEm = LocalDateTime.now();
    }
}
