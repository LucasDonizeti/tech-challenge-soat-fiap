package com.techchallenge.oficina.os.domain.services;

import com.techchallenge.oficina.os.domain.model.aggregates.OrdemServico;
import com.techchallenge.oficina.sharedkernel.infrastructure.notification.NotificacaoException;
import com.techchallenge.oficina.sharedkernel.infrastructure.notification.NotificacaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Serviço de domínio para notificações relacionadas a Ordem de Serviço.
 * Responsável por montar mensagens específicas e chamar o serviço de notificação.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacaoOSService {
    
    private final NotificacaoService notificacaoService;
    
    private static final NumberFormat FORMATADOR_MOEDA = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    
    /**
     * Notifica o cliente que o orçamento está pronto para aprovação.
     */
    public void notificarOrcamentoPronto(OrdemServico os) {
        try {
            String email = obterEmailCliente(os);
            String nome = obterNomeCliente(os);
            String assunto = "Orçamento Pronto - Sua Oficina";
            String mensagem = montarMensagemOrcamentoPronto(os);
            
            notificacaoService.notificarCliente(email, nome, assunto, mensagem);
        } catch (NotificacaoException e) {
            log.error("Erro ao notificar cliente sobre orçamento pronto. OS ID: {}", os.getId(), e);
            // Não interrompe o fluxo
        }
    }
    
    /**
     * Notifica o cliente que os serviços foram iniciados.
     */
    public void notificarServicoIniciado(OrdemServico os) {
        try {
            String email = obterEmailCliente(os);
            String nome = obterNomeCliente(os);
            String assunto = "Serviços Iniciados - Sua Oficina";
            String mensagem = montarMensagemServicoIniciado(os);
            
            notificacaoService.notificarCliente(email, nome, assunto, mensagem);
        } catch (NotificacaoException e) {
            log.error("Erro ao notificar cliente sobre serviço iniciado. OS ID: {}", os.getId(), e);
            // Não interrompe o fluxo
        }
    }
    
    /**
     * Notifica o cliente que os serviços foram finalizados.
     */
    public void notificarServicoFinalizado(OrdemServico os) {
        try {
            String email = obterEmailCliente(os);
            String nome = obterNomeCliente(os);
            String assunto = "Serviço Finalizado - Sua Oficina";
            String mensagem = montarMensagemServicoFinalizado(os);
            
            notificacaoService.notificarCliente(email, nome, assunto, mensagem);
        } catch (NotificacaoException e) {
            log.error("Erro ao notificar cliente sobre serviço finalizado. OS ID: {}", os.getId(), e);
            // Não interrompe o fluxo
        }
    }
    
    /**
     * Notifica o cliente que o orçamento foi recusado e a OS foi cancelada.
     */
    public void notificarOrcamentoRecusado(OrdemServico os, String motivo) {
        try {
            String email = obterEmailCliente(os);
            String nome = obterNomeCliente(os);
            String assunto = "Orçamento Recusado - Sua Oficina";
            String mensagem = montarMensagemOrcamentoRecusado(os, motivo);

            notificacaoService.notificarCliente(email, nome, assunto, mensagem);
        } catch (NotificacaoException e) {
            log.error("Erro ao notificar cliente sobre orçamento recusado. OS ID: {}", os.getId(), e);
        }
    }

    /**
     * Notifica o cliente que o veículo foi entregue.
     */
    public void notificarVeiculoEntregue(OrdemServico os) {
        try {
            String email = obterEmailCliente(os);
            String nome = obterNomeCliente(os);
            String assunto = "Veículo Entregue - Sua Oficina";
            String mensagem = montarMensagemVeiculoEntregue(os);
            
            notificacaoService.notificarCliente(email, nome, assunto, mensagem);
        } catch (NotificacaoException e) {
            log.error("Erro ao notificar cliente sobre veículo entregue. OS ID: {}", os.getId(), e);
            // Não interrompe o fluxo
        }
    }
    
    // Métodos privados auxiliares
    
    private String obterEmailCliente(OrdemServico os) {
        if (os.getCliente() != null && os.getCliente().getEmail() != null) {
            return os.getCliente().getEmail().getEndereco();
        }
        return "email-nao-informado@oficina.com";
    }
    
    private String obterNomeCliente(OrdemServico os) {
        if (os.getCliente() != null && os.getCliente().getNome() != null) {
            return os.getCliente().getNome().getValor();
        }
        return "Cliente";
    }
    
    private String montarMensagemOrcamentoPronto(OrdemServico os) {
        StringBuilder sb = new StringBuilder();
        sb.append("Olá ").append(obterNomeCliente(os)).append(",\n\n");
        sb.append("O orçamento da sua ordem de serviço para o carro ");
        
        if (os.getVeiculo() != null) {
            sb.append(os.getVeiculo().getMarca())
              .append(" ")
              .append(os.getVeiculo().getModelo())
              .append(" (")
              .append(os.getVeiculo().getPlaca().getFormatada())
              .append(")");
        }
        
        sb.append(" está pronto.\n\n");
        sb.append("Serviços:\n");
        
        os.getItensServico().forEach(item -> {
            sb.append("- ").append(item.getServicoNome()).append(": ")
              .append(FORMATADOR_MOEDA.format(item.getValorServico()))
              .append("\n");
        });
        
        sb.append("\nTotal: ").append(FORMATADOR_MOEDA.format(os.calcularValorTotal())).append("\n\n");
        sb.append("Para aprovar, acesse: /api/cliente/os/").append(os.getId()).append("/aprovar");
        
        return sb.toString();
    }
    
    private String montarMensagemServicoIniciado(OrdemServico os) {
        StringBuilder sb = new StringBuilder();
        sb.append("Olá ").append(obterNomeCliente(os)).append(",\n\n");
        sb.append("Iniciamos os serviços do seu carro ");
        
        if (os.getVeiculo() != null) {
            sb.append(os.getVeiculo().getMarca())
              .append(" ")
              .append(os.getVeiculo().getModelo())
              .append(" (")
              .append(os.getVeiculo().getPlaca().getFormatada())
              .append(")");
        }
        
        sb.append(".\n");
        sb.append("Ordem de serviço: ").append(os.getId());
        
        return sb.toString();
    }
    
    private String montarMensagemServicoFinalizado(OrdemServico os) {
        StringBuilder sb = new StringBuilder();
        sb.append("Olá ").append(obterNomeCliente(os)).append(",\n\n");
        sb.append("O serviço do seu carro ");
        
        if (os.getVeiculo() != null) {
            sb.append(os.getVeiculo().getMarca())
              .append(" ")
              .append(os.getVeiculo().getModelo())
              .append(" (")
              .append(os.getVeiculo().getPlaca().getFormatada())
              .append(")");
        }
        
        sb.append(" foi finalizado.\n");
        sb.append("Ordem de serviço: ").append(os.getId()).append("\n");
        sb.append("Valor total: ").append(FORMATADOR_MOEDA.format(os.calcularValorTotal()));
        
        return sb.toString();
    }
    
    private String montarMensagemVeiculoEntregue(OrdemServico os) {
        StringBuilder sb = new StringBuilder();
        sb.append("Olá ").append(obterNomeCliente(os)).append(",\n\n");
        sb.append("Seu carro ");

        if (os.getVeiculo() != null) {
            sb.append(os.getVeiculo().getMarca())
              .append(" ")
              .append(os.getVeiculo().getModelo())
              .append(" (")
              .append(os.getVeiculo().getPlaca().getFormatada())
              .append(")");
        }

        sb.append(" foi entregue.\n");
        sb.append("Ordem de serviço: ").append(os.getId());

        return sb.toString();
    }

    private String montarMensagemOrcamentoRecusado(OrdemServico os, String motivo) {
        StringBuilder sb = new StringBuilder();
        sb.append("Olá ").append(obterNomeCliente(os)).append(",\n\n");
        sb.append("Recebemos a sua recusa do orçamento para o carro ");

        if (os.getVeiculo() != null) {
            sb.append(os.getVeiculo().getMarca())
              .append(" ")
              .append(os.getVeiculo().getModelo())
              .append(" (")
              .append(os.getVeiculo().getPlaca().getFormatada())
              .append(")");
        }

        sb.append(".\n\n");
        sb.append("Motivo informado: ").append(motivo).append("\n\n");
        sb.append("A ordem de serviço ").append(os.getId()).append(" foi cancelada.\n");
        sb.append("Entre em contato conosco caso queira discutir alternativas.");

        return sb.toString();
    }
}
