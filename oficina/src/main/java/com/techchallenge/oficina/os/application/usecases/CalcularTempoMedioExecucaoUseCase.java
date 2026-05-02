package com.techchallenge.oficina.os.application.usecases;

import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
import com.techchallenge.oficina.os.domain.repositories.OrdemServicoRepository;
import com.techchallenge.oficina.os.web.dto.TempoMedioExecucaoResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalcularTempoMedioExecucaoUseCase {
    
    private final OrdemServicoRepository ordemServicoRepository;
    
    public TempoMedioExecucaoResponseDto execute(UUID servicoId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        log.info("Calculando tempo médio de execução: servicoId={}, dataInicio={}, dataFim={}", 
                servicoId, dataInicio, dataFim);
        
        // Buscar todas as OSs em status FINALIZADA ou ENTREGUE
        // Nota: Como o repository não tem método específico, precisamos buscar todas e filtrar
        var todasOS = ordemServicoRepository.findAll(org.springframework.data.domain.Pageable.unpaged());
        
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
                    .tempoMedioMinutos(0L)
                    .tempoMedioFormatado("00:00:00")
                    .tempoMinimoMinutos(0L)
                    .tempoMinimoFormatado("00:00:00")
                    .tempoMaximoMinutos(0L)
                    .tempoMaximoFormatado("00:00:00")
                    .quantidadeOS(0)
                    .tempoMedioPorServico(new HashMap<>())
                    .build();
        }
        
        // Calcular tempos de execução
        var temposMinutos = osFiltradas.stream()
                .map(os -> Duration.between(os.getDataInicioExecucao(), os.getDataFinalizacao()).toMinutes())
                .toList();
        
        long tempoMedio = temposMinutos.stream()
                .mapToLong(Long::longValue)
                .sum() / temposMinutos.size();
        
        long tempoMinimo = temposMinutos.stream()
                .mapToLong(Long::longValue)
                .min()
                .orElse(0L);
        
        long tempoMaximo = temposMinutos.stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);
        
        // Calcular tempo médio por tipo de serviço (agrupando por ItemServico)
        Map<String, Long> tempoMedioPorServico = new HashMap<>();
        osFiltradas.forEach(os -> {
            os.getItensServico().forEach(item -> {
                String servicoNome = item.getServicoNome();
                Duration duracao = Duration.between(os.getDataInicioExecucao(), os.getDataFinalizacao());
                long minutos = duracao.toMinutes();
                
                tempoMedioPorServico.merge(servicoNome, minutos, (existing, novo) -> (existing + novo) / 2);
            });
        });
        
        log.info("Tempo médio calculado: média={} min, mínimo={} min, máximo={} min, quantidade={}", 
                tempoMedio, tempoMinimo, tempoMaximo, osFiltradas.size());
        
        return TempoMedioExecucaoResponseDto.builder()
                .tempoMedioMinutos(tempoMedio)
                .tempoMedioFormatado(formatarDuracao(tempoMedio))
                .tempoMinimoMinutos(tempoMinimo)
                .tempoMinimoFormatado(formatarDuracao(tempoMinimo))
                .tempoMaximoMinutos(tempoMaximo)
                .tempoMaximoFormatado(formatarDuracao(tempoMaximo))
                .quantidadeOS(osFiltradas.size())
                .tempoMedioPorServico(tempoMedioPorServico)
                .build();
    }
    
    private String formatarDuracao(long minutos) {
        long horas = minutos / 60;
        long minutosRestantes = minutos % 60;
        return String.format("%02d:%02d:00", horas, minutosRestantes);
    }
}
