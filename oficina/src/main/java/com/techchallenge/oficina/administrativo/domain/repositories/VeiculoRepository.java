package com.techchallenge.oficina.administrativo.domain.repositories;

import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusVeiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VeiculoRepository {

    Veiculo save(Veiculo veiculo);

    Optional<Veiculo> findById(UUID id);

    Optional<Veiculo> findByPlaca(Placa placa);

    boolean existsByPlaca(Placa placa);

    boolean existsByPlacaAndClienteId(Placa placa, UUID clienteId);

    boolean existsByPlacaAndClienteIdAndIdNot(Placa placa, UUID clienteId, UUID veiculoId);

    List<Veiculo> findByClienteId(UUID clienteId);

    List<Veiculo> findByClienteIdAndStatus(UUID clienteId, StatusVeiculo status);

    List<Veiculo> findByStatus(StatusVeiculo status);

    List<Veiculo> findAll();

    Page<Veiculo> findAll(Pageable pageable);

    void deleteById(UUID id);
}
