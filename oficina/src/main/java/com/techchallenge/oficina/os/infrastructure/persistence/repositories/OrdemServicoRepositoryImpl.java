package com.techchallenge.oficina.os.infrastructure.persistence.repositories;

import com.techchallenge.oficina.administrativo.domain.repositories.ClienteRepository;
import com.techchallenge.oficina.administrativo.domain.repositories.VeiculoRepository;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
import com.techchallenge.oficina.os.domain.repositories.OrdemServicoRepository;
import com.techchallenge.oficina.os.infrastructure.persistence.OrdemServicoJpaRepository;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.OrdemServicoEntity;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.OrdemServicoEntity.StatusOSEntity;
import com.techchallenge.oficina.os.infrastructure.persistence.mappers.OrdemServicoJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrdemServicoRepositoryImpl implements OrdemServicoRepository {
    
    private final OrdemServicoJpaRepository jpaRepository;
    private final OrdemServicoJpaMapper mapper;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    
    @Override
    public OrdemServico save(OrdemServico ordemServico) {
        OrdemServicoEntity entity = mapper.toEntity(ordemServico);
        OrdemServicoEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved, clienteRepository, veiculoRepository);
    }
    
    @Override
    public Optional<OrdemServico> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(entity -> mapper.toDomain(entity, clienteRepository, veiculoRepository));
    }
    
    @Override
    public Page<OrdemServico> findAll(Pageable pageable) {
        Page<OrdemServicoEntity> entities = jpaRepository.findAll(pageable);
        return entities.map(entity -> mapper.toDomain(entity, clienteRepository, veiculoRepository));
    }
    
    @Override
    public Page<OrdemServico> findByFilters(UUID clienteId, UUID veiculoId, StatusOS status, 
                                             LocalDateTime dataInicio, LocalDateTime dataFim, Pageable pageable) {
        StatusOSEntity statusEntity = status != null ? StatusOSEntity.valueOf(status.name()) : null;
        Page<OrdemServicoEntity> entities = jpaRepository.findByFilters(
                clienteId, veiculoId, statusEntity, dataInicio, dataFim, pageable);
        return entities.map(entity -> mapper.toDomain(entity, clienteRepository, veiculoRepository));
    }
}
