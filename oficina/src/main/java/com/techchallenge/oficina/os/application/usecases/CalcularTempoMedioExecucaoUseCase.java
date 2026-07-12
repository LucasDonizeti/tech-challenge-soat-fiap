package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.application.usecases.ports.input.CalcularTempoMedioExecucaoInput;
import com.techchallenge.oficina.os.application.usecases.ports.output.OrdemServicoGateway;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
import com.techchallenge.oficina.os.domain.repositories.OrdemServicoRepository;
import com.techchallenge.oficina.os.web.dto.TempoMedioExecucaoResponseDto;
import com.techchallenge.oficina.os.web.dto.TempoMedioExecucaoResponseDto.TempoServicoMetricas;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class CalcularTempoMedioExecucaoUseCase implements CalcularTempoMedioExecucaoInput {
    
    private final OrdemServicoGateway ordemServicoGateway;
    
    public TempoMedioExecucaoResponseDto execute(UUID servicoId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        log.info("Calculando tempo médio de execução: servicoId={}, dataInicio={}, dataFim={}", 
                servicoId, dataInicio, dataFim);
        
        // Buscar todas as OSs em status FINALIZADA ou ENTREGUE
        // Nota: Como o repository não tem método específico, precisamos buscar todas e filtrar
        var todasOS = ordemServicoGateway.findAll(org.springframework.data.domain.Pageable.unpaged());
        
        // Filtrar por status e período
        var osFiltradas = todasOS.getContent().stream()
                .filter(os -> os.getStatus() == StatusOS.FINALIZADA || os.getStatus() == StatusOS.ENTREGUE)
                .filter(os -> os.getDataInicioExecucao() != null && os.getDataFinalizacao() != null)
                .filter(os -> dataInicio == null || !os.getDataFinalizacao().isBefore(dataInicio))
                .filter(os -> dataFim == null || !os.getDataFinalizacao().isAfter(dataFim))
                .toList();
        
        if (osFiltradas.isEmpty()) {
            log.warn("Nenhuma OS encontrada com os critérios especificados");
            return TempoMedioExecucaoResponseDto.builder()
                    .tempoMedioSegundos(0L)
                    .tempoMedioFormatado("00:00:00")
                    .tempoMinimoSegundos(0L)
                    .tempoMinimoFormatado("00:00:00")
                    .tempoMaximoSegundos(0L)
                    .tempoMaximoFormatado("00:00:00")
                    .quantidadeOS(0)
                    .tempoMedioPorServico(new HashMap<>())
                    .build();
        }
        
        // Calcular tempos de execução da OS (baseado nas datas da OS)
        var temposSegundos = osFiltradas.stream()
                .map(os -> Duration.between(os.getDataInicioExecucao(), os.getDataFinalizacao()).toSeconds())
                .toList();
        
        long tempoMedio = temposSegundos.stream()
                .mapToLong(Long::longValue)
                .sum() / temposSegundos.size();
        
        long tempoMinimo = temposSegundos.stream()
                .mapToLong(Long::longValue)
                .min()
                .orElse(0L);
        
        long tempoMaximo = temposSegundos.stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);
        
        // Calcular tempo médio por tipo de serviço (usando datas do próprio ItemServico)
        Map<String, List<Long>> temposPorServico = new HashMap<>();
        osFiltradas.forEach(os -> {
            os.getItensServico().forEach(item -> {
                // Ignorar serviços sem tempos registrados
                if (item.getDataInicioExecucao() == null || item.getDataFinalizacao() == null) {
                    return;
                }
                
                String servicoNome = item.getServicoNome();
                Duration duracao = Duration.between(item.getDataInicioExecucao(), item.getDataFinalizacao());
                long segundos = duracao.toSeconds();
                
                temposPorServico.computeIfAbsent(servicoNome, k -> new ArrayList<>()).add(segundos);
            });
        });
        
        // Calcular métricas por tipo de serviço
        Map<String, TempoServicoMetricas> tempoMedioPorServico = temposPorServico.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> {
                        List<Long> tempos = entry.getValue();
                        long media = tempos.stream().mapToLong(Long::longValue).sum() / tempos.size();
                        long min = tempos.stream().mapToLong(Long::longValue).min().orElse(0L);
                        long max = tempos.stream().mapToLong(Long::longValue).max().orElse(0L);
                        
                        return TempoServicoMetricas.builder()
                                .tempoMedioSegundos(media)
                                .tempoMedioFormatado(formatarDuracao(media))
                                .tempoMinimoSegundos(min)
                                .tempoMinimoFormatado(formatarDuracao(min))
                                .tempoMaximoSegundos(max)
                                .tempoMaximoFormatado(formatarDuracao(max))
                                .quantidade(tempos.size())
                                .build();
                    }
                ));
        
        log.info("Tempo médio calculado: média={} seg, mínimo={} seg, máximo={} seg, quantidade={}", 
                tempoMedio, tempoMinimo, tempoMaximo, osFiltradas.size());
        
        return TempoMedioExecucaoResponseDto.builder()
                .tempoMedioSegundos(tempoMedio)
                .tempoMedioFormatado(formatarDuracao(tempoMedio))
                .tempoMinimoSegundos(tempoMinimo)
                .tempoMinimoFormatado(formatarDuracao(tempoMinimo))
                .tempoMaximoSegundos(tempoMaximo)
                .tempoMaximoFormatado(formatarDuracao(tempoMaximo))
                .quantidadeOS(osFiltradas.size())
                .tempoMedioPorServico(tempoMedioPorServico)
                .build();
    }
    
    private String formatarDuracao(long segundos) {
        long horas = segundos / 3600;
        long minutos = (segundos % 3600) / 60;
        long segundosRestantes = segundos % 60;
        return String.format("%02d:%02d:%02d", horas, minutos, segundosRestantes);
    }
}
