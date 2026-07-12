package com.techchallenge.oficina.administrativo.domain.services;

import com.techchallenge.oficina.administrativo.application.usecases.ports.output.ClienteGateway;
import com.techchallenge.oficina.administrativo.domain.exceptions.CnpjJaCadastradoException;
import com.techchallenge.oficina.administrativo.domain.exceptions.CpfJaCadastradoException;
import com.techchallenge.oficina.administrativo.domain.exceptions.EmailJaCadastradoException;
import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoValorException;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CNPJ;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.repositories.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteDomainService {
    
    private final ClienteGateway clienteGateway;
    
    public void validarCPFUnico(CPF cpf) {
        if (clienteGateway.existsByCPF(cpf)) {
            throw new CpfJaCadastradoException(cpf);
        }
    }
    
    public void validarCNPJUnico(CNPJ cnpj) {
        if (clienteGateway.existsByCNPJ(cnpj)) {
            throw new CnpjJaCadastradoException(cnpj);
        }
    }
    
    public void validarEmailUnico(Email email) {
        if (clienteGateway.existsByEmail(email)) {
            throw new EmailJaCadastradoException(email);
        }
    }
    
    public void validarIdentificadoresUnicos(CPF cpf, CNPJ cnpj, Email email) {
        if (cpf != null) {
            validarCPFUnico(cpf);
        }
        if (cnpj != null) {
            validarCNPJUnico(cnpj);
        }
        validarEmailUnico(email);
    }
    
    public boolean isClienteAtivo(Cliente cliente) {
        return cliente != null && cliente.isAtivo();
    }
    
    public boolean podeExcluirCliente(Cliente cliente) {
        if (cliente == null) {
            return false;
        }
        
        // Regra: não pode excluir cliente com ordens de serviço em andamento
        // Por enquanto, apenas verificamos se o cliente possui veículos cadastrados
        return !cliente.possuiVeiculos();
    }
    
    public void validarExclusaoCliente(Cliente cliente) {
        if (!podeExcluirCliente(cliente)) {
            throw new ValidacaoValorException("Cliente não pode ser excluído. Verifique se há ordens de serviço em andamento ou veículos cadastrados.");
        }
    }
}
