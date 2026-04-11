package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusCliente;
import com.techchallenge.oficina.administrativo.domain.repositories.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListarClientesUseCase {
    
    private final ClienteRepository repository;
    
    public Page<ClienteResponse> execute(Pageable pageable) {
        log.info("Listando clientes - página: {}, tamanho: {}", 
                pageable.getPageNumber(), 
                pageable.getPageSize());
        
        List<Cliente> clientes = repository.findAll();
        
        // Aplicar paginação manualmente (já que não temos Page no repository)
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), clientes.size());
        
        List<Cliente> clientesPaginados = clientes.subList(start, end);
        
        List<ClienteResponse> responses = clientesPaginados.stream()
                .map(ClienteResponse::from)
                .collect(Collectors.toList());
        
        Page<ClienteResponse> page = new PageImpl<>(
                responses,
                pageable,
                clientes.size()
        );
        
        log.info("Listagem concluída: {} clientes retornados de um total de {}", 
                page.getContent().size(), 
                page.getTotalElements());
        
        return page;
    }
    
    // Métodos removidos - substituídos por BuscarClientesPorFiltroUseCase
    // executePorStatus, executePorNome, executePorTermo foram migrados para o novo use case genérico
}
