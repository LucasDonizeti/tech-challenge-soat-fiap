package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.ports.input.InativarMROInput;
import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;
import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoMROException;
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
public class InativarMROUseCase implements InativarMROInput {
    
    private final MRORepository repository;
    
    @Transactional
    public MROResponse execute(UUID id) {
        log.info("Iniciando inativação do MRO: ID={}", id);
        
        // Busca do MRO
        MRO mro = repository.findById(id)
                .orElseThrow(() -> new ValidacaoMROException("MRO não encontrado com ID: " + id));
        
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
