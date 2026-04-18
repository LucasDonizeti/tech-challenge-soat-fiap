package com.techchallenge.oficina.administrativo.infrastructure.persistence.mappers;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.ClienteRestauracaoParams;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.entities.VeiculoRestauracaoParams;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.*;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.ClienteEntity;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.ClienteEntity.StatusClienteEntity;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.VeiculoEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VeiculoJpaMapper {

    public VeiculoEntity toEntity(Veiculo veiculo) {
        if (veiculo == null) {
            return null;
        }

        ClienteEntity clienteEntity = null;
        if (veiculo.getCliente() != null) {
            // Criar apenas uma referência com ID para evitar loop circular
            clienteEntity = ClienteEntity.builder()
                    .id(veiculo.getCliente().getId())
                    .build();
        }

        return VeiculoEntity.builder()
                .id(veiculo.getId())
                .placa(veiculo.getPlaca().getValor())
                .marca(veiculo.getMarca())
                .modelo(veiculo.getModelo())
                .ano(veiculo.getAno())
                .cor(veiculo.getCor())
                .status(toEntityStatus(veiculo.getStatus()))
                .criadoEm(veiculo.getCriadoEm())
                .atualizadoEm(veiculo.getAtualizadoEm())
                .cliente(clienteEntity)
                .build();
    }
    
    public Veiculo toDomain(VeiculoEntity entity) {
        if (entity == null) {
            return null;
        }

        VeiculoRestauracaoParams params = new VeiculoRestauracaoParams(
                entity.getId(),
                Placa.of(entity.getPlaca()),
                entity.getMarca(),
                entity.getModelo(),
                entity.getAno(),
                entity.getCor(),
                toDomainStatus(entity.getStatus()),
                entity.getCriadoEm(),
                entity.getAtualizadoEm()
        );

        Veiculo veiculo = Veiculo.restaurar(params);

        // Converter cliente se existir
        if (entity.getCliente() != null) {
            Cliente cliente = criarClienteSimplificado(entity.getCliente());
            veiculo.setCliente(cliente);
        }

        return veiculo;
    }

    private Cliente criarClienteSimplificado(ClienteEntity clienteEntity) {
        // Cria um Cliente simplificado apenas com ID e nome para evitar loop circular
        ClienteRestauracaoParams params = new ClienteRestauracaoParams(
                clienteEntity.getId(),
                Nome.of(clienteEntity.getNome()),
                clienteEntity.getCpf() != null ? CPF.of(clienteEntity.getCpf()) : null,
                clienteEntity.getCnpj() != null ? CNPJ.of(clienteEntity.getCnpj()) : null,
                Email.of(clienteEntity.getEmail()),
                toDomainStatus(clienteEntity.getStatus()),
                clienteEntity.getCriadoEm(),
                clienteEntity.getAtualizadoEm()
        );
        return Cliente.restaurar(params);
    }
    
    public List<VeiculoEntity> toEntityList(List<Veiculo> veiculos) {
        if (veiculos == null) {
            return List.of();
        }
        
        return veiculos.stream()
                .map(this::toEntity)
                .toList();
    }
    
    public List<Veiculo> toDomainList(List<VeiculoEntity> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toDomain)
                .toList();
    }

    private VeiculoEntity.StatusVeiculoEntity toEntityStatus(StatusVeiculo status) {
        return switch (status) {
            case ATIVO -> VeiculoEntity.StatusVeiculoEntity.ATIVO;
            case INATIVO -> VeiculoEntity.StatusVeiculoEntity.INATIVO;
        };
    }

    private StatusVeiculo toDomainStatus(VeiculoEntity.StatusVeiculoEntity status) {
        return switch (status) {
            case ATIVO -> StatusVeiculo.ATIVO;
            case INATIVO -> StatusVeiculo.INATIVO;
        };
    }

    private StatusCliente toDomainStatus(StatusClienteEntity status) {
        return switch (status) {
            case ATIVO -> StatusCliente.ATIVO;
            case INATIVO -> StatusCliente.INATIVO;
        };
    }
}
