package com.techchallenge.oficina.administrativo.application.usecases.ports.output;

import com.techchallenge.oficina.administrativo.domain.model.entities.Servico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServicoGateway {
    Servico save(Servico servico);

    Optional<Servico> findById(UUID id);

    Page<Servico> findAll(Pageable pageable);

    List<Servico> findAll();

    List<Servico> findByAtivo(Boolean ativo);

    List<Servico> findByNomeContaining(String nome);

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
