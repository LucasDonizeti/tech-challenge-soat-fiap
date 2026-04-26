package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarPrecoMROCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AtualizarPrecoMRORequest {
    
    @NotNull(message = "Preço unitário é obrigatório")
    @Positive(message = "Preço unitário deve ser maior que zero")
    private BigDecimal novoPrecoUnitario;
    
    public AtualizarPrecoMROCommand toCommand(UUID id) {
        return new AtualizarPrecoMROCommand(id, novoPrecoUnitario);
    }
}
