package com.techchallenge.oficina.administrativo.infrastructure.persistence.repositories;

import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.MROEntity;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.TipoMRO;
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
public interface MROJpaRepository extends JpaRepository<MROEntity, UUID> {
    
    Optional<MROEntity> findByCodigo(String codigo);

    @Query("SELECT m FROM MROEntity m WHERE m.ativo = :ativo")
    List<MROEntity> findByAtivo(@Param("ativo") Boolean ativo);
    
    @Query("SELECT m FROM MROEntity m WHERE m.tipo = :tipo")
    List<MROEntity> findByTipo(@Param("tipo") TipoMRO tipo);
    
    @Query("SELECT m FROM MROEntity m WHERE m.nome LIKE %:nome%")
    List<MROEntity> findByNomeContaining(@Param("nome") String nome);
    
    @Query("SELECT m FROM MROEntity m WHERE m.quantidadeEstoque > :quantidade")
    List<MROEntity> findByQuantidadeEstoqueGreaterThan(@Param("quantidade") Integer quantidade);
    
    @Query("SELECT m FROM MROEntity m WHERE (:ativo IS NULL OR m.ativo = :ativo) AND (:tipo IS NULL OR m.tipo = :tipo) AND (:nome IS NULL OR m.nome LIKE %:nome%)")
    Page<MROEntity> findByFilter(@Param("ativo") Boolean ativo, @Param("tipo") TipoMRO tipo, @Param("nome") String nome, Pageable pageable);
}
