package com.techchallenge.oficina.administrativo.web.mappers;

import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;
import com.techchallenge.oficina.administrativo.web.dto.ServicoResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ServicoWebMapper {
    
    public ServicoResponseDto toDto(ServicoResponse response) {
        return ServicoResponseDto.from(response);
    }
    
    public List<ServicoResponseDto> toDtoList(List<ServicoResponse> responses) {
        return responses.stream()
                .map(ServicoResponseDto::from)
                .collect(Collectors.toList());
    }
    
    public Page<ServicoResponseDto> toDtoPage(Page<ServicoResponse> responses) {
        return responses.map(ServicoResponseDto::from);
    }
}
