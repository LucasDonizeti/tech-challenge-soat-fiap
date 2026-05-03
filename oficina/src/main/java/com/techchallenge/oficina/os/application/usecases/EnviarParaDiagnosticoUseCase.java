package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.application.usecases.commands.EnviarParaDiagnosticoCommand;
import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.domain.exceptions.OrdemServicoNaoEncontradaException;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.repositories.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnviarParaDiagnosticoUseCase {
    
    private final OrdemServicoRepository ordemServicoRepository;
    
    @Transactional
    public OrdemServicoResponse execute(EnviarParaDiagnosticoCommand command) {
        log.info("Enviando ordem de serviço para diagnóstico: ordemServicoId={}", command.getOrdemServicoId());
        
        // Buscar ordem de serviço
        OrdemServico ordemServico = ordemServicoRepository.findById(command.getOrdemServicoId())
                .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getOrdemServicoId()));
        
        // Enviar para diagnóstico (validações são feitas no domínio)
        ordemServico.enviarParaDiagnostico();
        
        // Persistência
        OrdemServico savedOrdemServico = ordemServicoRepository.save(ordemServico);
        
        log.info("Ordem de serviço enviada para diagnóstico com sucesso: ID={}, Status={}", 
                savedOrdemServico.getId(), savedOrdemServico.getStatus());
        
        return OrdemServicoResponse.from(savedOrdemServico);
    }
}
