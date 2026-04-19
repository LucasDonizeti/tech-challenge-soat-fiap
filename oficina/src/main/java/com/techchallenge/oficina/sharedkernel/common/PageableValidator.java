package com.techchallenge.oficina.sharedkernel.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PageableValidator {

    /**
     * Valida e limita os campos de ordenação do Pageable para apenas os campos permitidos.
     * Se o Sort contiver campos não permitidos, eles serão removidos.
     * Se todos os campos forem inválidos, retorna um Pageable sem ordenação.
     *
     * @param pageable   O Pageable a ser validado
     * @param allowedFields Conjunto de campos permitidos para ordenação
     * @return Pageable validado com apenas campos de ordenação permitidos
     */
    public Pageable validate(Pageable pageable, Set<String> allowedFields) {
        if (pageable.getSort().isEmpty()) {
            return pageable;
        }

        var orders = pageable.getSort().stream()
                .filter(order -> allowedFields.contains(order.getProperty()))
                .toList();

        if (orders.isEmpty()) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        }

        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(orders)
        );
    }
}
