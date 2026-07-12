package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarPrecoServicoCommand;
import com.techchallenge.oficina.administrativo.application.usecases.ports.input.AtualizarPrecoServicoInput;
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
public class AtualizarPrecoServicoUseCase implements AtualizarPrecoServicoInput {
    
    private final ServicoGateway servicoGateway;

    public ServicoResponse execute(UUID servicoId, AtualizarPrecoServicoCommand command) {
        log.info("Iniciando atualização de preço do serviço: ID={}", servicoId);
        
        // Buscar serviço existente
        Servico servico = servicoGateway.findById(servicoId)
                .orElseThrow(() -> {
                    log.warn("Serviço não encontrado para atualização de preço: ID={}", servicoId);
                    return new IllegalArgumentException("Serviço não encontrado: " + servicoId);
                });
        
        // Atualizar preço
        servico.atualizarPreco(command.getNovoPreco());
        
        // Persistir alterações
        Servico savedServico = servicoGateway.save(servico);
        
        log.info("Preço do serviço atualizado com sucesso: ID={}, Nome={}, NovoPreço={}", 
                servico.getId(), 
                servico.getNome(),
                servico.getPreco());
        
        return ServicoResponse.from(savedServico);
    }
}
