package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.application.usecases.commands.ReporEstoqueMROCommand;
import com.techchallenge.oficina.os.application.usecases.responses.MROResponse;
import com.techchallenge.oficina.os.domain.model.entities.MRO;
import com.techchallenge.oficina.os.domain.repositories.MRORepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReporEstoqueMROUseCase {
    
    private final MRORepository repository;
    
    @Transactional
    public MROResponse execute(ReporEstoqueMROCommand command) {
        log.info("Iniciando reposição de estoque do MRO: ID={}, Quantidade={}", command.getId(), command.getQuantidade());
        
        // Busca do MRO
        MRO mro = repository.findById(command.getId())
                .orElseThrow(() -> new RuntimeException("MRO não encontrado com ID: " + command.getId()));
        
        // Reposição do estoque
        mro.reporEstoque(command.getQuantidade());
        
        // Persistência
        MRO savedMRO = repository.save(mro);
        
        log.info("Estoque do MRO reposto com sucesso: ID={}, Nome={}, EstoqueAtual={}", 
                savedMRO.getId(), 
                savedMRO.getNome(),
                savedMRO.getQuantidadeEstoque());
        
        return MROResponse.from(savedMRO);
    }
}
