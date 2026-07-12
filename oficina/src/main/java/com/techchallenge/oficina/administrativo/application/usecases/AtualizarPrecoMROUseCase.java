package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarPrecoMROCommand;
import com.techchallenge.oficina.administrativo.application.usecases.ports.input.AtualizarPrecoMROInput;
import com.techchallenge.oficina.administrativo.application.usecases.ports.output.MROGateway;
import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;
import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoMROException;
import com.techchallenge.oficina.administrativo.domain.model.entities.MRO;
import com.techchallenge.oficina.administrativo.domain.repositories.MRORepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
public class AtualizarPrecoMROUseCase implements AtualizarPrecoMROInput {
    
    private final MROGateway mroGateway;

    public MROResponse execute(AtualizarPrecoMROCommand command) {
        log.info("Iniciando atualização de preço do MRO: ID={}", command.getId());
        
        // Busca do MRO
        MRO mro = mroGateway.findById(command.getId())
                .orElseThrow(() -> new ValidacaoMROException("MRO não encontrado com ID: " + command.getId()));
        
        // Atualização do preço
        mro.atualizarPreco(command.getPrecoUnitario());
        
        // Persistência
        MRO savedMRO = mroGateway.save(mro);
        
        log.info("Preço do MRO atualizado com sucesso: ID={}, Nome={}, NovoPreço={}", 
                savedMRO.getId(), 
                savedMRO.getNome(),
                savedMRO.getPrecoUnitario());
        
        return MROResponse.from(savedMRO);
    }
}
