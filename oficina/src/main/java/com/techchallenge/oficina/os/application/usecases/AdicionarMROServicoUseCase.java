package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.BuscarMROUseCase;
import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;
import com.techchallenge.oficina.os.application.usecases.commands.AdicionarMROServicoCommand;
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
public class AdicionarMROServicoUseCase {
    
    private final OrdemServicoRepository ordemServicoRepository;
    private final BuscarMROUseCase buscarMROUseCase;
    
    @Transactional
    public OrdemServicoResponse execute(AdicionarMROServicoCommand command) {
        log.info("Adicionando MRO ao serviço: ordemServicoId={}, itemServicoId={}, mroId={}, quantidade={}", 
                command.getOrdemServicoId(), command.getItemServicoId(), command.getMroId(), command.getQuantidade());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoRepository.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getOrdemServicoId()));
        
        // Validar status - permite adicionar MROs quando a OS está RECEBIDA ou EM_DIAGNOSTICO
        if (ordemServico.getStatus() != StatusOS.RECEBIDA && ordemServico.getStatus() != StatusOS.EM_DIAGNOSTICO) {
            throw new OrdemServicoStatusInvalidoException("Só é possível adicionar MROs quando a Ordem de Serviço está nos status RECEBIDA ou EM_DIAGNOSTICO. Status atual: " + ordemServico.getStatus());
        }
        
        // Buscar item de serviço
        ItemServico itemServico = ordemServico.getItensServico().stream()
                .filter(item -> item.getId().equals(command.getItemServicoId()))
                .findFirst()
                .orElseThrow(() -> new ItemServicoNaoEncontradoException(command.getItemServicoId()));
        
        // Buscar MRO via ACL (contexto administrativo)
        MROResponse mroResponse = buscarMROUseCase.execute(command.getMroId());
        
        // Adicionar MRO ao item de serviço com dados do DTO
        // Nota: O débito de estoque será feito em uma etapa posterior (ao iniciar a execução da OS)
        itemServico.adicionarMRO(
            mroResponse.getId(),
            mroResponse.getNome(),
            mroResponse.getDescricao(),
            mroResponse.getPrecoUnitario(),
            command.getQuantidade()
        );
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoRepository.save(ordemServico);
        
        log.info("MRO adicionado ao serviço com sucesso");
        
        return OrdemServicoResponse.from(savedOrdemServico);
    }
}
