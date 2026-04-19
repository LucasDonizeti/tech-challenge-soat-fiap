package com.techchallenge.oficina.os.domain.exceptions;

import com.techchallenge.oficina.sharedkernel.domain.exceptions.DomainException;

public class ValidacaoServicoException extends DomainException {

    public ValidacaoServicoException(String message) {
        super(message, "VALIDACAO_SERVICO");
    }

    public ValidacaoServicoException(String message, Throwable cause) {
        super(message, "VALIDACAO_SERVICO");
        initCause(cause);
    }
}
