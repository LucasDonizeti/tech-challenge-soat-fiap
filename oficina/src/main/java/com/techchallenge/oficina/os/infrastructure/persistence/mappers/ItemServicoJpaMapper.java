package com.techchallenge.oficina.os.infrastructure.persistence.mappers;

import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import com.techchallenge.oficina.os.domain.model.entities.Servico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusItemServico;
import com.techchallenge.oficina.os.domain.repositories.ServicoRepository;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.ItemServicoEntity;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.ItemServicoEntity.StatusItemServicoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemServicoJpaMapper {
    
    private final ServicoRepository servicoRepository;
    private final ItemMROJpaMapper itemMROJpaMapper;
    
    public ServicoRepository getServicoRepository() {
        return servicoRepository;
    }
    
    public ItemServicoEntity toEntity(ItemServico itemServico, UUID ordemServicoId) {
        if (itemServico == null) {
            return null;
        }
        
        ItemServicoEntity entity = ItemServicoEntity.builder()
                .id(itemServico.getId())
                .ordemServicoId(ordemServicoId)
                .servicoId(itemServico.getServico() != null ? itemServico.getServico().getId() : null)
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
    
    public ItemServico toDomain(ItemServicoEntity entity, ServicoRepository servicoRepo) {
        if (entity == null) {
            return null;
        }
        
        Optional<Servico> servico = servicoRepo.findById(entity.getServicoId());
        
        StatusItemServico status = entity.getStatus() != null ? StatusItemServico.valueOf(entity.getStatus().name()) : null;
        
        ItemServico itemServico = ItemServico.reconstruir(
                entity.getId(),
                servico.orElse(null),
                status,
                entity.getObservacoes(),
                entity.getValorServico(),
                entity.getValorMro()
        );
        
        // Load and add MROs if they exist
        if (entity.getMros() != null && !entity.getMros().isEmpty()) {
            var mroRepository = itemMROJpaMapper.getMroRepository();
            var mros = itemMROJpaMapper.toDomainList(entity.getMros(), mroRepository);
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
    
    public List<ItemServico> toDomainList(List<ItemServicoEntity> entities, ServicoRepository servicoRepo) {
        if (entities == null) {
            return List.of();
        }
        
        return entities.stream()
                .map(entity -> toDomain(entity, servicoRepo))
                .collect(Collectors.toList());
    }
}
