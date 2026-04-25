package com.techchallenge.oficina.os.infrastructure.persistence;

import com.techchallenge.oficina.os.infrastructure.persistence.entities.ItemServicoEntity;
import com.techchallenge.oficina.os.infrastructure.persistence.entities.ItemServicoEntity.StatusItemServicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemServicoJpaRepository extends JpaRepository<ItemServicoEntity, UUID> {
    
    List<ItemServicoEntity> findByOrdemServicoId(UUID ordemServicoId);
    
    List<ItemServicoEntity> findByOrdemServicoIdAndStatus(UUID ordemServicoId, StatusItemServicoEntity status);
}
