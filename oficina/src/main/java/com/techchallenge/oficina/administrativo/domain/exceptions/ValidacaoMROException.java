package com.techchallenge.oficina.administrativo.domain.exceptions;

import com.techchallenge.oficina.sharedkernel.domain.exceptions.DomainException;

public class ValidacaoMROException extends DomainException {

    public ValidacaoMROException(String message) {
        super(message, "VALIDACAO_MRO");
    }

    public ValidacaoMROException(String message, Throwable cause) {
        super(message, "VALIDACAO_MRO");
        initCause(cause);
    }
}
