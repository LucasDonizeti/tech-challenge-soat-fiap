package com.techchallenge.oficina.administrativo.infrastructure.persistence.repositories;

import com.techchallenge.oficina.administrativo.domain.model.entities.Servico;
import com.techchallenge.oficina.administrativo.domain.repositories.ServicoRepository;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.ServicoEntity;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.mappers.ServicoJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ServicoRepositoryImpl implements ServicoRepository {
    
    private final ServicoJpaRepository jpaRepository;
    private final ServicoJpaMapper mapper;
    
    @Override
    public Servico save(Servico servico) {
        ServicoEntity entity = mapper.toEntity(servico);
        ServicoEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<Servico> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Page<Servico> findAll(Pageable pageable) {
        Page<ServicoEntity> entities = jpaRepository.findAll(pageable);
        return entities.map(mapper::toDomain);
    }

    @Override
    public List<Servico> findAll() {
        List<ServicoEntity> entities = jpaRepository.findAll();
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Servico> findByAtivo(Boolean ativo) {
        List<ServicoEntity> entities = jpaRepository.findByAtivo(ativo);
        return mapper.toDomainList(entities);
    }
    
    @Override
    public List<Servico> findByNomeContaining(String nome) {
        List<ServicoEntity> entities = jpaRepository.findByNomeContaining(nome);
        return mapper.toDomainList(entities);
    }
    
    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
    
    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
}
