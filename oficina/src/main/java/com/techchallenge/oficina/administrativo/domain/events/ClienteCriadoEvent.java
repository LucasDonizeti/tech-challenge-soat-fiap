package com.techchallenge.oficina.administrativo.domain.events;

import com.techchallenge.oficina.sharedkernel.domain.events.DomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ClienteCriadoEvent extends DomainEvent {
    private final UUID clienteId;
    private final String nome;
    private final String cpf;
    private final String cnpj;
    private final String email;

    public ClienteCriadoEvent(UUID clienteId, String nome, String cpf, String cnpj, String email) {
        super();
        this.clienteId = clienteId;
        this.nome = nome;
        this.cpf = cpf;
        this.cnpj = cnpj;
        this.email = email;
    }
}
