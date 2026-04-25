package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
import com.techchallenge.oficina.os.domain.repositories.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListarOrdensServicoUseCase {
    
    private final OrdemServicoRepository ordemServicoRepository;
    
    @Transactional(readOnly = true)
    public Page<OrdemServicoResponse> execute(UUID clienteId, UUID veiculoId, StatusOS status, 
                                               LocalDateTime dataInicio, LocalDateTime dataFim, 
                                               Pageable pageable) {
        log.info("Listando ordens de serviço com filtros: clienteId={}, veiculoId={}, status={}, dataInicio={}, dataFim={}", 
                clienteId, veiculoId, status, dataInicio, dataFim);
        
        Page<OrdemServico> ordensServico = ordemServicoRepository.findByFilters(
                clienteId, veiculoId, status, dataInicio, dataFim, pageable);
        
        return ordensServico.map(OrdemServicoResponse::from);
    }
}
