package com.techchallenge.oficina.administrativo.infrastructure.persistence.repositories;

import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusVeiculo;
import com.techchallenge.oficina.administrativo.domain.repositories.VeiculoRepository;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.ClienteEntity;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.VeiculoEntity;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.mappers.VeiculoJpaMapper;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.repositories.ClienteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class VeiculoRepositoryImpl implements VeiculoRepository {

    private final VeiculoJpaRepository jpaRepository;
    private final VeiculoJpaMapper mapper;
    private final ClienteJpaRepository clienteJpaRepository;

    @Override
    public Veiculo save(Veiculo veiculo) {
        VeiculoEntity entity = mapper.toEntity(veiculo);

        // Se o veículo tem um cliente, buscar a entidade cliente do banco
        if (veiculo.getCliente() != null) {
            ClienteEntity clienteEntity = clienteJpaRepository.findById(veiculo.getCliente().getId())
                    .orElse(null);
            entity.setCliente(clienteEntity);
        }

        VeiculoEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Veiculo> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Veiculo> findByPlaca(Placa placa) {
        return jpaRepository.findByPlaca(placa.getValor())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByPlaca(Placa placa) {
        return jpaRepository.existsByPlaca(placa.getValor());
    }

    @Override
    public List<Veiculo> findByClienteId(UUID clienteId) {
        List<VeiculoEntity> entities = jpaRepository.findByClienteId(clienteId);
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Veiculo> findByClienteIdAndStatus(UUID clienteId, StatusVeiculo status) {
        VeiculoEntity.StatusVeiculoEntity entityStatus = toEntityStatus(status);
        List<VeiculoEntity> entities = jpaRepository.findByClienteIdAndStatus(clienteId, entityStatus);
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Veiculo> findByStatus(StatusVeiculo status) {
        VeiculoEntity.StatusVeiculoEntity entityStatus = toEntityStatus(status);
        List<VeiculoEntity> entities = jpaRepository.findByStatus(entityStatus);
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Veiculo> findAll() {
        List<VeiculoEntity> entities = jpaRepository.findAll();
        return mapper.toDomainList(entities);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private VeiculoEntity.StatusVeiculoEntity toEntityStatus(StatusVeiculo status) {
        return switch (status) {
            case ATIVO -> VeiculoEntity.StatusVeiculoEntity.ATIVO;
            case INATIVO -> VeiculoEntity.StatusVeiculoEntity.INATIVO;
        };
    }
}
