package com.techchallenge.oficina.os.web.mappers;

import com.techchallenge.oficina.os.application.usecases.responses.OrdemServicoResponse;
import com.techchallenge.oficina.os.web.dto.OrdemServicoResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrdemServicoWebMapper {
    
    public OrdemServicoResponseDto toDto(OrdemServicoResponse response) {
        return OrdemServicoResponseDto.from(response);
    }
    
    public List<OrdemServicoResponseDto> toDtoList(List<OrdemServicoResponse> responses) {
        return responses.stream()
                .map(OrdemServicoResponseDto::from)
                .collect(Collectors.toList());
    }
    
    public Page<OrdemServicoResponseDto> toDtoPage(Page<OrdemServicoResponse> responses) {
        return responses.map(OrdemServicoResponseDto::from);
    }
}
