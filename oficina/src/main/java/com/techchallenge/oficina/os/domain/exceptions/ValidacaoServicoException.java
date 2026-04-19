package com.techchallenge.oficina.os.domain.exceptions;

public class ValidacaoServicoException extends RuntimeException {
    
    public ValidacaoServicoException(String message) {
        super(message);
    }
    
    public ValidacaoServicoException(String message, Throwable cause) {
        super(message, cause);
    }
}
