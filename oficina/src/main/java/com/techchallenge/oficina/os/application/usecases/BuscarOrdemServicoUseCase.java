package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.application.usecases.ports.input.BuscarOrdemServicoInput;
import com.techchallenge.oficina.os.application.usecases.ports.output.OrdemServicoGateway;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.repositories.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class BuscarOrdemServicoUseCase implements BuscarOrdemServicoInput {
    
    private final OrdemServicoGateway ordemServicoGateway;
    
    public OrdemServicoResponse execute(UUID ordemServicoId) {
        log.info("Buscando ordem de serviço: ordemServicoId={}", ordemServicoId);
        
        OrdemServico ordemServico = ordemServicoGateway.findById(ordemServicoId)
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(ordemServicoId));
        
        log.info("Ordem de serviço encontrada: ID={}, Status={}", ordemServico.getId(), ordemServico.getStatus());
        
        return OrdemServicoResponse.from(ordemServico);
    }
}
