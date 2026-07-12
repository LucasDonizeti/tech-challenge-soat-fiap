package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.ports.input.ReativarClienteInput;
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
public class ReativarClienteUseCase implements ReativarClienteInput {
    
        private final ClienteGateway clienteGateway;

    public ClienteResponse execute(UUID clienteId) {
        log.info("Iniciando reativação do cliente: ID={}", clienteId);
        
        // Buscar cliente existente
        Cliente cliente = clienteGateway.findById(clienteId)
                .orElseThrow(() -> {
                    log.warn("Cliente não encontrado para reativação: ID={}", clienteId);
                    return new IllegalArgumentException("Cliente não encontrado: " + clienteId);
                });
        
        // Reativar cliente
        cliente.reativar();
        
        // Persistir alterações
        Cliente savedCliente = clienteGateway.save(cliente);
        
        log.info("Cliente reativado com sucesso: ID={}, Nome={}", 
                cliente.getId(), 
                cliente.getNome().getValor());
        
        return ClienteResponse.from(savedCliente);
    }
}
