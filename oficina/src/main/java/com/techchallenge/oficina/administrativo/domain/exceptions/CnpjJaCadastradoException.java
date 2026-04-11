package com.techchallenge.oficina.administrativo.domain.exceptions;

import com.techchallenge.oficina.sharedkernel.domain.exceptions.DomainException;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CNPJ;
import lombok.Getter;

@Getter
public class CnpjJaCadastradoException extends DomainException {
    private final transient CNPJ cnpj;

    public CnpjJaCadastradoException(CNPJ cnpj) {
        super("CNPJ já cadastrado: " + cnpj.getFormatado());
        this.cnpj = cnpj;
    }
}
