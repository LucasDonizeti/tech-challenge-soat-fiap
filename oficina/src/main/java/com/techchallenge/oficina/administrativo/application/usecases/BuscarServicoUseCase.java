package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.ports.input.BuscarServicoInput;
import com.techchallenge.oficina.administrativo.application.usecases.ports.output.ServicoGateway;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;
import com.techchallenge.oficina.administrativo.domain.model.entities.Servico;
import com.techchallenge.oficina.administrativo.domain.repositories.ServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class BuscarServicoUseCase implements BuscarServicoInput {
    
    private final ServicoGateway servicoGateway;
    
    public ServicoResponse execute(UUID servicoId) {
        log.info("Buscando serviço por ID: {}", servicoId);
        
        Servico servico = servicoGateway.findById(servicoId)
                .orElseThrow(() -> {
                    log.warn("Serviço não encontrado: ID={}", servicoId);
                    return new IllegalArgumentException("Serviço não encontrado: " + servicoId);
                });
        
        return ServicoResponse.from(servico);
    }
}
