package com.techchallenge.oficina.os.infrastructure.persistence.mappers;

import com.techchallenge.oficina.os.domain.model.entities.MRO;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.MROEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MROJpaMapper {
    
    public MROEntity toEntity(MRO mro) {
        if (mro == null) {
            return null;
        }
        
        return MROEntity.builder()
                .id(mro.getId())
                .nome(mro.getNome())
                .descricao(mro.getDescricao())
                .tipo(mro.getTipo())
                .quantidadeEstoque(mro.getQuantidadeEstoque())
                .precoUnitario(mro.getPrecoUnitario())
                .ativo(mro.getAtivo())
                .criadoEm(mro.getCriadoEm())
                .atualizadoEm(mro.getAtualizadoEm())
                .build();
    }
    
    public MRO toDomain(MROEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return MRO.reconstruir(
            entity.getId(),
            entity.getNome(),
            entity.getDescricao(),
            entity.getTipo(),
            entity.getQuantidadeEstoque(),
            entity.getPrecoUnitario(),
            entity.getAtivo(),
            entity.getCriadoEm(),
            entity.getAtualizadoEm()
        );
    }
    
    public List<MRO> toDomainList(List<MROEntity> entities) {
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
}
