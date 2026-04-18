package com.techchallenge.oficina.administrativo.domain.exceptions;

import com.techchallenge.oficina.sharedkernel.domain.exceptions.DomainException;

public class ValidacaoClienteException extends DomainException {

    public ValidacaoClienteException(String message) {
        super(message, "VALIDACAO_CLIENTE");
    }
}
