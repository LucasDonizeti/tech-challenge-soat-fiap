package com.techchallenge.oficina.administrativo.domain.events;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ServicoAtualizadoEvent {
    
    private final UUID servicoId;
    private final String nome;
    private final LocalDateTime ocorridoEm;
    
    public ServicoAtualizadoEvent(UUID servicoId, String nome) {
        this.servicoId = servicoId;
        this.nome = nome;
        this.ocorridoEm = LocalDateTime.now();
    }
}
