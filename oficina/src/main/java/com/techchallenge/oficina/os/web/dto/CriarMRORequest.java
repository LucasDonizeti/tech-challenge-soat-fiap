package com.techchallenge.oficina.os.web.dto;

import com.techchallenge.oficina.os.application.usecases.commands.CriarMROCommand;
import com.techchallenge.oficina.os.domain.model.valueobjects.TipoMRO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CriarMRORequest {
    
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    
    private String descricao;
    
    @NotBlank(message = "Tipo é obrigatório")
    private String tipo;
    
    @NotNull(message = "Quantidade em estoque é obrigatória")
    @PositiveOrZero(message = "Quantidade em estoque deve ser maior ou igual a zero")
    private Integer quantidadeEstoque;
    
    @NotNull(message = "Preço unitário é obrigatório")
    @Positive(message = "Preço unitário deve ser maior que zero")
    private BigDecimal precoUnitario;
    
    public CriarMROCommand toCommand() {
        TipoMRO tipoEnum = TipoMRO.valueOf(tipo.toUpperCase());
        return new CriarMROCommand(nome, descricao, tipoEnum, quantidadeEstoque, precoUnitario);
    }
}
