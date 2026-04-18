package com.techchallenge.oficina.administrativo.infrastructure.persistence.repositories;

import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.ClienteEntity;
import com.techchallenge.oficina.administrativo.infrastructure.persistence.entities.ClienteEntity.StatusClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, UUID> {
    
    Optional<ClienteEntity> findByCpf(String cpf);
    
    Optional<ClienteEntity> findByCnpj(String cnpj);
    
    Optional<ClienteEntity> findByEmail(String email);
    
    boolean existsByCpf(String cpf);
    
    boolean existsByCnpj(String cnpj);
    
    boolean existsByEmail(String email);
    
    List<ClienteEntity> findByStatus(StatusClienteEntity status);
    
    @Query("SELECT c FROM ClienteEntity c WHERE " +
           "(:nome IS NULL OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
           "(:cpf IS NULL OR c.cpf LIKE CONCAT('%', :cpf, '%')) AND " +
           "(:cnpj IS NULL OR c.cnpj LIKE CONCAT('%', :cnpj, '%')) AND " +
           "(:email IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:status IS NULL OR c.status = :status)")
    List<ClienteEntity> findByFilter(@Param("nome") String nome,
                                     @Param("cpf") String cpf,
                                     @Param("cnpj") String cnpj,
                                     @Param("email") String email,
                                     @Param("status") StatusClienteEntity status);
}
