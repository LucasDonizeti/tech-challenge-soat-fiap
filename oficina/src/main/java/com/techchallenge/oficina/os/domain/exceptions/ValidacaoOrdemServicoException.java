package com.techchallenge.oficina.os.domain.exceptions;

import com.techchallenge.oficina.sharedkernel.domain.exceptions.DomainException;

public class ValidacaoOrdemServicoException extends DomainException {

    public ValidacaoOrdemServicoException(String message) {
        super(message, "VALIDACAO_ORDEM_SERVICO");
    }

    public ValidacaoOrdemServicoException(String message, Throwable cause) {
        super(message, "VALIDACAO_ORDEM_SERVICO");
        initCause(cause);
    }
}
