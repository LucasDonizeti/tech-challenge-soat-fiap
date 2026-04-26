package com.techchallenge.oficina.os.domain.exceptions;

import com.techchallenge.oficina.sharedkernel.domain.exceptions.DomainException;

import java.util.UUID;

public class OrdemServicoNaoEncontradaException extends DomainException {

    public OrdemServicoNaoEncontradaException(UUID id) {
        super("Ordem de Serviço não encontrada com ID: " + id, "ORDEM_SERVICO_NAO_ENCONTRADA");
    }
}
