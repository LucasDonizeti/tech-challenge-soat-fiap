package com.techchallenge.oficina.administrativo.domain.exceptions;

import com.techchallenge.oficina.sharedkernel.domain.exceptions.DomainException;

public class ValidacaoVeiculoException extends DomainException {

    public ValidacaoVeiculoException(String message) {
        super(message, "VALIDACAO_VEICULO");
    }
}
