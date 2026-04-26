package com.techchallenge.oficina.os.domain.exceptions;

import com.techchallenge.oficina.sharedkernel.domain.exceptions.DomainException;

public class OrdemServicoStatusInvalidoException extends DomainException {

    public OrdemServicoStatusInvalidoException(String message) {
        super(message, "ORDEM_SERVICO_STATUS_INVALIDO");
    }
}
