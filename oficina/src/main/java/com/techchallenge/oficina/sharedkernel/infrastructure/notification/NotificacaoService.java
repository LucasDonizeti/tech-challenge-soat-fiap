package com.techchallenge.oficina.sharedkernel.infrastructure.notification;

/**
 * Interface para serviço de notificação ao cliente.
 * Permite diferentes implementações (email, SMS, console, etc.)
 */
public interface NotificacaoService {
    
    /**
     * Envia notificação ao cliente.
     * 
     * @param email Email do cliente
     * @param nome Nome do cliente
     * @param assunto Assunto da notificação
     * @param mensagem Corpo da mensagem
     * @throws NotificacaoException Caso ocorra erro no envio da notificação
     */
    void notificarCliente(String email, String nome, String assunto, String mensagem) throws NotificacaoException;
}
