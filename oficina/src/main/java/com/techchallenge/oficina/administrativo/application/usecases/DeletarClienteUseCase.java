package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.ports.input.DeletarClienteInput;
import com.techchallenge.oficina.administrativo.application.usecases.ports.output.ClienteGateway;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.repositories.ClienteRepository;
import com.techchallenge.oficina.administrativo.domain.services.ClienteDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class DeletarClienteUseCase implements DeletarClienteInput {
    
        private final ClienteGateway clienteGateway;
    private final ClienteDomainService domainService;

    public void execute(UUID clienteId) {
        log.info("Iniciando exclusão do cliente: ID={}", clienteId);
        
        // Buscar cliente existente
        Cliente cliente = clienteGateway.findById(clienteId)
                .orElseThrow(() -> {
                    log.warn("Cliente não encontrado para exclusão: ID={}", clienteId);
                    return new IllegalArgumentException("Cliente não encontrado: " + clienteId);
                });
        
        // Validar se pode excluir
        domainService.validarExclusaoCliente(cliente);
        
        // Excluir cliente
        clienteGateway.deleteById(clienteId);
        
        log.info("Cliente excluído com sucesso: ID={}, Nome={}", 
                cliente.getId(), 
                cliente.getNome().getValor());
    }
}
