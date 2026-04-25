package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.application.usecases.commands.AdicionarMROServicoCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import com.techchallenge.oficina.os.domain.model.entities.MRO;
import com.techchallenge.oficina.os.domain.repositories.OrdemServicoRepository;
import com.techchallenge.oficina.os.domain.repositories.MRORepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdicionarMROServicoUseCase {
    
    private final OrdemServicoRepository ordemServicoRepository;
    private final MRORepository mroRepository;
    
    @Transactional
    public OrdemServicoResponse execute(AdicionarMROServicoCommand command) {
        log.info("Adicionando MRO ao serviço: ordemServicoId={}, itemServicoId={}, mroId={}, quantidade={}", 
                command.getOrdemServicoId(), command.getItemServicoId(), command.getMroId(), command.getQuantidade());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoRepository.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new RuntimeException("Ordem de Serviço não encontrada com ID: " + command.getOrdemServicoId()));
        
        // Buscar item de serviço
        ItemServico itemServico = ordemServico.getItensServico().stream()
                .filter(item -> item.getId().equals(command.getItemServicoId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item de Serviço não encontrado com ID: " + command.getItemServicoId()));
        
        // Buscar MRO
        MRO mro = mroRepository.findById(command.getMroId())
                .orElseThrow(() -> new RuntimeException("MRO não encontrado com ID: " + command.getMroId()));
        
        // Adicionar MRO ao item de serviço
        itemServico.adicionarMRO(mro, command.getQuantidade());
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoRepository.save(ordemServico);
        
        log.info("MRO adicionado ao serviço com sucesso");
        
        return OrdemServicoResponse.from(savedOrdemServico);
    }
}
