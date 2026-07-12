package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.ports.input.InativarClienteInput;
import com.techchallenge.oficina.administrativo.application.usecases.ports.output.ClienteGateway;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.repositories.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class InativarClienteUseCase implements InativarClienteInput {
    
        private final ClienteGateway clienteGateway;

    public ClienteResponse execute(UUID clienteId) {
        log.info("Iniciando inativação do cliente: ID={}", clienteId);
        
        // Buscar cliente existente
        Cliente cliente = clienteGateway.findById(clienteId)
                .orElseThrow(() -> {
                    log.warn("Cliente não encontrado para inativação: ID={}", clienteId);
                    return new IllegalArgumentException("Cliente não encontrado: " + clienteId);
                });
        
        // Inativar cliente
        cliente.inativar();
        
        // Persistir alterações
        Cliente savedCliente = clienteGateway.save(cliente);
        
        log.info("Cliente inativado com sucesso: ID={}, Nome={}", 
                cliente.getId(), 
                cliente.getNome().getValor());
        
        return ClienteResponse.from(savedCliente);
    }
}
