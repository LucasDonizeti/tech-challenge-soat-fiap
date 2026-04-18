package com.techchallenge.oficina.administrativo.domain.repositories;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CNPJ;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusCliente;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository {
    
    Cliente save(Cliente cliente);
    
    Optional<Cliente> findById(UUID id);
    
    List<Cliente> findAll();
    
    List<Cliente> findByStatus(StatusCliente status);
    
    Optional<Cliente> findByCPF(CPF cpf);
    
    Optional<Cliente> findByCNPJ(CNPJ cnpj);
    
    Optional<Cliente> findByEmail(Email email);
    
    boolean existsByCPF(CPF cpf);
    
    boolean existsByCNPJ(CNPJ cnpj);
    
    boolean existsByEmail(Email email);
    
    void deleteById(UUID id);
    
    List<Cliente> findByFilter(Object filter);
}
