package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.application.usecases.commands.AtualizarQuantidadeMROCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.oficina.os.domain.exceptions.ItemServicoNaoEncontradoException;
import com.techchallenge.oficina.os.domain.exceptions.ValidacaoOrdemServicoException;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.entities.ItemMRO;
import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import com.techchallenge.oficina.os.domain.repositories.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AtualizarQuantidadeMROUseCase {
    
    private final OrdemServicoRepository ordemServicoRepository;
    
    @Transactional
    public OrdemServicoResponse execute(AtualizarQuantidadeMROCommand command) {
        log.info("Atualizando quantidade de MRO: ordemServicoId={}, itemServicoId={}, itemMroId={}, novaQuantidade={}", 
                command.getOrdemServicoId(), command.getItemServicoId(), command.getItemMroId(), command.getNovaQuantidade());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoRepository.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getOrdemServicoId()));
        
        // Buscar item de serviço
        ItemServico itemServico = ordemServico.getItensServico().stream()
                .filter(item -> item.getId().equals(command.getItemServicoId()))
                .findFirst()
                .orElseThrow(() -> new ItemServicoNaoEncontradoException(command.getItemServicoId()));
        
        // Buscar item de MRO
        ItemMRO itemMRO = itemServico.getMrosServicos().stream()
                .filter(mro -> mro.getId().equals(command.getItemMroId()))
                .findFirst()
                .orElseThrow(() -> new ValidacaoOrdemServicoException("Item de MRO não encontrado com ID: " + command.getItemMroId()));
        
        // Atualizar quantidade
        itemMRO.atualizarQuantidade(command.getNovaQuantidade());
        
        // Atualizar valor total do item de serviço
        itemServico.atualizarValorMRO();
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoRepository.save(ordemServico);
        
        log.info("Quantidade de MRO atualizada com sucesso: itemMroId={}, novaQuantidade={}", 
                command.getItemMroId(), command.getNovaQuantidade());
        
        return OrdemServicoResponse.from(savedOrdemServico);
    }
}
