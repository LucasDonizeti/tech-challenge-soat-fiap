package com.techchallenge.oficina.os.infrastructure.persistence.mappers;

import com.techchallenge.oficina.os.domain.model.entities.ItemMRO;
import com.techchallenge.oficina.os.domain.model.entities.MRO;
import com.techchallenge.oficina.os.domain.repositories.MRORepository;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.ItemMROEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMROJpaMapper {
    
    private final MRORepository mroRepository;
    
    public MRORepository getMroRepository() {
        return mroRepository;
    }
    
    public ItemMROEntity toEntity(ItemMRO itemMRO, UUID itemServicoId) {
        if (itemMRO == null) {
            return null;
        }
        
        return ItemMROEntity.builder()
                .id(itemMRO.getId())
                .itemServicoId(itemServicoId)
                .mroId(itemMRO.getMro() != null ? itemMRO.getMro().getId() : null)
                .quantidade(itemMRO.getQuantidade())
                .valorUnitario(itemMRO.getValorUnitario())
                .build();
    }
    
    public ItemMRO toDomain(ItemMROEntity entity, MRORepository mroRepo) {
        if (entity == null) {
            return null;
        }
        
        Optional<MRO> mro = mroRepo.findById(entity.getMroId());
        
        return ItemMRO.reconstruir(
                entity.getId(),
                mro.orElse(null),
                entity.getQuantidade(),
                entity.getValorUnitario()
        );
    }
    
    public List<ItemMROEntity> toEntityList(List<ItemMRO> mros, UUID itemServicoId) {
        if (mros == null) {
            return List.of();
        }
        
        return mros.stream()
                .map(mro -> toEntity(mro, itemServicoId))
                .collect(Collectors.toList());
    }
    
    public List<ItemMRO> toDomainList(List<ItemMROEntity> entities, MRORepository mroRepo) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(entity -> toDomain(entity, mroRepo))
                .collect(Collectors.toList());
    }
}
