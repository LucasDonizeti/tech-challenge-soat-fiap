package com.techchallenge.oficina.administrativo.application.usecases.commands;

import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoServicoException;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class AtualizarPrecoServicoCommand {
    
    private final BigDecimal novoPreco;
    
    public AtualizarPrecoServicoCommand(BigDecimal novoPreco) {
        if (novoPreco == null || novoPreco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoServicoException("Preço deve ser maior que zero");
        }
        this.novoPreco = novoPreco;
    }
}
