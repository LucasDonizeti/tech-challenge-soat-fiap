package com.techchallenge.oficina.administrativo.web.presenters;

import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;
import com.techchallenge.oficina.administrativo.web.dto.ServicoResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ServicoPresenter {
    
    /**
     * Prepara o view model para um único serviço.
     * Adiciona formatações específicas para visualização administrativa.
     */
    public ServicoResponseDto prepararViewModel(ServicoResponse response) {
        if (response == null) {
            return null;
        }
        
        // Usar o método estático existente para manter compatibilidade
        ServicoResponseDto dto = ServicoResponseDto.from(response);
        
        return dto;
    }
    
    /**
     * Prepara o view model para uma página de serviços.
     */
    public Page<ServicoResponseDto> prepararViewModelPage(Page<ServicoResponse> responses) {
        if (responses == null) {
            return Page.empty();
        }
        
        return responses.map(this::prepararViewModel);
    }
}
