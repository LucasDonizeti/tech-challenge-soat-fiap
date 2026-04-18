package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.responses.VeiculoResponse;
import com.techchallenge.oficina.administrativo.domain.repositories.VeiculoRepository;
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
public class ListarVeiculosUseCase {

    private final VeiculoRepository repository;

    public Page<VeiculoResponse> execute(Pageable pageable) {
        log.info("Listando veículos - página: {}, tamanho: {}",
                pageable.getPageNumber(),
                pageable.getPageSize());

        List<VeiculoResponse> veiculos = repository.findAll().stream()
                .map(VeiculoResponse::from)
                .toList();

        // Aplicar paginação manualmente (já que não temos Page no repository)
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), veiculos.size());

        // Verificar se start está além do tamanho da lista
        if (start >= veiculos.size()) {
            return new PageImpl<>(List.of(), pageable, veiculos.size());
        }

        List<VeiculoResponse> veiculosPaginados = veiculos.subList(start, end);

        Page<VeiculoResponse> page = new PageImpl<>(
                veiculosPaginados,
                pageable,
                veiculos.size()
        );

        log.info("Listagem concluída: {} veículos retornados de um total de {}",
                page.getContent().size(),
                page.getTotalElements());

        return page;
    }
}
