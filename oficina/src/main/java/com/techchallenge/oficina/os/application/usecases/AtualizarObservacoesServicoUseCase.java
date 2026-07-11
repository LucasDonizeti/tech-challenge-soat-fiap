package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.application.usecases.commands.AtualizarObservacoesServicoCommand;
import com.techchallenge.oficina.os.application.usecases.ports.input.AtualizarObservacoesServicoInput;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class AtualizarObservacoesServicoUseCase implements AtualizarObservacoesServicoInput {
    
    private final OrdemServicoRepository ordemServicoRepository;
    
    @Transactional
    public OrdemServicoResponse execute(AtualizarObservacoesServicoCommand command) {
        log.info("Atualizando observações do serviço: ordemServicoId={}, itemServicoId={}", 
                command.getOrdemServicoId(), command.getItemServicoId());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoRepository.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getOrdemServicoId()));
        
        // Validar status - só permite atualizar observações quando a OS está RECEBIDA ou EM_DIAGNOSTICO
        if (ordemServico.getStatus() != StatusOS.RECEBIDA && ordemServico.getStatus() != StatusOS.EM_DIAGNOSTICO) {
            throw new OrdemServicoStatusInvalidoException("Só é possível atualizar observações quando a Ordem de Serviço está nos status RECEBIDA ou EM_DIAGNOSTICO. Status atual: " + ordemServico.getStatus());
        }
        
        // Buscar item de serviço
        ItemServico itemServico = ordemServico.getItensServico().stream()
                .filter(item -> item.getId().equals(command.getItemServicoId()))
                .findFirst()
                .orElseThrow(() -> new ItemServicoNaoEncontradoException(command.getItemServicoId()));
        
        // Atualizar observações
        itemServico.atualizarObservacoes(command.getObservacoes());
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoRepository.save(ordemServico);
        
        log.info("Observações do serviço atualizadas com sucesso: itemServicoId={}", command.getItemServicoId());
        
        return OrdemServicoResponse.from(savedOrdemServico);
    }
}
