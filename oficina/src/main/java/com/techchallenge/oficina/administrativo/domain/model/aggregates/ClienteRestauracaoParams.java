package com.techchallenge.oficina.administrativo.domain.model.aggregates;

import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CNPJ;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Nome;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusCliente;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClienteRestauracaoParams(
    UUID id,
    Nome nome,
    CPF cpf,
    CNPJ cnpj,
    Email email,
    StatusCliente status,
    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm
) {}
