package com.techchallenge.oficina.os.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de resposta para métricas de tempo médio de execução")
public class TempoMedioExecucaoResponseDto {
    
    @Schema(description = "Tempo médio de execução em segundos")
    private Long tempoMedioSegundos;
    
    @Schema(description = "Tempo médio de execução formatado (HH:mm:ss)")
    private String tempoMedioFormatado;
    
    @Schema(description = "Tempo mínimo de execução em segundos")
    private Long tempoMinimoSegundos;
    
    @Schema(description = "Tempo mínimo de execução formatado (HH:mm:ss)")
    private String tempoMinimoFormatado;
    
    @Schema(description = "Tempo máximo de execução em segundos")
    private Long tempoMaximoSegundos;
    
    @Schema(description = "Tempo máximo de execução formatado (HH:mm:ss)")
    private String tempoMaximoFormatado;
    
    @Schema(description = "Quantidade de ordens de serviço consideradas")
    private Integer quantidadeOS;
    
    @Schema(description = "Tempo médio por tipo de serviço")
    private Map<String, TempoServicoMetricas> tempoMedioPorServico;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Métricas de tempo para um tipo de serviço específico")
    public static class TempoServicoMetricas {
        @Schema(description = "Tempo médio em segundos")
        private Long tempoMedioSegundos;
        
        @Schema(description = "Tempo médio formatado (HH:mm:ss)")
        private String tempoMedioFormatado;
        
        @Schema(description = "Tempo mínimo em segundos")
        private Long tempoMinimoSegundos;
        
        @Schema(description = "Tempo mínimo formatado (HH:mm:ss)")
        private String tempoMinimoFormatado;
        
        @Schema(description = "Tempo máximo em segundos")
        private Long tempoMaximoSegundos;
        
        @Schema(description = "Tempo máximo formatado (HH:mm:ss)")
        private String tempoMaximoFormatado;
        
        @Schema(description = "Quantidade de serviços considerados")
        private Integer quantidade;
    }
}
