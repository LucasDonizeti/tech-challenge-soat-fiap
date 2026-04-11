package com.techchallenge.oficina.administrativo.infrastructure.persistence.mappers;

import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.entities.VeiculoRestauracaoParams;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.*;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.VeiculoEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VeiculoJpaMapper {
    
    public VeiculoEntity toEntity(Veiculo veiculo) {
        if (veiculo == null) {
            return null;
        }
        
        return VeiculoEntity.builder()
                .id(veiculo.getId())
                .placa(veiculo.getPlaca().getValor())
                .marca(veiculo.getMarca())
                .modelo(veiculo.getModelo())
                .ano(veiculo.getAno())
                .cor(veiculo.getCor())
                .criadoEm(veiculo.getCriadoEm())
                .atualizadoEm(veiculo.getAtualizadoEm())
                // cliente será configurado pelo mapper do cliente
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
                entity.getCriadoEm(),
                entity.getAtualizadoEm()
        );
        
        return Veiculo.restaurar(params);
    }
    
    public List<VeiculoEntity> toEntityList(List<Veiculo> veiculos) {
        if (veiculos == null) {
            return List.of();
        }
        
        return veiculos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
    
    public List<Veiculo> toDomainList(List<VeiculoEntity> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
}
