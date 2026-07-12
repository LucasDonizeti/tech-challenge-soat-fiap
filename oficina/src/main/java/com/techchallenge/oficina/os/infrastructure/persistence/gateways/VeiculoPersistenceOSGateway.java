package com.techchallenge.oficina.os.infrastructure.persistence.gateways;

import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusVeiculo;
import com.techchallenge.oficina.administrativo.domain.repositories.VeiculoRepository;
import com.techchallenge.oficina.os.application.usecases.ports.output.VeiculoGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class VeiculoPersistenceOSGateway implements VeiculoGateway {

    private final VeiculoRepository veiculoRepository;

    @Override
    public Veiculo save(Veiculo veiculo) {
        return veiculoRepository.save(veiculo);
    }

    @Override
    public Optional<Veiculo> findById(UUID id) {
        return veiculoRepository.findById(id);
    }

    @Override
    public Optional<Veiculo> findByPlaca(Placa placa) {
        return veiculoRepository.findByPlaca(placa);
    }

    @Override
    public boolean existsByPlaca(Placa placa) {
        return veiculoRepository.existsByPlaca(placa);
    }

    @Override
    public boolean existsByPlacaAndClienteId(Placa placa, UUID clienteId) {
        return veiculoRepository.existsByPlacaAndClienteId(placa, clienteId);
    }

    @Override
    public boolean existsByPlacaAndClienteIdAndIdNot(Placa placa, UUID clienteId, UUID veiculoId) {
        return veiculoRepository.existsByPlacaAndClienteIdAndIdNot(placa, clienteId, veiculoId);
    }

    @Override
    public List<Veiculo> findByClienteId(UUID clienteId) {
        return veiculoRepository.findByClienteId(clienteId);
    }

    @Override
    public List<Veiculo> findByClienteIdAndStatus(UUID clienteId, StatusVeiculo status) {
        return veiculoRepository.findByClienteIdAndStatus(clienteId, status);
    }

    @Override
    public List<Veiculo> findByStatus(StatusVeiculo status) {
        return veiculoRepository.findByStatus(status);
    }

    @Override
    public List<Veiculo> findAll() {
        return veiculoRepository.findAll();
    }

    @Override
    public Page<Veiculo> findAll(Pageable pageable) {
        return veiculoRepository.findAll(pageable);
    }

    @Override
    public void deleteById(UUID id) {
        veiculoRepository.deleteById(id);
    }
}
