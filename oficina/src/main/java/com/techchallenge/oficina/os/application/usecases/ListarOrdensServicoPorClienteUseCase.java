package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.application.usecases.ports.input.ListarOrdensServicoPorClienteInput;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.repositories.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListarOrdensServicoPorClienteUseCase implements ListarOrdensServicoPorClienteInput {
    
    private final OrdemServicoRepository ordemServicoRepository;
    
    public Page<OrdemServicoResponse> execute(UUID clienteId, org.springframework.data.domain.Pageable pageable) {
        log.info("Listando ordens de serviço por cliente: clienteId={}", clienteId);
        
        if (clienteId == null) {
            throw new IllegalArgumentException("Cliente ID é obrigatório");
        }
        
        Page<OrdemServicoResponse> responses = ordemServicoRepository
                .findByFilters(clienteId, null, null, null, null, pageable)
                .map(OrdemServicoResponse::from);
        
        log.info("Listagem concluída: {} ordens de serviço retornadas", responses.getTotalElements());
        
        return responses;
    }
}
