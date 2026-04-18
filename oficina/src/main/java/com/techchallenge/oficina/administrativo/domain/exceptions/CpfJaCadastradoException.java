package com.techchallenge.oficina.administrativo.domain.exceptions;

import com.techchallenge.oficina.sharedkernel.domain.exceptions.DomainException;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import lombok.Getter;

@Getter
public class CpfJaCadastradoException extends DomainException {
    private final transient CPF cpf;

    public CpfJaCadastradoException(CPF cpf) {
        super("CPF já cadastrado: " + cpf.getFormatado());
        this.cpf = cpf;
    }
}
