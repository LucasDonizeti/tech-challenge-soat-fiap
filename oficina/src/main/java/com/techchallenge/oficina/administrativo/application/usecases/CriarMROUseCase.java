package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.commands.CriarMROCommand;
import com.techchallenge.oficina.administrativo.application.usecases.ports.input.CriarMROInput;
import com.techchallenge.oficina.administrativo.application.usecases.ports.output.MROGateway;
import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;
import com.techchallenge.oficina.administrativo.domain.model.entities.MRO;
import com.techchallenge.oficina.administrativo.domain.repositories.MRORepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
public class CriarMROUseCase implements CriarMROInput {
    
    private final MROGateway mroGateway;

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
        MRO savedMRO = mroGateway.save(mro);
        
        log.info("MRO criado com sucesso: ID={}, Nome={}, Tipo={}", 
                savedMRO.getId(), 
                savedMRO.getNome(),
                savedMRO.getTipo());
        
        return MROResponse.from(savedMRO);
    }
}
