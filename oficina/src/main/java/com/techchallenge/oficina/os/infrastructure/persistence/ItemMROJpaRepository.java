package com.techchallenge.oficina.os.infrastructure.persistence;

import com.techchallenge.oficina.os.infrastructure.persistence.entities.ItemMROEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemMROJpaRepository extends JpaRepository<ItemMROEntity, UUID> {
    
    List<ItemMROEntity> findByItemServicoId(UUID itemServicoId);
}
