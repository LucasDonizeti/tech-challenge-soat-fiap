package com.techchallenge.oficina.os.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Callback externo com a decisão do cliente sobre o orçamento")
public class DecisaoOrcamentoRequest {

    @NotNull(message = "A decisão é obrigatória")
    @Schema(description = "Decisão do cliente: APROVADO ou RECUSADO",
            allowableValues = {"APROVADO", "RECUSADO"},
            example = "APROVADO")
    private Decisao decisao;

    @Schema(description = "Motivo da recusa (obrigatório apenas quando decisão = RECUSADO)",
            example = "Valor acima do esperado")
    private String motivo;

    @AssertTrue(message = "Motivo é obrigatório quando a decisão é RECUSADO")
    @Schema(hidden = true)
    public boolean isMotivoValido() {
        if (decisao == Decisao.RECUSADO) {
            return motivo != null && !motivo.isBlank();
        }
        return true;
    }

    public enum Decisao {
        APROVADO,
        RECUSADO
    }
}
