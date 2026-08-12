package com.techchallenge.oficina.administrativo.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "servicos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicoEntity {
    
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;
    
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;
    
    @Column(name = "codigo", nullable = false, length = 30, unique = true)
    private String codigo;
    
    @Column(name = "descricao", length = 500)
    private String descricao;
    
    @Column(name = "preco", nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;
    
    @Column(name = "ativo", nullable = false)
    private Boolean ativo;
    
    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;
    
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;
}
