package com.techchallenge.oficina.os.web.presenters;

import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.web.dto.OrdemServicoResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class OrdemServicoPresenter {
    
    /**
     * Prepara o view model para uma única ordem de serviço.
     * Adiciona formatações específicas para visualização administrativa.
     */
    public OrdemServicoResponseDto prepararViewModel(OrdemServicoResponse response) {
        if (response == null) {
            return null;
        }
        
        // Usar o método estático existente para manter compatibilidade
        OrdemServicoResponseDto dto = OrdemServicoResponseDto.from(response);
        
        // Adicionar formatações específicas de apresentação
        if (response.getStatus() != null) {
            dto.setStatus(formatarStatus(response.getStatus()));
        }
        
        return dto;
    }
    
    /**
     * Prepara o view model para uma página de ordens de serviço.
     */
    public Page<OrdemServicoResponseDto> prepararViewModelPage(Page<OrdemServicoResponse> responses) {
        if (responses == null) {
            return Page.empty();
        }
        
        return responses.map(this::prepararViewModel);
    }
    
    /**
     * Formata o status para ser mais legível.
     * Converte underscores por espaços.
     */
    private String formatarStatus(String status) {
        if (status == null) {
            return null;
        }
        
        return status.replace("_", " ");
    }
}
