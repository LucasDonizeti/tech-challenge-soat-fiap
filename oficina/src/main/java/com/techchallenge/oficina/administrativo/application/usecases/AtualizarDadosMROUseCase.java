package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarDadosMROCommand;
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
public class AtualizarDadosMROUseCase {
    
    private final MRORepository repository;
    
    @Transactional
    public MROResponse execute(AtualizarDadosMROCommand command) {
        log.info("Iniciando atualização de dados do MRO: ID={}", command.getId());
        
        // Busca do MRO
        MRO mro = repository.findById(command.getId())
                .orElseThrow(() -> new ValidacaoMROException("MRO não encontrado com ID: " + command.getId()));
        
        // Atualização dos dados
        mro.atualizarDados(command.getNome(), command.getDescricao(), command.getTipo());
        
        // Persistência
        MRO savedMRO = repository.save(mro);
        
        log.info("Dados do MRO atualizados com sucesso: ID={}, Nome={}", 
                savedMRO.getId(), 
                savedMRO.getNome());
        
        return MROResponse.from(savedMRO);
    }
}
