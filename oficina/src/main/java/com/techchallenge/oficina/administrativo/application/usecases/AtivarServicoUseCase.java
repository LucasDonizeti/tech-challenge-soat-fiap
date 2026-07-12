package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.ports.input.AtivarServicoInput;
import com.techchallenge.oficina.administrativo.application.usecases.ports.output.ServicoGateway;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;
import com.techchallenge.oficina.administrativo.domain.model.entities.Servico;
import com.techchallenge.oficina.administrativo.domain.repositories.ServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class AtivarServicoUseCase implements AtivarServicoInput {
    
    private final ServicoGateway servicoGateway;

    public ServicoResponse execute(UUID servicoId) {
        log.info("Iniciando ativação do serviço: ID={}", servicoId);
        
        // Buscar serviço existente
        Servico servico = servicoGateway.findById(servicoId)
                .orElseThrow(() -> {
                    log.warn("Serviço não encontrado para ativação: ID={}", servicoId);
                    return new IllegalArgumentException("Serviço não encontrado: " + servicoId);
                });
        
        // Ativar serviço
        servico.ativar();
        
        // Persistir alterações
        Servico savedServico = servicoGateway.save(servico);
        
        log.info("Serviço ativado com sucesso: ID={}, Nome={}", 
                servico.getId(), 
                servico.getNome());
        
        return ServicoResponse.from(savedServico);
    }
}
