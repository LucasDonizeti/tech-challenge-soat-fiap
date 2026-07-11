package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.BuscarServicoUseCase;
import com.techchallenge.oficina.administrativo.application.usecases.ports.input.BuscarServicoInput;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;
import com.techchallenge.oficina.os.application.usecases.commands.AdicionarServicoOrdemCommand;
import com.techchallenge.oficina.os.application.usecases.ports.input.AdicionarServicoOrdemInput;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
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
public class AdicionarServicoOrdemUseCase implements AdicionarServicoOrdemInput {
    
    private final OrdemServicoRepository ordemServicoRepository;
    private final BuscarServicoInput buscarServicoInput;
    
    @Transactional
    public OrdemServicoResponse execute(AdicionarServicoOrdemCommand command) {
        log.info("Adicionando serviço à ordem de serviço: ordemServicoId={}, servicoId={}", 
                command.getOrdemServicoId(), command.getServicoId());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoRepository.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getOrdemServicoId()));
        
        // A validação de status é feita dentro do próprio aggregate (adicionarItemServico)
        // que só permite adicionar serviços quando a OS está RECEBIDA
        
        // Buscar serviço via ACL (contexto administrativo)
        ServicoResponse servicoResponse = buscarServicoInput.execute(command.getServicoId());
        
        // Criar item de serviço com dados do DTO
        ItemServico itemServico = ItemServico.criarComDados(
            servicoResponse.getId(),
            servicoResponse.getNome(),
            servicoResponse.getDescricao(),
            servicoResponse.getPreco()
        );
        
        // Adicionar à ordem de serviço (validação acontece dentro do método)
        ordemServico.adicionarItemServico(itemServico);
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoRepository.save(ordemServico);
        
        log.info("Serviço adicionado à ordem de serviço com sucesso: itemServicoId={}", itemServico.getId());
        
        return OrdemServicoResponse.from(savedOrdemServico);
    }
}
