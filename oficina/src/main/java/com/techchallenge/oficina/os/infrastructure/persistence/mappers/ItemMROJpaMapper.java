package com.techchallenge.oficina.os.infrastructure.persistence.mappers;

import com.techchallenge.oficina.os.domain.model.entities.ItemMRO;
import com.techchallenge.oficina.os.infrastructure.acl.dto.MROIntegrationDto;
import com.techchallenge.oficina.os.infrastructure.acl.mro.MROAdapter;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.ItemMROEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ItemMROJpaMapper {
    
    private final MROAdapter mroAdapter;
    
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
        
        // Buscar dados do MRO via ACL para preencher nome e descricao
        String mroNome = null;
        String mroDescricao = null;
        if (entity.getMroId() != null) {
            try {
                var mroOptional = mroAdapter.buscarPorId(entity.getMroId());
                if (mroOptional.isPresent()) {
                    MROIntegrationDto mro = mroOptional.get();
                    mroNome = mro.getNome();
                    mroDescricao = mro.getDescricao();
                }
            } catch (Exception e) {
                log.warn("Erro ao buscar MRO via ACL para enriquecimento: mroId={}", entity.getMroId(), e);
            }
        }
        
        return ItemMRO.reconstruir(
                entity.getId(),
                entity.getMroId(),
                mroNome,
                mroDescricao,
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
