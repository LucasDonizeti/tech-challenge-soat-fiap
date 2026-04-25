package com.techchallenge.oficina.os.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "itens_servico")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemServicoEntity {
    
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;
    
    @Column(name = "ordem_servico_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID ordemServicoId;
    
    @Column(name = "servico_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID servicoId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusItemServicoEntity status;
    
    @Column(name = "observacoes", length = 500)
    private String observacoes;
    
    @Column(name = "valor_servico", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorServico;
    
    @Column(name = "valor_mro", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorMro;
    
    @OneToMany(mappedBy = "itemServicoId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ItemMROEntity> mros = new ArrayList<>();
    
    public enum StatusItemServicoEntity {
        PENDENTE,
        EM_ANDAMENTO,
        CONCLUIDO,
        CANCELADO
    }
}
