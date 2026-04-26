package com.techchallenge.oficina.os.domain.exceptions;

import com.techchallenge.oficina.sharedkernel.domain.exceptions.DomainException;

import java.util.UUID;

public class ItemServicoNaoEncontradoException extends DomainException {

    public ItemServicoNaoEncontradoException(UUID id) {
        super("Item de Serviço não encontrado com ID: " + id, "ITEM_SERVICO_NAO_ENCONTRADO");
    }
}
