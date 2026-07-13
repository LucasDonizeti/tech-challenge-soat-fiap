package com.techchallenge.oficina.os.infrastructure.persistence.gateways;

import com.techchallenge.oficina.os.application.usecases.ports.output.OrdemServicoGateway;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
import com.techchallenge.oficina.os.domain.repositories.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrdemServicoPersistenceGateway implements OrdemServicoGateway {

    private final OrdemServicoRepository ordemServicoRepository;

    @Override
    public OrdemServico save(OrdemServico ordemServico) {
        return ordemServicoRepository.save(ordemServico);
    }

    @Override
    public Optional<OrdemServico> findById(UUID id) {
        return ordemServicoRepository.findById(id);
    }

    @Override
    public Page<OrdemServico> findAll(Pageable pageable) {
        return ordemServicoRepository.findAll(pageable);
    }

    @Override
    public Page<OrdemServico> findByFilters(UUID clienteId, UUID veiculoId, StatusOS status, LocalDateTime dataInicio, LocalDateTime dataFim, Pageable pageable) {
        return ordemServicoRepository.findByFilters(clienteId, veiculoId, status, dataInicio, dataFim, pageable);
    }
}
