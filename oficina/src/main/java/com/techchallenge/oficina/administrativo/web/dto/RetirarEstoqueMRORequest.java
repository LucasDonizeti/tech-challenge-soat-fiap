package com.techchallenge.oficina.administrativo.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição para retirar estoque de MRO")
public class RetirarEstoqueMRORequest {
    
    @NotNull(message = "Quantidade não pode ser nula")
    @Positive(message = "Quantidade deve ser maior que zero")
    @Schema(description = "Quantidade a ser retirada do estoque", example = "5", required = true)
    private Integer quantidade;
}
