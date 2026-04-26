package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;
import com.techchallenge.oficina.administrativo.domain.model.entities.MRO;
import com.techchallenge.oficina.administrativo.domain.repositories.MRORepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListarMROsUseCase {
    
    private final MRORepository repository;
    
    public Page<MROResponse> execute(Pageable pageable) {
        log.info("Listando MROs com paginação - página: {}, tamanho: {}",
                pageable.getPageNumber(),
                pageable.getPageSize());

        Page<MRO> mros = repository.findAll(pageable);

        log.info("Listagem concluída: {} MROs retornados de um total de {}",
                mros.getContent().size(),
                mros.getTotalElements());

        return mros.map(MROResponse::from);
    }
}
