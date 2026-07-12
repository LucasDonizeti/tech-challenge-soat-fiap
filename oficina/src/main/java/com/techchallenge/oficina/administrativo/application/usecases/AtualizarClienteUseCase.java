package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarClienteCommand;
import com.techchallenge.oficina.administrativo.application.usecases.ports.input.AtualizarClienteInput;
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
public class AtualizarClienteUseCase implements AtualizarClienteInput {

        private final ClienteGateway clienteGateway;
    private final ClienteDomainService domainService;

    public ClienteResponse execute(UUID clienteId, AtualizarClienteCommand command) {
        log.info("Iniciando atualização do cliente: ID={}", clienteId);
        
        // Buscar cliente existente
        Cliente cliente = clienteGateway.findById(clienteId)
                .orElseThrow(() -> {
                    log.warn("Cliente não encontrado para atualização: ID={}", clienteId);
                    return new IllegalArgumentException("Cliente não encontrado: " + clienteId);
                });
        
        // Validar se email já está em uso por outro cliente
        if (!cliente.getEmail().getEndereco().equals(command.getEmail().getEndereco())) {
            domainService.validarEmailUnico(command.getEmail());
        }
        
        // Atualizar dados
        String nomeAntigo = cliente.getNome().getValor();
        cliente.atualizarNome(command.getNome());
        cliente.atualizarEmail(command.getEmail());
        
        // Persistir alterações
        Cliente savedCliente = clienteGateway.save(cliente);
        
        log.info("Cliente atualizado com sucesso: ID={}, Nome Antigo={}, Nome Novo={}", 
                cliente.getId(), 
                nomeAntigo, 
                cliente.getNome().getValor());
        
        return ClienteResponse.from(savedCliente);
    }
}
