package com.techchallenge.oficina.os.web.dto;

import com.techchallenge.oficina.os.application.usecases.commands.AtualizarPrecoServicoCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AtualizarPrecoServicoRequest {
    
    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser maior que zero")
    private BigDecimal novoPreco;
    
    public AtualizarPrecoServicoCommand toCommand() {
        return new AtualizarPrecoServicoCommand(novoPreco);
    }
}
