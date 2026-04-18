package com.techchallenge.oficina.administrativo.web.mappers;

import com.techchallenge.oficina.administrativo.application.usecases.responses.ClienteResponse;
import com.techchallenge.oficina.administrativo.web.dto.ClienteResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClienteWebMapper {
    
    public ClienteResponseDto toDto(ClienteResponse response) {
        return ClienteResponseDto.from(response);
    }
    
    public List<ClienteResponseDto> toDtoList(List<ClienteResponse> responses) {
        if (responses == null) {
            return List.of();
        }
        
        return responses.stream()
                .map(this::toDto)
                .toList();
    }
    
    public Page<ClienteResponseDto> toDtoPage(Page<ClienteResponse> responses) {
        if (responses == null) {
            return Page.empty();
        }
        
        List<ClienteResponseDto> dtos = toDtoList(responses.getContent());
        return new org.springframework.data.domain.PageImpl<>(
                dtos,
                responses.getPageable(),
                responses.getTotalElements()
        );
    }
}
