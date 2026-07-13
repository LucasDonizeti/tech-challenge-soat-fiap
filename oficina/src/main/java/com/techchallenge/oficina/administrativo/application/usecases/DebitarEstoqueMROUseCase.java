package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.commands.DebitarEstoqueMROCommand;
import com.techchallenge.oficina.administrativo.application.usecases.ports.input.DebitarEstoqueMROInput;
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
public class DebitarEstoqueMROUseCase implements DebitarEstoqueMROInput {
    
    private final MROGateway mroGateway;

    public MROResponse execute(DebitarEstoqueMROCommand command) {
        log.info("Iniciando débito de estoque do MRO: ID={}, Quantidade={}", command.getId(), command.getQuantidade());
        
        // Busca do MRO
        MRO mro = mroGateway.findById(command.getId())
                .orElseThrow(() -> new ValidacaoMROException("MRO não encontrado com ID: " + command.getId()));
        
        // Débito do estoque
        mro.debitarEstoque(command.getQuantidade());
        
        // Persistência
        MRO savedMRO = mroGateway.save(mro);
        
        log.info("Estoque do MRO debitado com sucesso: ID={}, Nome={}, EstoqueAtual={}", 
                savedMRO.getId(), 
                savedMRO.getNome(),
                savedMRO.getQuantidadeEstoque());
        
        return MROResponse.from(savedMRO);
    }
}
