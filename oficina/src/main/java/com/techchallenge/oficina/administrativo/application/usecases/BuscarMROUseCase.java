package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.ports.input.BuscarMROInput;
import com.techchallenge.oficina.administrativo.application.usecases.ports.output.MROGateway;
import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;
import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoMROException;
import com.techchallenge.oficina.administrativo.domain.model.entities.MRO;
import com.techchallenge.oficina.administrativo.domain.repositories.MRORepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class BuscarMROUseCase implements BuscarMROInput {
    
    private final MROGateway mroGateway;
    
    public MROResponse execute(UUID id) {
        log.info("Buscando MRO: ID={}", id);
        
        MRO mro = mroGateway.findById(id)
                .orElseThrow(() -> new ValidacaoMROException("MRO não encontrado com ID: " + id));
        
        log.info("MRO encontrado: ID={}, Nome={}", mro.getId(), mro.getNome());
        
        return MROResponse.from(mro);
    }
}
