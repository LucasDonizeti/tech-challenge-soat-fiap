package com.techchallenge.oficina.os.domain.exceptions;

import com.techchallenge.oficina.sharedkernel.domain.exceptions.DomainException;

public class ValidacaoEstoqueException extends DomainException {
    
    public ValidacaoEstoqueException(String message) {
        super(message, "VALIDACAO_ESTOQUE");
    }
    
    public ValidacaoEstoqueException(String message, Throwable cause) {
        super(message, "VALIDACAO_ESTOQUE");
        this.initCause(cause);
    }
}
