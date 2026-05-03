package com.techchallenge.oficina.sharedkernel.infrastructure.notification;

/**
 * Exceção lançada quando ocorre erro no envio de notificação.
 */
public class NotificacaoException extends RuntimeException {
    
    public NotificacaoException(String message) {
        super(message);
    }
    
    public NotificacaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
