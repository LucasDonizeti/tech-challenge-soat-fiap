package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.ports.input.BuscarClienteInput;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.repositories.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BuscarClienteUseCase implements BuscarClienteInput {
    
    private final ClienteRepository repository;
    
    public ClienteResponse execute(UUID clienteId) {
        log.info("Buscando cliente por ID: {}", clienteId);
        
        return repository.findById(clienteId)
                .map(cliente -> {
                    log.info("Cliente encontrado: ID={}, Nome={}", 
                            cliente.getId(), 
                            cliente.getNome().getValor());
                    return ClienteResponse.from(cliente);
                })
                .orElseThrow(() -> {
                    log.warn("Cliente não encontrado: ID={}", clienteId);
                    return new IllegalArgumentException("Cliente não encontrado: " + clienteId);
                });
    }
}
