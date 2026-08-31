package com.techchallenge.oficina.os.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ordens_servico")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServicoEntity {
    
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;
    
    @Column(name = "cliente_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID clienteId;
    
    @Column(name = "veiculo_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID veiculoId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusOSEntity status;
    
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;
    
    @Column(name = "data_inicio_execucao")
    private LocalDateTime dataInicioExecucao;
    
    @Column(name = "data_finalizacao")
    private LocalDateTime dataFinalizacao;

    @Column(name = "data_ultima_mudanca_status")
    private LocalDateTime dataUltimaMudancaStatus;

    @OneToMany(mappedBy = "ordemServicoId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ItemServicoEntity> itensServico = new ArrayList<>();
    
    public enum StatusOSEntity {
        RECEBIDA,
        EM_DIAGNOSTICO,
        AGUARDANDO_APROVACAO,
        EM_EXECUCAO,
        FINALIZADA,
        ENTREGUE,
        CANCELADA
    }
}
