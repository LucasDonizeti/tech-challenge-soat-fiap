package com.techchallenge.oficina.os.web.dto;

import com.techchallenge.oficina.os.application.usecases.responses.ServicoResponse;
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
public class ServicoResponseDto {
    
    private UUID id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Boolean ativo;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    
    public static ServicoResponseDto from(ServicoResponse response) {
        if (response == null) {
            return null;
        }
        
        return ServicoResponseDto.builder()
                .id(response.getId())
                .nome(response.getNome())
                .descricao(response.getDescricao())
                .preco(response.getPreco())
                .ativo(response.getAtivo())
                .criadoEm(response.getCriadoEm())
                .atualizadoEm(response.getAtualizadoEm())
                .build();
    }
}
