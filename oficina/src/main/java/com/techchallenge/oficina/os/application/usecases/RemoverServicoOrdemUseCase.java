package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.application.usecases.commands.RemoverServicoOrdemCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.oficina.os.domain.exceptions.OrdemServicoStatusInvalidoException;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
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
public class RemoverServicoOrdemUseCase {
    
    private final OrdemServicoRepository ordemServicoRepository;
    
    @Transactional
    public OrdemServicoResponse execute(RemoverServicoOrdemCommand command) {
        log.info("Removendo serviço da ordem de serviço: ordemServicoId={}, itemServicoId={}", 
                command.getOrdemServicoId(), command.getItemServicoId());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoRepository.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getOrdemServicoId()));
        
        // A validação de status é feita dentro do próprio aggregate (removerItemServico)
        // que só permite remover serviços quando a OS está RECEBIDA
        
        // Remover item de serviço (validação acontece dentro do método)
        ordemServico.removerItemServico(command.getItemServicoId());
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoRepository.save(ordemServico);
        
        log.info("Serviço removido da ordem de serviço com sucesso: itemServicoId={}", command.getItemServicoId());
        
        return OrdemServicoResponse.from(savedOrdemServico);
    }
}
