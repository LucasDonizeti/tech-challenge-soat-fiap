package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.responses.VeiculoResponse;
import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoVeiculoException;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.repositories.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BuscarVeiculoUseCase {

    private final VeiculoRepository repository;

    public VeiculoResponse execute(UUID veiculoId) {
        log.info("Buscando veículo por ID: {}", veiculoId);

        return repository.findById(veiculoId)
                .map(veiculo -> {
                    log.info("Veículo encontrado: ID={}, Placa={}",
                            veiculo.getId(), veiculo.getPlaca().getFormatada());
                    return VeiculoResponse.from(veiculo);
                })
                .orElseThrow(() -> {
                    log.warn("Veículo não encontrado: ID={}", veiculoId);
                    return new ValidacaoVeiculoException("Veículo não encontrado: " + veiculoId);
                });
    }
}
