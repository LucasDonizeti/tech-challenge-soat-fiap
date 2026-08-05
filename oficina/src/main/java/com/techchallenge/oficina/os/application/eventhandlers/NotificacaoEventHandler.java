package com.techchallenge.oficina.os.application.eventhandlers;

import com.techchallenge.oficina.os.domain.events.*;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.repositories.OrdemServicoRepository;
import com.techchallenge.oficina.os.domain.services.NotificacaoOSService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Event handler para processar notificações ao cliente.
 * Escuta eventos de domínio e aciona o serviço de notificação de forma assíncrona.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificacaoEventHandler {
    
    private final NotificacaoOSService notificacaoOSService;
    private final OrdemServicoRepository ordemServicoRepository;
    
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrcamentoPronto(OrcamentoProntoEvent event) {
        log.info("Processando evento OrcamentoPronto para OS ID: {}", event.ordemServicoId());
        try {
            ordemServicoRepository.findById(event.ordemServicoId())
                    .ifPresent(notificacaoOSService::notificarOrcamentoPronto);
        } catch (Exception e) {
            log.error("Erro ao processar evento OrcamentoPronto", e);
        }
    }
    
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleServicoIniciado(ServicoIniciadoEvent event) {
        log.info("Processando evento ServicoIniciado para OS ID: {}", event.ordemServicoId());
        try {
            ordemServicoRepository.findById(event.ordemServicoId())
                    .ifPresent(notificacaoOSService::notificarServicoIniciado);
        } catch (Exception e) {
            log.error("Erro ao processar evento ServicoIniciado", e);
        }
    }
    
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleServicoFinalizado(ServicoFinalizadoEvent event) {
        log.info("Processando evento ServicoFinalizado para OS ID: {}", event.ordemServicoId());
        try {
            ordemServicoRepository.findById(event.ordemServicoId())
                    .ifPresent(notificacaoOSService::notificarServicoFinalizado);
        } catch (Exception e) {
            log.error("Erro ao processar evento ServicoFinalizado", e);
        }
    }
    
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleVeiculoEntregue(VeiculoEntregueEvent event) {
        log.info("Processando evento VeiculoEntregue para OS ID: {}", event.ordemServicoId());
        try {
            ordemServicoRepository.findById(event.ordemServicoId())
                    .ifPresent(notificacaoOSService::notificarVeiculoEntregue);
        } catch (Exception e) {
            log.error("Erro ao processar evento VeiculoEntregue", e);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrcamentoRecusado(OrcamentoRecusadoEvent event) {
        log.info("Processando evento OrcamentoRecusado para OS ID: {}", event.ordemServicoId());
        try {
            ordemServicoRepository.findById(event.ordemServicoId())
                    .ifPresent(os -> notificacaoOSService.notificarOrcamentoRecusado(os, event.motivoRecusa()));
        } catch (Exception e) {
            log.error("Erro ao processar evento OrcamentoRecusado", e);
        }
    }
}
