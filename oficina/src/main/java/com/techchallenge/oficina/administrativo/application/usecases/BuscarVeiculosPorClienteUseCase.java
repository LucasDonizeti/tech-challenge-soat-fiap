package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.ports.input.BuscarVeiculosPorClienteInput;
import com.techchallenge.oficina.administrativo.application.usecases.responses.VeiculoResponse;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.repositories.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BuscarVeiculosPorClienteUseCase implements BuscarVeiculosPorClienteInput {

    private final VeiculoRepository repository;

    public List<VeiculoResponse> execute(UUID clienteId) {
        log.info("Buscando veículos do cliente: ID={}", clienteId);

        List<Veiculo> veiculos = repository.findByClienteId(clienteId);

        log.info("Encontrados {} veículos para o cliente ID={}", veiculos.size(), clienteId);

        return veiculos.stream()
                .map(VeiculoResponse::from)
                .toList();
    }
}

