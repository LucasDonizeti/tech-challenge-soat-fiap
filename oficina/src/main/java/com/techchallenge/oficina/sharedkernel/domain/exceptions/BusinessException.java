package com.techchallenge.oficina.sharedkernel.domain.exceptions;

public class BusinessException extends DomainException {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, String errorCode) {
        super(message, errorCode);
    }
}
