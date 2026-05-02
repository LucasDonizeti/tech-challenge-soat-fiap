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
    
    @Schema(description = "Tempo médio de execução em minutos")
    private Long tempoMedioMinutos;
    
    @Schema(description = "Tempo médio de execução formatado (HH:mm:ss)")
    private String tempoMedioFormatado;
    
    @Schema(description = "Tempo mínimo de execução em minutos")
    private Long tempoMinimoMinutos;
    
    @Schema(description = "Tempo mínimo de execução formatado (HH:mm:ss)")
    private String tempoMinimoFormatado;
    
    @Schema(description = "Tempo máximo de execução em minutos")
    private Long tempoMaximoMinutos;
    
    @Schema(description = "Tempo máximo de execução formatado (HH:mm:ss)")
    private String tempoMaximoFormatado;
    
    @Schema(description = "Quantidade de ordens de serviço consideradas")
    private Integer quantidadeOS;
    
    @Schema(description = "Tempo médio por tipo de serviço (opcional)")
    private Map<String, Long> tempoMedioPorServico;
}
