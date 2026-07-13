package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.ports.input.ListarServicosInput;
import com.techchallenge.oficina.administrativo.application.usecases.ports.output.ServicoGateway;
import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;
import com.techchallenge.oficina.administrativo.domain.model.entities.Servico;
import com.techchallenge.oficina.administrativo.domain.repositories.ServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
public class ListarServicosUseCase implements ListarServicosInput {

    private final ServicoGateway servicoGateway;

    public Page<ServicoResponse> execute(Pageable pageable) {
        log.info("Listando serviços com paginação - página: {}, tamanho: {}",
                pageable.getPageNumber(),
                pageable.getPageSize());

        Page<Servico> servicos = servicoGateway.findAll(pageable);

        log.info("Listagem concluída: {} serviços retornados de um total de {}",
                servicos.getContent().size(),
                servicos.getTotalElements());

        return servicos.map(ServicoResponse::from);
    }
}
