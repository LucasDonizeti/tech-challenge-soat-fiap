package com.techchallenge.oficina.administrativo.infrastructure.persistence.mappers;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.aggregates.ClienteRestauracaoParams;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.*;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.ClienteEntity;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.ClienteEntity.StatusClienteEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ClienteJpaMapper {
    
    private final VeiculoJpaMapper veiculoJpaMapper;
    
    public ClienteEntity toEntity(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        
        return ClienteEntity.builder()
                .id(cliente.getId())
                .nome(cliente.getNome().getValor())
                .cpf(cliente.getCpf() != null ? cliente.getCpf().getValor() : null)
                .cnpj(cliente.getCnpj() != null ? cliente.getCnpj().getValor() : null)
                .email(cliente.getEmail().getEndereco())
                .status(toEntityStatus(cliente.getStatus()))
                .criadoEm(cliente.getCriadoEm())
                .atualizadoEm(cliente.getAtualizadoEm())
                .veiculos(veiculoJpaMapper.toEntityList(cliente.getVeiculos()))
                .build();
    }
    
    public Cliente toDomain(ClienteEntity entity) {
        if (entity == null) {
            return null;
        }
        
        ClienteRestauracaoParams params = new ClienteRestauracaoParams(
                entity.getId(),
                Nome.of(entity.getNome()),
                entity.getCpf() != null ? CPF.of(entity.getCpf()) : null,
                entity.getCnpj() != null ? CNPJ.of(entity.getCnpj()) : null,
                Email.of(entity.getEmail()),
                toDomainStatus(entity.getStatus()),
                entity.getCriadoEm(),
                entity.getAtualizadoEm()
        );
        
        Cliente cliente = Cliente.restaurar(params);
        
        // Adiciona os veículos ao cliente
        List<Veiculo> veiculos = veiculoJpaMapper.toDomainList(entity.getVeiculos());
        veiculos.forEach(cliente::adicionarVeiculo);
        
        return cliente;
    }
    
    public List<ClienteEntity> toEntityList(List<Cliente> clientes) {
        if (clientes == null) {
            return List.of();
        }
        
        return clientes.stream()
                .map(this::toEntity)
                .toList();
    }
    
    public List<Cliente> toDomainList(List<ClienteEntity> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(this::toDomain)
                .toList();
    }
    
    private StatusClienteEntity toEntityStatus(StatusCliente status) {
        return switch (status) {
            case ATIVO -> StatusClienteEntity.ATIVO;
            case INATIVO -> StatusClienteEntity.INATIVO;
        };
    }
    
    private StatusCliente toDomainStatus(StatusClienteEntity status) {
        return switch (status) {
            case ATIVO -> StatusCliente.ATIVO;
            case INATIVO -> StatusCliente.INATIVO;
        };
    }
}
