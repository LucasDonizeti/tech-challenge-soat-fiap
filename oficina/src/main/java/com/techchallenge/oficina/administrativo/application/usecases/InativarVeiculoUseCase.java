package com.techchallenge.oficina.administrativo.application.usecases;

import com.techchallenge.oficina.administrativo.application.usecases.ports.input.InativarVeiculoInput;
import com.techchallenge.oficina.administrativo.application.usecases.ports.output.VeiculoGateway;
import com.techchallenge.oficina.administrativo.application.usecases.responses.VeiculoResponse;
import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoVeiculoException;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.repositories.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class InativarVeiculoUseCase implements InativarVeiculoInput {

        private final VeiculoGateway veiculoGateway;

    public VeiculoResponse execute(UUID veiculoId) {
        log.info("Iniciando inativação do veículo: ID={}", veiculoId);

        // Buscar veículo existente
        Veiculo veiculo = veiculoGateway.findById(veiculoId)
                .orElseThrow(() -> {
                    log.warn("Veículo não encontrado para inativação: ID={}", veiculoId);
                    return new ValidacaoVeiculoException("Veículo não encontrado: " + veiculoId);
                });

        // Inativar veículo
        veiculo.inativar();

        // Persistir alterações
        Veiculo savedVeiculo = veiculoGateway.save(veiculo);

        log.info("Veículo inativado com sucesso: ID={}, Placa={}",
                veiculo.getId(),
                veiculo.getPlaca().getFormatada());

        return VeiculoResponse.from(savedVeiculo);
    }
}
