package com.techchallenge.oficina.administrativo.domain.exceptions;

import com.techchallenge.oficina.sharedkernel.domain.exceptions.DomainException;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import lombok.Getter;

@Getter
public class EmailJaCadastradoException extends DomainException {
    private final transient Email email;

    public EmailJaCadastradoException(Email email) {
        super("Email já cadastrado: " + email.getEndereco());
        this.email = email;
    }
}
