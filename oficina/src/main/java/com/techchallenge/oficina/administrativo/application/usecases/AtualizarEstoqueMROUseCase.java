package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarEstoqueMROCommand;
import com.techchallenge.oficina.administrativo.application.usecases.ports.input.AtualizarEstoqueMROInput;
import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;
import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoMROException;
import com.techchallenge.oficina.administrativo.domain.model.entities.MRO;
import com.techchallenge.oficina.administrativo.domain.repositories.MRORepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AtualizarEstoqueMROUseCase implements AtualizarEstoqueMROInput {
    
    private final MRORepository repository;
    
    @Transactional
    public MROResponse execute(AtualizarEstoqueMROCommand command) {
        log.info("Iniciando atualização de estoque do MRO: ID={}", command.getId());
        
        // Busca do MRO
        MRO mro = repository.findById(command.getId())
                .orElseThrow(() -> new ValidacaoMROException("MRO não encontrado com ID: " + command.getId()));
        
        // Atualização do estoque
        mro.atualizarEstoque(command.getQuantidadeEstoque());
        
        // Persistência
        MRO savedMRO = repository.save(mro);
        
        log.info("Estoque do MRO atualizado com sucesso: ID={}, Nome={}, NovoEstoque={}", 
                savedMRO.getId(), 
                savedMRO.getNome(),
                savedMRO.getQuantidadeEstoque());
        
        return MROResponse.from(savedMRO);
    }
}
