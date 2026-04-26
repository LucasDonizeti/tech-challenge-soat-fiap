package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarDadosServicoCommand;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;
import com.techchallenge.oficina.administrativo.domain.model.entities.Servico;
import com.techchallenge.oficina.administrativo.domain.repositories.ServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AtualizarDadosServicoUseCase {
    
    private final ServicoRepository repository;
    
    @Transactional
    public ServicoResponse execute(UUID servicoId, AtualizarDadosServicoCommand command) {
        log.info("Iniciando atualização de dados do serviço: ID={}", servicoId);
        
        // Buscar serviço existente
        Servico servico = repository.findById(servicoId)
                .orElseThrow(() -> {
                    log.warn("Serviço não encontrado para atualização de dados: ID={}", servicoId);
                    return new IllegalArgumentException("Serviço não encontrado: " + servicoId);
                });
        
        // Atualizar dados
        servico.atualizarDados(command.getNome(), command.getDescricao());
        
        // Persistir alterações
        Servico savedServico = repository.save(servico);
        
        log.info("Dados do serviço atualizados com sucesso: ID={}, Nome={}", 
                servico.getId(), 
                servico.getNome());
        
        return ServicoResponse.from(savedServico);
    }
}
