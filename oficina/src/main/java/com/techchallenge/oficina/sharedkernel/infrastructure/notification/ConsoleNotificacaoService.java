package com.techchallenge.oficina.sharedkernel.infrastructure.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implementação de notificação via console log.
 * Usada para desenvolvimento e testes. Implementação futura pode enviar e-mail real.
 */
@Slf4j
@Component
public class ConsoleNotificacaoService implements NotificacaoService {
    
    @Override
    public void notificarCliente(String email, String nome, String assunto, String mensagem) throws NotificacaoException {
        if (email == null) {
            throw new NotificacaoException("Email não pode ser nulo");
        }
        if (nome == null) {
            throw new NotificacaoException("Nome não pode ser nulo");
        }
        if (assunto == null) {
            throw new NotificacaoException("Assunto não pode ser nulo");
        }
        if (mensagem == null) {
            throw new NotificacaoException("Mensagem não pode ser nula");
        }
        
        try {
            log.info("=== NOTIFICAÇÃO AO CLIENTE ===");
            log.info("Email: {}", email);
            log.info("Nome: {}", nome);
            log.info("Assunto: {}", assunto);
            log.info("Mensagem:");
            log.info("{}", mensagem);
            log.info("============================");
        } catch (Exception e) {
            throw new NotificacaoException("Erro ao enviar notificação via console", e);
        }
    }
}
