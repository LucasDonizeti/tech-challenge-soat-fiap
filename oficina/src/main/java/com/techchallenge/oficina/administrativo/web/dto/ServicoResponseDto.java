package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.application.usecases.responses.ServicoResponse;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Resposta de Serviço")
public class ServicoResponseDto {
    
    @Schema(description = "UUID do serviço", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;
    
    @Schema(description = "Nome do serviço", example = "Troca de Óleo")
    private String nome;
    
    @Schema(description = "Descrição do serviço", example = "Troca completa de óleo do motor")
    private String descricao;
    
    @Schema(description = "Preço do serviço", example = "150.00")
    private BigDecimal preco;
    
    @Schema(description = "Indica se está ativo", example = "true")
    private Boolean ativo;
    
    @Schema(description = "Data de criação", example = "2024-01-15T10:30:00")
    private LocalDateTime criadoEm;
    
    @Schema(description = "Data da última atualização", example = "2024-01-20T14:45:00")
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
