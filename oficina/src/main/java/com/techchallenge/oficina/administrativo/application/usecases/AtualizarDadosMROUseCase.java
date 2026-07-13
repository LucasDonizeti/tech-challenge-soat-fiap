package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarDadosMROCommand;
import com.techchallenge.oficina.administrativo.application.usecases.ports.input.AtualizarDadosMROInput;
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
public class AtualizarDadosMROUseCase implements AtualizarDadosMROInput {

    private final MROGateway mroGateway;

    public MROResponse execute(AtualizarDadosMROCommand command) {
        log.info("Iniciando atualização de dados do MRO: ID={}", command.getId());
        
        // Busca do MRO
        MRO mro = mroGateway.findById(command.getId())
                .orElseThrow(() -> new ValidacaoMROException("MRO não encontrado com ID: " + command.getId()));
        
        // Atualização dos dados
        mro.atualizarDados(command.getNome(), command.getDescricao(), command.getTipo());
        
        // Persistência
        MRO savedMRO = mroGateway.save(mro);
        
        log.info("Dados do MRO atualizados com sucesso: ID={}, Nome={}", 
                savedMRO.getId(), 
                savedMRO.getNome());
        
        return MROResponse.from(savedMRO);
    }
}
