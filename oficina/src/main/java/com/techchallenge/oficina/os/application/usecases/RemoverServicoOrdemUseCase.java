package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.application.usecases.commands.RemoverServicoOrdemCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.repositories.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RemoverServicoOrdemUseCase {
    
    private final OrdemServicoRepository ordemServicoRepository;
    
    @Transactional
    public OrdemServicoResponse execute(RemoverServicoOrdemCommand command) {
        log.info("Removendo serviço da ordem de serviço: ordemServicoId={}, itemServicoId={}", 
                command.getOrdemServicoId(), command.getItemServicoId());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoRepository.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new RuntimeException("Ordem de Serviço não encontrada com ID: " + command.getOrdemServicoId()));
        
        // Remover item de serviço
        ordemServico.removerItemServico(command.getItemServicoId());
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoRepository.save(ordemServico);
        
        log.info("Serviço removido da ordem de serviço com sucesso: itemServicoId={}", command.getItemServicoId());
        
        return OrdemServicoResponse.from(savedOrdemServico);
    }
}
