package com.techchallenge.oficina.os.web.presenters;

import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.web.dto.OrdemServicoResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ClienteOSPresenter {
    
    /**
     * Prepara o view model para uma única ordem de serviço no contexto de cliente.
     * Adiciona formatações específicas para visualização do cliente.
     */
    public OrdemServicoResponseDto prepararViewModel(OrdemServicoResponse response) {
        if (response == null) {
            return null;
        }
        
        // Usar o método estático existente para manter compatibilidade
        OrdemServicoResponseDto dto = OrdemServicoResponseDto.from(response);
        
        // Adicionar formatações específicas de apresentação para cliente
        if (response.getStatus() != null) {
            dto.setStatus(formatarStatusParaCliente(response.getStatus()));
        }
        
        return dto;
    }
    
    /**
     * Prepara o view model para uma página de ordens de serviço no contexto de cliente.
     */
    public Page<OrdemServicoResponseDto> prepararViewModelPage(Page<OrdemServicoResponse> responses) {
        if (responses == null) {
            return Page.empty();
        }
        
        return responses.map(this::prepararViewModel);
    }
    
    /**
     * Formata o status para ser mais amigável ao cliente.
     * Converte underscores por espaços e adiciona formatação legível.
     */
    private String formatarStatusParaCliente(String status) {
        if (status == null) {
            return null;
        }
        
        return status
                .replace("_", " ")
                .toLowerCase()
                .replace("em ", "em ")
                .replace("aguardando", "Aguardando")
                .replace("recebida", "Recebida")
                .replace("diagnostico", "Diagnóstico")
                .replace("execucao", "Execução")
                .replace("finalizada", "Finalizada")
                .replace("entregue", "Entregue");
    }
}
