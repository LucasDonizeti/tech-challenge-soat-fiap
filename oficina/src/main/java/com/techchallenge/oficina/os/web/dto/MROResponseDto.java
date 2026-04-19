package com.techchallenge.oficina.os.web.dto;

import com.techchallenge.oficina.os.application.usecases.responses.MROResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MROResponseDto {
    
    private UUID id;
    private String nome;
    private String descricao;
    private String tipo;
    private Integer quantidadeEstoque;
    private BigDecimal precoUnitario;
    private Boolean ativo;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    
    public static MROResponseDto from(MROResponse response) {
        if (response == null) {
            return null;
        }
        
        return MROResponseDto.builder()
                .id(response.getId())
                .nome(response.getNome())
                .descricao(response.getDescricao())
                .tipo(response.getTipo())
                .quantidadeEstoque(response.getQuantidadeEstoque())
                .precoUnitario(response.getPrecoUnitario())
                .ativo(response.getAtivo())
                .criadoEm(response.getCriadoEm())
                .atualizadoEm(response.getAtualizadoEm())
                .build();
    }
}
