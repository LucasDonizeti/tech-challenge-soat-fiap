package com.techchallenge.oficina.os.domain.repositories;

import com.techchallenge.oficina.os.domain.model.entities.Servico;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServicoRepository {
    
    Servico save(Servico servico);
    
    Optional<Servico> findById(UUID id);
    
    Page<Servico> findAll(Pageable pageable);
    
    List<Servico> findAll();
    
    List<Servico> findByAtivo(Boolean ativo);
    
    List<Servico> findByNomeContaining(String nome);
    
    void deleteById(UUID id);
}
