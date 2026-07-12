package com.techchallenge.oficina.administrativo.web.presenters;

import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;
import com.techchallenge.oficina.administrativo.web.dto.MROResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class MROPresenter {
    
    /**
     * Prepara o view model para um único MRO.
     * Adiciona formatações específicas para visualização administrativa.
     */
    public MROResponseDto prepararViewModel(MROResponse response) {
        if (response == null) {
            return null;
        }
        
        // Usar o método estático existente para manter compatibilidade
        MROResponseDto dto = MROResponseDto.from(response);
        
        return dto;
    }
    
    /**
     * Prepara o view model para uma página de MROs.
     */
    public Page<MROResponseDto> prepararViewModelPage(Page<MROResponse> responses) {
        if (responses == null) {
            return Page.empty();
        }
        
        return responses.map(this::prepararViewModel);
    }
}
