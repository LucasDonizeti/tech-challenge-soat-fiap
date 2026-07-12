package com.techchallenge.oficina.administrativo.infrastructure.gateways;

import com.techchallenge.oficina.administrativo.application.usecases.ports.output.MROGateway;
import com.techchallenge.oficina.administrativo.domain.model.entities.MRO;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.TipoMRO;
import com.techchallenge.oficina.administrativo.domain.repositories.MRORepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MROPersistenceGateway implements MROGateway {

    private final MRORepository repository;

    @Override
    public MRO save(MRO mro) {
        return repository.save(mro);
    }

    @Override
    public Optional<MRO> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Page<MRO> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public List<MRO> findAll() {
        return repository.findAll();
    }

    @Override
    public List<MRO> findByAtivo(Boolean ativo) {
        return repository.findByAtivo(ativo);
    }

    @Override
    public List<MRO> findByTipo(TipoMRO tipo) {
        return repository.findByTipo(tipo);
    }

    @Override
    public List<MRO> findByNomeContaining(String nome) {
        return repository.findByNomeContaining(nome);
    }

    @Override
    public List<MRO> findByQuantidadeEstoqueGreaterThan(Integer quantidade) {
        return repository.findByQuantidadeEstoqueGreaterThan(quantidade);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }
}
