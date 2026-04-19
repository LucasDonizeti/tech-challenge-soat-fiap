package com.techchallenge.oficina.os.infrastructure.persistence.repositories;

import com.techchallenge.oficina.os.infrastructure.persistence.entities.ServicoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServicoJpaRepository extends JpaRepository<ServicoEntity, UUID> {
    
    @Query("SELECT s FROM ServicoEntity s WHERE s.ativo = :ativo")
    List<ServicoEntity> findByAtivo(@Param("ativo") Boolean ativo);
    
    @Query("SELECT s FROM ServicoEntity s WHERE s.nome LIKE %:nome%")
    List<ServicoEntity> findByNomeContaining(@Param("nome") String nome);
    
    @Query("SELECT s FROM ServicoEntity s WHERE (:ativo IS NULL OR s.ativo = :ativo) AND (:nome IS NULL OR s.nome LIKE %:nome%)")
    Page<ServicoEntity> findByFilter(@Param("ativo") Boolean ativo, @Param("nome") String nome, Pageable pageable);
}
