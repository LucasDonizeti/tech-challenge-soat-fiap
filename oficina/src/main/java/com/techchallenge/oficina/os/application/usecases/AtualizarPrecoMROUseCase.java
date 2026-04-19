package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.application.usecases.commands.AtualizarPrecoMROCommand;
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
public class AtualizarPrecoMROUseCase {
    
    private final MRORepository repository;
    
    @Transactional
    public MROResponse execute(AtualizarPrecoMROCommand command) {
        log.info("Iniciando atualização de preço do MRO: ID={}", command.getId());
        
        // Busca do MRO
        MRO mro = repository.findById(command.getId())
                .orElseThrow(() -> new RuntimeException("MRO não encontrado com ID: " + command.getId()));
        
        // Atualização do preço
        mro.atualizarPreco(command.getPrecoUnitario());
        
        // Persistência
        MRO savedMRO = repository.save(mro);
        
        log.info("Preço do MRO atualizado com sucesso: ID={}, Nome={}, NovoPreço={}", 
                savedMRO.getId(), 
                savedMRO.getNome(),
                savedMRO.getPrecoUnitario());
        
        return MROResponse.from(savedMRO);
    }
}
