package com.techchallenge.oficina.administrativo.domain.exceptions;

import com.techchallenge.oficina.sharedkernel.domain.exceptions.DomainException;

public class ValidacaoValorException extends DomainException {

    public ValidacaoValorException(String message) {
        super(message, "VALIDACAO_VALOR");
    }
}
