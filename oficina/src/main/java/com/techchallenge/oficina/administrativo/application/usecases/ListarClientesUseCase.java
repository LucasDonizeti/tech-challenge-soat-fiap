package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.ports.input.ListarClientesInput;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.repositories.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListarClientesUseCase implements ListarClientesInput {

    private final ClienteRepository repository;

    public Page<ClienteResponse> execute(Pageable pageable) {
        log.info("Listando clientes - página: {}, tamanho: {}",
                pageable.getPageNumber(),
                pageable.getPageSize());

        Page<Cliente> clientes = repository.findAll(pageable);

        log.info("Listagem concluída: {} clientes retornados de um total de {}",
                clientes.getContent().size(),
                clientes.getTotalElements());

        return clientes.map(ClienteResponse::from);
    }
}
