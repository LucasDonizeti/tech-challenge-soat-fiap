package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.application.usecases.responses.MROResponse;
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
@Schema(description = "Resposta de MRO (Material, Reparo ou Óleo)")
public class MROResponseDto {
    
    @Schema(description = "UUID do MRO", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;
    
    @Schema(description = "Nome do MRO", example = "Óleo de Motor 5W30")
    private String nome;
    
    @Schema(description = "Descrição do MRO", example = "Óleo sintético para motor")
    private String descricao;
    
    @Schema(description = "Tipo do MRO (PECA, INSUMO)", allowableValues = {"PECA", "INSUMO"}, example = "INSUMO")
    private String tipo;
    
    @Schema(description = "Quantidade em estoque", example = "50")
    private Integer quantidadeEstoque;
    
    @Schema(description = "Preço unitário", example = "45.90")
    private BigDecimal precoUnitario;
    
    @Schema(description = "Indica se está ativo", example = "true")
    private Boolean ativo;
    
    @Schema(description = "Data de criação", example = "2024-01-15T10:30:00")
    private LocalDateTime criadoEm;
    
    @Schema(description = "Data da última atualização", example = "2024-01-20T14:45:00")
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
