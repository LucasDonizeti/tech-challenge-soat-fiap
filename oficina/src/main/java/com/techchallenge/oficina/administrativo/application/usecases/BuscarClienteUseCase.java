package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.ports.input.BuscarClienteInput;
import com.techchallenge.oficina.administrativo.application.usecases.ports.output.ClienteGateway;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.repositories.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class BuscarClienteUseCase implements BuscarClienteInput {
    
        private final ClienteGateway clienteGateway;
    
    public ClienteResponse execute(UUID clienteId) {
        log.info("Buscando cliente por ID: {}", clienteId);
        
        return clienteGateway.findById(clienteId)
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
