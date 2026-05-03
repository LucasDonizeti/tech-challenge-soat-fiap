package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.application.usecases.commands.RemoverMROServicoCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.exceptions.ItemServicoNaoEncontradoException;
import com.techchallenge.oficina.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.oficina.os.domain.exceptions.OrdemServicoStatusInvalidoException;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
import com.techchallenge.oficina.os.domain.repositories.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RemoverMROServicoUseCase {
    
    private final OrdemServicoRepository ordemServicoRepository;
    
    @Transactional
    public OrdemServicoResponse execute(RemoverMROServicoCommand command) {
        log.info("Removendo MRO do serviço: ordemServicoId={}, itemServicoId={}, itemMroId={}", 
                command.getOrdemServicoId(), command.getItemServicoId(), command.getItemMroId());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoRepository.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getOrdemServicoId()));
        
        // Validar status - permite remover MROs quando a OS está RECEBIDA ou EM_DIAGNOSTICO
        if (ordemServico.getStatus() != StatusOS.RECEBIDA && ordemServico.getStatus() != StatusOS.EM_DIAGNOSTICO) {
            throw new OrdemServicoStatusInvalidoException("Só é possível remover MROs quando a Ordem de Serviço está nos status RECEBIDA ou EM_DIAGNOSTICO. Status atual: " + ordemServico.getStatus());
        }
        
        // Buscar item de serviço
        ItemServico itemServico = ordemServico.getItensServico().stream()
                .filter(item -> item.getId().equals(command.getItemServicoId()))
                .findFirst()
                .orElseThrow(() -> new ItemServicoNaoEncontradoException(command.getItemServicoId()));
        
        // Remover MRO do item de serviço
        itemServico.removerMRO(command.getItemMroId());
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoRepository.save(ordemServico);
        
        log.info("MRO removido do serviço com sucesso: itemMroId={}", command.getItemMroId());
        
        return OrdemServicoResponse.from(savedOrdemServico);
    }
}
