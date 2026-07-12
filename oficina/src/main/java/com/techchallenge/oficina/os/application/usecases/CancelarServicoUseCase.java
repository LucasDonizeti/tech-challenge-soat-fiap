package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.application.usecases.commands.CancelarServicoCommand;
import com.techchallenge.oficina.os.application.usecases.ports.input.CancelarServicoInput;
import com.techchallenge.oficina.os.application.usecases.ports.output.OrdemServicoGateway;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.exceptions.ItemServicoNaoEncontradoException;
import com.techchallenge.oficina.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.oficina.os.domain.exceptions.ValidacaoEstoqueException;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusItemServico;
import com.techchallenge.oficina.os.domain.repositories.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
public class CancelarServicoUseCase implements CancelarServicoInput {
    
    private final OrdemServicoGateway ordemServicoGateway;

    public OrdemServicoResponse execute(CancelarServicoCommand command) {
        log.info("Cancelando serviço: ordemServicoId={}, itemServicoId={}", 
                command.getOrdemServicoId(), command.getItemServicoId());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoGateway.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getOrdemServicoId()));
        
        // Buscar item de serviço
        ItemServico itemServico = ordemServico.getItensServico().stream()
                .filter(item -> item.getId().equals(command.getItemServicoId()))
                .findFirst()
                .orElseThrow(() -> new ItemServicoNaoEncontradoException(command.getItemServicoId()));
        
        // Validar que item não está CONCLUIDO ou CANCELADO
        if (itemServico.getStatus() == StatusItemServico.CONCLUIDO) {
            throw new ValidacaoEstoqueException("Não é possível cancelar um serviço já concluído");
        }
        
        if (itemServico.getStatus() == StatusItemServico.CANCELADO) {
            throw new ValidacaoEstoqueException("Serviço já está cancelado");
        }
        
        // Atualizar status para CANCELADO
        itemServico.atualizarStatus(StatusItemServico.CANCELADO);
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoGateway.save(ordemServico);
        
        log.info("Serviço cancelado com sucesso: itemServicoId={}", itemServico.getId());
        
        return OrdemServicoResponse.from(savedOrdemServico);
    }
}
