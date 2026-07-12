package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.ports.input.ListarVeiculosInput;
import com.techchallenge.oficina.administrativo.application.usecases.ports.output.VeiculoGateway;
import com.techchallenge.oficina.administrativo.application.usecases.responses.VeiculoResponse;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.repositories.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Slf4j
public class ListarVeiculosUseCase implements ListarVeiculosInput {

    private final VeiculoGateway veiculoGateway;

    public Page<VeiculoResponse> execute(Pageable pageable) {
        log.info("Listando veículos - página: {}, tamanho: {}",
                pageable.getPageNumber(),
                pageable.getPageSize());

        Page<Veiculo> veiculos = veiculoGateway.findAll(pageable);

        log.info("Listagem concluída: {} veículos retornados de um total de {}",
                veiculos.getContent().size(),
                veiculos.getTotalElements());

        return veiculos.map(VeiculoResponse::from);
    }
}
