package com.techchallenge.oficina.administrativo.infrastructure.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clientes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEntity {
    
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;
    
    @Column(name = "nome", length = 100, nullable = false)
    private String nome;
    
    @Column(name = "cpf", length = 11, unique = true)
    private String cpf;
    
    @Column(name = "cnpj", length = 14, unique = true)
    private String cnpj;
    
    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private StatusClienteEntity status;
    
    @Column(name = "criado_em", columnDefinition = "DATETIME(6)", nullable = false)
    private LocalDateTime criadoEm;
    
    @Column(name = "atualizado_em", columnDefinition = "DATETIME(6)", nullable = false)
    private LocalDateTime atualizadoEm;
    
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VeiculoEntity> veiculos;
    
    public enum StatusClienteEntity {
        ATIVO,
        INATIVO
    }
}
