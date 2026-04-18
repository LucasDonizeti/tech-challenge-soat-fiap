package com.techchallenge.oficina.administrativo.domain.repositories;

import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusVeiculo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VeiculoRepository {

    Veiculo save(Veiculo veiculo);

    Optional<Veiculo> findById(UUID id);

    Optional<Veiculo> findByPlaca(Placa placa);

    boolean existsByPlaca(Placa placa);

    List<Veiculo> findByClienteId(UUID clienteId);

    List<Veiculo> findByClienteIdAndStatus(UUID clienteId, StatusVeiculo status);

    List<Veiculo> findByStatus(StatusVeiculo status);

    List<Veiculo> findAll();

    void deleteById(UUID id);
}
