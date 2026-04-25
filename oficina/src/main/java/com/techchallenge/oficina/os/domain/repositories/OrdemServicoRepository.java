package com.techchallenge.oficina.os.domain.repositories;

import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface OrdemServicoRepository {
    
    OrdemServico save(OrdemServico ordemServico);
    
    Optional<OrdemServico> findById(UUID id);
    
    Page<OrdemServico> findAll(Pageable pageable);
    
    Page<OrdemServico> findByFilters(UUID clienteId, UUID veiculoId, StatusOS status, 
                                       LocalDateTime dataInicio, LocalDateTime dataFim, Pageable pageable);
}
