package com.techchallenge.oficina.administrativo.web.presenters;

import com.techchallenge.oficina.administrativo.application.usecases.responses.VeiculoResponse;
import com.techchallenge.oficina.administrativo.web.dto.VeiculoResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VeiculoPresenter {
    
    /**
     * Prepara o view model para um único veículo.
     * Adiciona formatações específicas para visualização administrativa.
     */
    public VeiculoResponseDto prepararViewModel(VeiculoResponse response) {
        if (response == null) {
            return null;
        }
        
        // Usar o método estático existente para manter compatibilidade
        VeiculoResponseDto dto = VeiculoResponseDto.from(response);
        
        return dto;
    }
    
    /**
     * Prepara o view model para uma lista de veículos.
     */
    public List<VeiculoResponseDto> prepararViewModelList(List<VeiculoResponse> responses) {
        if (responses == null) {
            return List.of();
        }
        
        return responses.stream()
                .map(this::prepararViewModel)
                .collect(Collectors.toList());
    }
    
    /**
     * Prepara o view model para uma página de veículos.
     */
    public Page<VeiculoResponseDto> prepararViewModelPage(Page<VeiculoResponse> responses) {
        if (responses == null) {
            return Page.empty();
        }
        
        return responses.map(this::prepararViewModel);
    }
}
