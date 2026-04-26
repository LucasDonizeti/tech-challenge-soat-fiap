package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.commands.CriarServicoCommand;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;
import com.techchallenge.oficina.administrativo.domain.model.entities.Servico;
import com.techchallenge.oficina.administrativo.domain.repositories.ServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CriarServicoUseCase {
    
    private final ServicoRepository repository;
    
    @Transactional
    public ServicoResponse execute(CriarServicoCommand command) {
        log.info("Iniciando criação de serviço: {}", command.getNome());
        
        // Criação do serviço
        Servico servico = Servico.criar(
            command.getNome(),
            command.getDescricao(),
            command.getPreco()
        );
        
        // Persistência
        Servico savedServico = repository.save(servico);
        
        log.info("Serviço criado com sucesso: ID={}, Nome={}", 
                savedServico.getId(), 
                savedServico.getNome());
        
        return ServicoResponse.from(savedServico);
    }
}
