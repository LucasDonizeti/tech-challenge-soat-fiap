package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.ports.input.BuscarClientesPorFiltroInput;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.repositories.ClienteRepository;
import com.techchallenge.oficina.administrativo.web.dto.ClienteFilterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BuscarClientesPorFiltroUseCase implements BuscarClientesPorFiltroInput {
    
    private final ClienteRepository repository;
    
    public Page<ClienteResponse> execute(ClienteFilterRequest filter, Pageable pageable) {
        log.info("Buscando clientes com filtros - página: {}, tamanho: {}", 
                pageable.getPageNumber(), 
                pageable.getPageSize());
        
        List<Cliente> clientes = repository.findByFilter(filter);
        
        // Aplicar paginação manualmente
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), clientes.size());
        List<Cliente> pageContent = clientes.subList(start, end);
        
        Page<ClienteResponse> response = new PageImpl<>(
                pageContent.stream()
                        .map(ClienteResponse::from)
                        .toList(),
                pageable,
                clientes.size()
        );
        
        log.info("Encontrados {} clientes no total, retornando {} na página", 
                clientes.size(), pageContent.size());
        
        return response;
    }
}
