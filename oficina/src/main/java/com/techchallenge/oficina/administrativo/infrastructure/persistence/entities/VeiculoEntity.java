package com.techchallenge.oficina.administrativo.infrastructure.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "veiculos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VeiculoEntity {

    public enum StatusVeiculoEntity {
        ATIVO,
        INATIVO
    }
    
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;
    
    @Column(name = "placa", length = 8, nullable = false, unique = true)
    private String placa;
    
    @Column(name = "marca", length = 50, nullable = false)
    private String marca;
    
    @Column(name = "modelo", length = 50, nullable = false)
    private String modelo;
    
    @Column(name = "ano", nullable = false)
    private Integer ano;
    
    @Column(name = "cor", length = 30)
    private String cor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusVeiculoEntity status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteEntity cliente;
    
    @Column(name = "criado_em", columnDefinition = "DATETIME(6)", nullable = false)
    private LocalDateTime criadoEm;
    
    @Column(name = "atualizado_em", columnDefinition = "DATETIME(6)", nullable = false)
    private LocalDateTime atualizadoEm;
}
