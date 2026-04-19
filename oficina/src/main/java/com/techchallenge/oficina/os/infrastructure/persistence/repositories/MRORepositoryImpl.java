package com.techchallenge.oficina.os.infrastructure.persistence.repositories;

import com.techchallenge.oficina.os.domain.model.entities.MRO;
import com.techchallenge.oficina.os.domain.model.valueobjects.TipoMRO;
import com.techchallenge.oficina.os.domain.repositories.MRORepository;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.MROEntity;
import com.techchallenge.oficina.os.infrastructure.persistence.mappers.MROJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MRORepositoryImpl implements MRORepository {
    
    private final MROJpaRepository jpaRepository;
    private final MROJpaMapper mapper;
    
    @Override
    public MRO save(MRO mro) {
        MROEntity entity = mapper.toEntity(mro);
        MROEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<MRO> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Page<MRO> findAll(Pageable pageable) {
        Page<MROEntity> entities = jpaRepository.findAll(pageable);
        return entities.map(mapper::toDomain);
    }

    @Override
    public List<MRO> findAll() {
        List<MROEntity> entities = jpaRepository.findAll();
        return mapper.toDomainList(entities);
    }

    @Override
    public List<MRO> findByAtivo(Boolean ativo) {
        List<MROEntity> entities = jpaRepository.findByAtivo(ativo);
        return mapper.toDomainList(entities);
    }
    
    @Override
    public List<MRO> findByTipo(TipoMRO tipo) {
        List<MROEntity> entities = jpaRepository.findByTipo(tipo);
        return mapper.toDomainList(entities);
    }
    
    @Override
    public List<MRO> findByNomeContaining(String nome) {
        List<MROEntity> entities = jpaRepository.findByNomeContaining(nome);
        return mapper.toDomainList(entities);
    }
    
    @Override
    public List<MRO> findByQuantidadeEstoqueGreaterThan(Integer quantidade) {
        List<MROEntity> entities = jpaRepository.findByQuantidadeEstoqueGreaterThan(quantidade);
        return mapper.toDomainList(entities);
    }
    
    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
