package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoMROException;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class AtualizarPrecoMROCommand {
    
    private final UUID id;
    private final BigDecimal precoUnitario;
    
    public AtualizarPrecoMROCommand(UUID id, BigDecimal precoUnitario) {
        if (id == null) {
            throw new ValidacaoMROException("ID é obrigatório");
        }
        if (precoUnitario == null || precoUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoMROException("Preço unitário deve ser maior que zero");
        }
        
        this.id = id;
        this.precoUnitario = precoUnitario;
    }
}
