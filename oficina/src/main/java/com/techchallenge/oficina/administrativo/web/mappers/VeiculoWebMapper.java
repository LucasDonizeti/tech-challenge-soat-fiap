package com.techchallenge.oficina.administrativo.web.mappers;

import com.techchallenge.oficina.administrativo.application.usecases.responses.VeiculoResponse;
import com.techchallenge.oficina.administrativo.web.dto.VeiculoResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VeiculoWebMapper {

    public VeiculoResponseDto toDto(VeiculoResponse response) {
        return VeiculoResponseDto.from(response);
    }

    public List<VeiculoResponseDto> toDtoList(List<VeiculoResponse> responses) {
        if (responses == null) {
            return List.of();
        }

        return responses.stream()
                .map(this::toDto)
                .toList();
    }

    public Page<VeiculoResponseDto> toDtoPage(Page<VeiculoResponse> responses) {
        if (responses == null) {
            return Page.empty();
        }

        return responses.map(this::toDto);
    }
}
