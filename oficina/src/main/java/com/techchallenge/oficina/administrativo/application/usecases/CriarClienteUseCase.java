package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.commands.CriarClienteCommand;
import com.techchallenge.oficina.administrativo.application.usecases.ports.input.CriarClienteInput;
import com.techchallenge.oficina.administrativo.application.usecases.ports.output.ClienteGateway;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.repositories.ClienteRepository;
import com.techchallenge.oficina.administrativo.domain.services.ClienteDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
public class CriarClienteUseCase implements CriarClienteInput {
    
    private final ClienteGateway clienteGateway;
    private final ClienteDomainService domainService;

    public ClienteResponse execute(CriarClienteCommand command) {
        log.info("Iniciando criação de cliente: {}", command.getNome().getValor());
        
        // Validações de domínio
        domainService.validarIdentificadoresUnicos(
            command.getCpf(), 
            command.getCnpj(), 
            command.getEmail()
        );
        
        // Criação do aggregate
        Cliente cliente;
        if (command.isPessoaFisica()) {
            cliente = Cliente.criar(
                command.getNome(), 
                command.getCpf(), 
                command.getEmail()
            );
            log.info("Criando cliente Pessoa Física com CPF: {}", command.getCpf().getFormatado());
        } else {
            cliente = Cliente.criarPJ(
                command.getNome(), 
                command.getCnpj(), 
                command.getEmail()
            );
            log.info("Criando cliente Pessoa Jurídica com CNPJ: {}", command.getCnpj().getFormatado());
        }
        
        // Persistência
        Cliente savedCliente = clienteGateway.save(cliente);
        
        log.info("Cliente criado com sucesso: ID={}, Nome={}", 
                savedCliente.getId(), 
                savedCliente.getNome().getValor());
        
        return ClienteResponse.from(savedCliente);
    }
}
