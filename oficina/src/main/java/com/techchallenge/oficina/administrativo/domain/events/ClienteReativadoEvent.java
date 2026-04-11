package com.techchallenge.oficina.administrativo.domain.events;

import com.techchallenge.oficina.sharedkernel.domain.events.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ClienteReativadoEvent extends DomainEvent {
    private final UUID clienteId;

    public ClienteReativadoEvent(UUID clienteId) {
        super();
        this.clienteId = clienteId;
    }
}
