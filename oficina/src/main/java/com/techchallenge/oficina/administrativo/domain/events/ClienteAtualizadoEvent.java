package com.techchallenge.oficina.administrativo.domain.events;

import com.techchallenge.oficina.sharedkernel.domain.events.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ClienteAtualizadoEvent extends DomainEvent {
    private final UUID clienteId;
    private final String nome;

    public ClienteAtualizadoEvent(UUID clienteId, String nome) {
        super();
        this.clienteId = clienteId;
        this.nome = nome;
    }
}
