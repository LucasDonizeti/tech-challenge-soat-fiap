package com.techchallenge.oficina.os.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "itens_mro")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemMROEntity {
    
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;
    
    @Column(name = "item_servico_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID itemServicoId;
    
    @Column(name = "mro_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID mroId;
    
    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;
    
    @Column(name = "valor_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorUnitario;
}
