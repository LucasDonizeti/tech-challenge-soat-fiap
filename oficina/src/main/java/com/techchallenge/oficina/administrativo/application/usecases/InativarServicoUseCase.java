package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;
import com.techchallenge.oficina.administrativo.domain.model.entities.Servico;
import com.techchallenge.oficina.administrativo.domain.repositories.ServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InativarServicoUseCase {
    
    private final ServicoRepository repository;
    
    @Transactional
    public ServicoResponse execute(UUID servicoId) {
        log.info("Iniciando inativação do serviço: ID={}", servicoId);
        
        // Buscar serviço existente
        Servico servico = repository.findById(servicoId)
                .orElseThrow(() -> {
                    log.warn("Serviço não encontrado para inativação: ID={}", servicoId);
                    return new IllegalArgumentException("Serviço não encontrado: " + servicoId);
                });
        
        // Inativar serviço
        servico.desativar();
        
        // Persistir alterações
        Servico savedServico = repository.save(servico);
        
        log.info("Serviço inativado com sucesso: ID={}, Nome={}", 
                servico.getId(), 
                servico.getNome());
        
        return ServicoResponse.from(savedServico);
    }
}
