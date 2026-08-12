package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.application.usecases.commands.RecusarOrcamentoCommand;
import com.techchallenge.oficina.os.application.usecases.ports.input.RecusarOrcamentoInput;
import com.techchallenge.oficina.os.application.usecases.ports.output.OrdemServicoGateway;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class RecusarOrcamentoUseCase implements RecusarOrcamentoInput {

    private final OrdemServicoGateway ordemServicoGateway;

    @Override
    public OrdemServicoResponse execute(RecusarOrcamentoCommand command) {
        log.info("Recusando orçamento: ordemServicoId={}, motivo={}",
                command.getOrdemServicoId(), command.getMotivo());

        OrdemServico ordemServico = ordemServicoGateway.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getOrdemServicoId()));

        // Transição de status + emissão de evento de domínio feitos no agregado
        ordemServico.recusarOrcamento(command.getMotivo());

        OrdemServico saved = ordemServicoGateway.save(ordemServico);

        log.info("Orçamento recusado com sucesso: ID={}, Status={}",
                saved.getId(), saved.getStatus());

        return OrdemServicoResponse.from(saved);
    }
}
