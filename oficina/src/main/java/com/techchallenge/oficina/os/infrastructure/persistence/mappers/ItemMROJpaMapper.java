package com.techchallenge.oficina.os.infrastructure.persistence.mappers;

import com.techchallenge.oficina.os.domain.model.entities.ItemMRO;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.ItemMROEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ItemMROJpaMapper {
    
    public ItemMROEntity toEntity(ItemMRO itemMRO, UUID itemServicoId) {
        if (itemMRO == null) {
            return null;
        }
        
        return ItemMROEntity.builder()
                .id(itemMRO.getId())
                .itemServicoId(itemServicoId)
                .mroId(itemMRO.getMroId())
                .quantidade(itemMRO.getQuantidade())
                .valorUnitario(itemMRO.getValorUnitario())
                .build();
    }
    
    public ItemMRO toDomain(ItemMROEntity entity) {
        if (entity == null) {
            return null;
        }
        
        // Campos nome e descricao não são persistidos no banco, ficam null
        // Devem ser preenchidos via ACL quando necessário
        return ItemMRO.reconstruir(
                entity.getId(),
                entity.getMroId(),
                null,  // mroNome - não persistido no banco
                null,  // mroDescricao - não persistido no banco
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
    
    public List<ItemMRO> toDomainList(List<ItemMROEntity> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
}
