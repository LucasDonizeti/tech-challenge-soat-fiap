package com.techchallenge.oficina.administrativo.infrastructure.persistence.repositories;

import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.VeiculoEntity;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.VeiculoEntity.StatusVeiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VeiculoJpaRepository extends JpaRepository<VeiculoEntity, UUID> {

    Optional<VeiculoEntity> findByPlaca(String placa);

    boolean existsByPlaca(String placa);

    boolean existsByPlacaAndClienteId(String placa, UUID clienteId);

    boolean existsByPlacaAndClienteIdAndIdNot(String placa, UUID clienteId, UUID veiculoId);

    List<VeiculoEntity> findByClienteId(UUID clienteId);

    List<VeiculoEntity> findByClienteIdAndStatus(UUID clienteId, StatusVeiculoEntity status);

    List<VeiculoEntity> findByStatus(StatusVeiculoEntity status);
}
