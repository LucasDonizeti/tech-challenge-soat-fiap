package com.techchallenge.oficina.sharedkernel.domain.exceptions;

public class ValorInvalidoException extends DomainException {
    
    public ValorInvalidoException(String message) {
        super(message);
    }
    
    public ValorInvalidoException(String message, String errorCode) {
        super(message, errorCode);
    }
}
