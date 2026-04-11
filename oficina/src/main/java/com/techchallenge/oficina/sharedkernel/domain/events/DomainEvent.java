package com.techchallenge.oficina.sharedkernel.domain.events;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public abstract class DomainEvent {
    private final LocalDateTime occurredOn;

    protected DomainEvent() {
        this.occurredOn = LocalDateTime.now();
    }

    protected DomainEvent(LocalDateTime occurredOn) {
        this.occurredOn = occurredOn;
    }
}
