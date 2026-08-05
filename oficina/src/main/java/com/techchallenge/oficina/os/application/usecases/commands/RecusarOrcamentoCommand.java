package com.techchallenge.oficina.os.application.usecases.commands;

import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import lombok.Getter;

import java.util.UUID;

@Getter
public class RecusarOrcamentoCommand {

    private final UUID ordemServicoId;
    private final String motivo;

    public RecusarOrcamentoCommand(UUID ordemServicoId, String motivo) {
        if (ordemServicoId == null) {
            throw new ValidacaoOrdemServicoException("ID da ordem de serviço é obrigatório");
        }
        this.ordemServicoId = ordemServicoId;
        this.motivo = (motivo != null && !motivo.isBlank()) ? motivo : "Motivo não informado";
    }
}
