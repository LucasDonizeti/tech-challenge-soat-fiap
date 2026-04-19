package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.application.usecases.responses.MROResponse;
import com.techchallenge.oficina.os.domain.model.entities.MRO;
import com.techchallenge.oficina.os.domain.repositories.MRORepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InativarMROUseCase {
    
    private final MRORepository repository;
    
    @Transactional
    public MROResponse execute(UUID id) {
        log.info("Iniciando inativação do MRO: ID={}", id);
        
        // Busca do MRO
        MRO mro = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("MRO não encontrado com ID: " + id));
        
        // Inativação
        mro.desativar();
        
        // Persistência
        MRO savedMRO = repository.save(mro);
        
        log.info("MRO inativado com sucesso: ID={}, Nome={}", 
                savedMRO.getId(), 
                savedMRO.getNome());
        
        return MROResponse.from(savedMRO);
    }
}
