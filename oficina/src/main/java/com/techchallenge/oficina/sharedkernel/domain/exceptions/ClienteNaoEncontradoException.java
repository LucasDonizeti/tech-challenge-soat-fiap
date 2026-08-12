package com.techchallenge.oficina.sharedkernel.domain.exceptions;

public class ClienteNaoEncontradoException extends DomainException {

    public ClienteNaoEncontradoException(String message) {
        super(message);
    }

    public ClienteNaoEncontradoException(String message, String errorCode) {
        super(message, errorCode);
    }
}
