package com.techchallenge.oficina.administrativo.web.presenters;

import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.administrativo.web.dto.ClienteResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ClientePresenter {
    
    /**
     * Prepara o view model para um único cliente.
     * Adiciona formatações específicas para visualização administrativa.
     */
    public ClienteResponseDto prepararViewModel(ClienteResponse response) {
        if (response == null) {
            return null;
        }
        
        // Usar o método estático existente para manter compatibilidade
        ClienteResponseDto dto = ClienteResponseDto.from(response);
        
        // O status já é um enum no DTO, não precisa de formatação adicional
        
        return dto;
    }
    
    /**
     * Prepara o view model para uma página de clientes.
     */
    public Page<ClienteResponseDto> prepararViewModelPage(Page<ClienteResponse> responses) {
        if (responses == null) {
            return Page.empty();
        }
        
        return responses.map(this::prepararViewModel);
    }
    
}
