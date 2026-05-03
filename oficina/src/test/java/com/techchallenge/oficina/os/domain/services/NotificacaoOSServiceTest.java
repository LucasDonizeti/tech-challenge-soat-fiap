package com.techchallenge.oficina.os.domain.services;

import com.techchallenge.oficina.administrativo.domain.model.aggregates.Cliente;
import com.techchallenge.oficina.administrativo.domain.model.entities.Veiculo;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.*;
import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.os.domain.model.entities.ItemServico;
import com.techchallenge.oficina.os.domain.model.valueobjects.StatusOS;
import com.techchallenge.oficina.sharedkernel.infrastructure.notification.NotificacaoException;
import com.techchallenge.oficina.sharedkernel.infrastructure.notification.NotificacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacaoOSServiceTest {
    
    @Mock
    private NotificacaoService notificacaoService;
    
    @InjectMocks
    private NotificacaoOSService notificacaoOSService;
    
    private OrdemServico ordemServico;
    private Cliente cliente;
    private Veiculo veiculo;
    
    @BeforeEach
    void setUp() {
        cliente = Cliente.criar(
            Nome.of("João Silva"),
            CPF.of("52998224725"),
            Email.of("joao@email.com")
        );
        
        veiculo = new Veiculo(Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Branco");
        veiculo.setCliente(cliente);
        
        ordemServico = OrdemServico.criar(cliente, veiculo);
        
        ItemServico itemServico = ItemServico.criarComDados(
            UUID.randomUUID(),
            "Troca de Óleo",
            "Troca de óleo sintético",
            new BigDecimal("100.00")
        );
        ordemServico.adicionarItemServico(itemServico);
    }
    
    @Test
    void notificarOrcamentoPronto_deveChamarNotificacaoService() throws NotificacaoException {
        notificacaoOSService.notificarOrcamentoPronto(ordemServico);
        
        verify(notificacaoService, times(1)).notificarCliente(
            eq("joao@email.com"),
            eq("João Silva"),
            anyString(),
            anyString()
        );
    }
    
    @Test
    void notificarOrcamentoPronto_deveTratarExcecaoSemLancar() throws NotificacaoException {
        doThrow(new NotificacaoException("Erro de teste"))
            .when(notificacaoService).notificarCliente(anyString(), anyString(), anyString(), anyString());
        
        assertDoesNotThrow(() -> notificacaoOSService.notificarOrcamentoPronto(ordemServico));
    }
    
    @Test
    void notificarServicoIniciado_deveChamarNotificacaoService() throws NotificacaoException {
        notificacaoOSService.notificarServicoIniciado(ordemServico);
        
        verify(notificacaoService, times(1)).notificarCliente(
            eq("joao@email.com"),
            eq("João Silva"),
            anyString(),
            anyString()
        );
    }
    
    @Test
    void notificarServicoIniciado_deveTratarExcecaoSemLancar() throws NotificacaoException {
        doThrow(new NotificacaoException("Erro de teste"))
            .when(notificacaoService).notificarCliente(anyString(), anyString(), anyString(), anyString());
        
        assertDoesNotThrow(() -> notificacaoOSService.notificarServicoIniciado(ordemServico));
    }
    
    @Test
    void notificarServicoFinalizado_deveChamarNotificacaoService() throws NotificacaoException {
        notificacaoOSService.notificarServicoFinalizado(ordemServico);
        
        verify(notificacaoService, times(1)).notificarCliente(
            eq("joao@email.com"),
            eq("João Silva"),
            anyString(),
            anyString()
        );
    }
    
    @Test
    void notificarServicoFinalizado_deveTratarExcecaoSemLancar() throws NotificacaoException {
        doThrow(new NotificacaoException("Erro de teste"))
            .when(notificacaoService).notificarCliente(anyString(), anyString(), anyString(), anyString());
        
        assertDoesNotThrow(() -> notificacaoOSService.notificarServicoFinalizado(ordemServico));
    }
    
    @Test
    void notificarVeiculoEntregue_deveChamarNotificacaoService() throws NotificacaoException {
        notificacaoOSService.notificarVeiculoEntregue(ordemServico);
        
        verify(notificacaoService, times(1)).notificarCliente(
            eq("joao@email.com"),
            eq("João Silva"),
            anyString(),
            anyString()
        );
    }
    
    @Test
    void notificarVeiculoEntregue_deveTratarExcecaoSemLancar() throws NotificacaoException {
        doThrow(new NotificacaoException("Erro de teste"))
            .when(notificacaoService).notificarCliente(anyString(), anyString(), anyString(), anyString());
        
        assertDoesNotThrow(() -> notificacaoOSService.notificarVeiculoEntregue(ordemServico));
    }
}
