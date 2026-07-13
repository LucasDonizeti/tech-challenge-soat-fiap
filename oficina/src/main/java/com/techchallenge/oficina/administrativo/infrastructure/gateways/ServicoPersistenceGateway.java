package com.techchallenge.oficina.administrativo.infrastructure.gateways;

import com.techchallenge.oficina.administrativo.application.usecases.ports.output.ServicoGateway;
import com.techchallenge.oficina.administrativo.domain.model.entities.Servico;
import com.techchallenge.oficina.administrativo.domain.repositories.ServicoRepository;
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
public class ServicoPersistenceGateway implements ServicoGateway {

    private final ServicoRepository repository;

    @Override
    public Servico save(Servico servico) {
        return repository.save(servico);
    }

    @Override
    public Optional<Servico> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Page<Servico> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public List<Servico> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Servico> findByAtivo(Boolean ativo) {
        return repository.findByAtivo(ativo);
    }

    @Override
    public List<Servico> findByNomeContaining(String nome) {
        return repository.findByNomeContaining(nome);
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
