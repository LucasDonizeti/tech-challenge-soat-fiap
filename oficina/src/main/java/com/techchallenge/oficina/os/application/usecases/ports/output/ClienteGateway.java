package com.techchallenge.oficina.os.application.usecases.ports.output;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CNPJ;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.CPF;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Email;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusCliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteGateway {
    Cliente save(Cliente cliente);

    Optional<Cliente> findById(UUID id);

    List<Cliente> findAll();

    Page<Cliente> findAll(Pageable pageable);

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
