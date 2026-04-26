package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.commands.CriarMROCommand;
import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;
import com.techchallenge.oficina.administrativo.domain.model.entities.MRO;
import com.techchallenge.oficina.administrativo.domain.repositories.MRORepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CriarMROUseCase {
    
    private final MRORepository repository;
    
    @Transactional
    public MROResponse execute(CriarMROCommand command) {
        log.info("Iniciando criação de MRO: {}", command.getNome());
        
        // Criação do MRO
        MRO mro = MRO.criar(
            command.getNome(),
            command.getDescricao(),
            command.getTipo(),
            command.getQuantidadeEstoque(),
            command.getPrecoUnitario()
        );
        
        // Persistência
        MRO savedMRO = repository.save(mro);
        
        log.info("MRO criado com sucesso: ID={}, Nome={}, Tipo={}", 
                savedMRO.getId(), 
                savedMRO.getNome(),
                savedMRO.getTipo());
        
        return MROResponse.from(savedMRO);
    }
}
