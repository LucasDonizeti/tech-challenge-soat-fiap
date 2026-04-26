package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;
import com.techchallenge.oficina.administrativo.domain.model.entities.MRO;
import com.techchallenge.oficina.administrativo.domain.repositories.MRORepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AtivarMROUseCase {
    
    private final MRORepository repository;
    
    @Transactional
    public MROResponse execute(UUID id) {
        log.info("Iniciando ativação do MRO: ID={}", id);
        
        // Busca do MRO
        MRO mro = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("MRO não encontrado com ID: " + id));
        
        // Ativação
        mro.ativar();
        
        // Persistência
        MRO savedMRO = repository.save(mro);
        
        log.info("MRO ativado com sucesso: ID={}, Nome={}", 
                savedMRO.getId(), 
                savedMRO.getNome());
        
        return MROResponse.from(savedMRO);
    }
}
