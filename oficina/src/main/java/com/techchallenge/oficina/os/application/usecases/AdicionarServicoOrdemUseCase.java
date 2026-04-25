package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.application.usecases.commands.AdicionarServicoOrdemCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import com.techchallenge.oficina.os.domain.model.entities.Servico;
import com.techchallenge.oficina.os.domain.repositories.OrdemServicoRepository;
import com.techchallenge.oficina.os.domain.repositories.ServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdicionarServicoOrdemUseCase {
    
    private final OrdemServicoRepository ordemServicoRepository;
    private final ServicoRepository servicoRepository;
    
    @Transactional
    public OrdemServicoResponse execute(AdicionarServicoOrdemCommand command) {
        log.info("Adicionando serviço à ordem de serviço: ordemServicoId={}, servicoId={}", 
                command.getOrdemServicoId(), command.getServicoId());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoRepository.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new RuntimeException("Ordem de Serviço não encontrada com ID: " + command.getOrdemServicoId()));
        
        // Buscar serviço
        Servico servico = servicoRepository.findById(command.getServicoId())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com ID: " + command.getServicoId()));
        
        // Criar item de serviço
        ItemServico itemServico = ItemServico.criar(servico);
        
        // Adicionar à ordem de serviço
        ordemServico.adicionarItemServico(itemServico);
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoRepository.save(ordemServico);
        
        log.info("Serviço adicionado à ordem de serviço com sucesso: itemServicoId={}", itemServico.getId());
        
        return OrdemServicoResponse.from(savedOrdemServico);
    }
}
