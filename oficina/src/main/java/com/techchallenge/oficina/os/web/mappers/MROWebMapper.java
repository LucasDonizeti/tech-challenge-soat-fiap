package com.techchallenge.oficina.os.web.mappers;

import com.techchallenge.oficina.os.application.usecases.responses.MROResponse;
import com.techchallenge.oficina.os.web.dto.MROResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MROWebMapper {
    
    public MROResponseDto toDto(MROResponse response) {
        return MROResponseDto.from(response);
    }
    
    public List<MROResponseDto> toDtoList(List<MROResponse> responses) {
        return responses.stream()
                .map(MROResponseDto::from)
                .collect(Collectors.toList());
    }
    
    public Page<MROResponseDto> toDtoPage(Page<MROResponse> responses) {
        return responses.map(MROResponseDto::from);
    }
}
