package com.techchallenge.oficina.os.infrastructure.persistence.gateways;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CNPJ;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusCliente;
import com.techchallenge.oficina.administrativo.domain.repositories.ClienteRepository;
import com.techchallenge.oficina.os.application.usecases.ports.output.ClienteGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ClientePersistenceOSGateway implements ClienteGateway {

    private final ClienteRepository clienteRepository;

    @Override
    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Override
    public Optional<Cliente> findById(UUID id) {
        return clienteRepository.findById(id);
    }

    @Override
    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    @Override
    public Page<Cliente> findAll(Pageable pageable) {
        return clienteRepository.findAll(pageable);
    }

    @Override
    public List<Cliente> findByStatus(StatusCliente status) {
        return clienteRepository.findByStatus(status);
    }

    @Override
    public Optional<Cliente> findByCPF(CPF cpf) {
        return clienteRepository.findByCPF(cpf);
    }

    @Override
    public Optional<Cliente> findByCNPJ(CNPJ cnpj) {
        return clienteRepository.findByCNPJ(cnpj);
    }

    @Override
    public Optional<Cliente> findByEmail(Email email) {
        return clienteRepository.findByEmail(email);
    }

    @Override
    public boolean existsByCPF(CPF cpf) {
        return clienteRepository.existsByCPF(cpf);
    }

    @Override
    public boolean existsByCNPJ(CNPJ cnpj) {
        return clienteRepository.existsByCNPJ(cnpj);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return clienteRepository.existsByEmail(email);
    }

    @Override
    public void deleteById(UUID id) {
        clienteRepository.deleteById(id);
    }

    @Override
    public List<Cliente> findByFilter(Object filter) {
        return clienteRepository.findByFilter(filter);
    }
}
