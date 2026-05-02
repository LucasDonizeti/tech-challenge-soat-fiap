package com.techchallenge.oficina.os.infrastructure.persistence.mappers;

import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusItemServico;
import com.techchallenge.oficina.os.infrastructure.acl.dto.ServicoIntegrationDto;
import com.techchallenge.oficina.os.infrastructure.acl.servico.ServicoAdapter;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.ItemServicoEntity;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.ItemServicoEntity.StatusItemServicoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ItemServicoJpaMapper {
    
    private final ItemMROJpaMapper itemMROJpaMapper;
    private final ServicoAdapter servicoAdapter;
    
    public ItemServicoEntity toEntity(ItemServico itemServico, UUID ordemServicoId) {
        if (itemServico == null) {
            return null;
        }
        
        ItemServicoEntity entity = ItemServicoEntity.builder()
                .id(itemServico.getId())
                .ordemServicoId(ordemServicoId)
                .servicoId(itemServico.getServicoId())
                .status(itemServico.getStatus() != null ? StatusItemServicoEntity.valueOf(itemServico.getStatus().name()) : null)
                .observacoes(itemServico.getObservacoes())
                .valorServico(itemServico.getValorServico())
                .valorMro(itemServico.getValorMro())
                .build();
        
        if (itemServico.getMrosServicos() != null && !itemServico.getMrosServicos().isEmpty()) {
            entity.setMros(itemMROJpaMapper.toEntityList(itemServico.getMrosServicos(), itemServico.getId()));
        }
        
        return entity;
    }
    
    public ItemServico toDomain(ItemServicoEntity entity) {
        if (entity == null) {
            return null;
        }
        
        StatusItemServico status = entity.getStatus() != null ? StatusItemServico.valueOf(entity.getStatus().name()) : null;
        
        // Buscar dados do serviço via ACL para preencher nome e descricao
        String servicoNome = null;
        String servicoDescricao = null;
        if (entity.getServicoId() != null) {
            try {
                var servicoOptional = servicoAdapter.buscarPorId(entity.getServicoId());
                if (servicoOptional.isPresent()) {
                    ServicoIntegrationDto servico = servicoOptional.get();
                    servicoNome = servico.getNome();
                    servicoDescricao = servico.getDescricao();
                }
            } catch (Exception e) {
                log.warn("Erro ao buscar serviço via ACL para enriquecimento: servicoId={}", entity.getServicoId(), e);
            }
        }
        
        ItemServico itemServico = ItemServico.reconstruir(
                entity.getId(),
                entity.getServicoId(),
                servicoNome,
                servicoDescricao,
                status,
                entity.getObservacoes(),
                entity.getValorServico(),
                entity.getValorMro()
        );
        
        // Load and add MROs if they exist
        if (entity.getMros() != null && !entity.getMros().isEmpty()) {
            var mros = itemMROJpaMapper.toDomainList(entity.getMros());
            mros.forEach(itemServico::adicionarMRO);
        }
        
        return itemServico;
    }
    
    public List<ItemServicoEntity> toEntityList(List<ItemServico> itensServico, UUID ordemServicoId) {
        if (itensServico == null) {
            return List.of();
        }
        
        return itensServico.stream()
                .map(item -> toEntity(item, ordemServicoId))
                .collect(Collectors.toList());
    }
    
    public List<ItemServico> toDomainList(List<ItemServicoEntity> entities) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
}
