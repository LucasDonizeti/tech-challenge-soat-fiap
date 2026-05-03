package com.techchallenge.oficina.sharedkernel.infrastructure.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ConsoleNotificacaoServiceTest {
    
    @InjectMocks
    private ConsoleNotificacaoService consoleNotificacaoService;
    
    @Test
    void notificarCliente_deveLancarExcecaoQuandoEmailNulo() {
        assertThrows(NotificacaoException.class, () -> {
            consoleNotificacaoService.notificarCliente(null, "João", "Assunto", "Mensagem");
        });
    }
    
    @Test
    void notificarCliente_deveLancarExcecaoQuandoNomeNulo() {
        assertThrows(NotificacaoException.class, () -> {
            consoleNotificacaoService.notificarCliente("joao@email.com", null, "Assunto", "Mensagem");
        });
    }
    
    @Test
    void notificarCliente_deveLancarExcecaoQuandoAssuntoNulo() {
        assertThrows(NotificacaoException.class, () -> {
            consoleNotificacaoService.notificarCliente("joao@email.com", "João", null, "Mensagem");
        });
    }
    
    @Test
    void notificarCliente_deveLancarExcecaoQuandoMensagemNula() {
        assertThrows(NotificacaoException.class, () -> {
            consoleNotificacaoService.notificarCliente("joao@email.com", "João", "Assunto", null);
        });
    }
    
    @Test
    void notificarCliente_deveExecutarComSucessoComParametrosValidos() {
        assertDoesNotThrow(() -> {
            consoleNotificacaoService.notificarCliente(
                "joao@email.com", 
                "João Silva", 
                "Assunto de Teste", 
                "Mensagem de teste"
            );
        });
    }
    
    @Test
    void notificarCliente_deveExecutarComSucessoComMensagemVazia() {
        assertDoesNotThrow(() -> {
            consoleNotificacaoService.notificarCliente(
                "joao@email.com", 
                "João Silva", 
                "Assunto de Teste", 
                ""
            );
        });
    }
}
