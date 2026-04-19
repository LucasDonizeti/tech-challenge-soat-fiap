package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.application.usecases.responses.ServicoResponse;
import com.techchallenge.oficina.os.domain.model.entities.Servico;
import com.techchallenge.oficina.os.domain.repositories.ServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BuscarServicoUseCase {
    
    private final ServicoRepository repository;
    
    public ServicoResponse execute(UUID servicoId) {
        log.info("Buscando serviço por ID: {}", servicoId);
        
        Servico servico = repository.findById(servicoId)
                .orElseThrow(() -> {
                    log.warn("Serviço não encontrado: ID={}", servicoId);
                    return new IllegalArgumentException("Serviço não encontrado: " + servicoId);
                });
        
        return ServicoResponse.from(servico);
    }
}
