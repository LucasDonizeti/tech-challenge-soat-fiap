package com.techchallenge.oficina.os.application.usecases.responses;

import com.techchallenge.oficina.os.domain.model.entities.MRO;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class MROResponse {
    
    private final UUID id;
    private final String nome;
    private final String descricao;
    private final String tipo;
    private final Integer quantidadeEstoque;
    private final BigDecimal precoUnitario;
    private final Boolean ativo;
    private final LocalDateTime criadoEm;
    private final LocalDateTime atualizadoEm;
    
    public static MROResponse from(MRO mro) {
        if (mro == null) {
            return null;
        }
        
        return MROResponse.builder()
                .id(mro.getId())
                .nome(mro.getNome())
                .descricao(mro.getDescricao())
                .tipo(mro.getTipo() != null ? mro.getTipo().name() : null)
                .quantidadeEstoque(mro.getQuantidadeEstoque())
                .precoUnitario(mro.getPrecoUnitario())
                .ativo(mro.getAtivo())
                .criadoEm(mro.getCriadoEm())
                .atualizadoEm(mro.getAtualizadoEm())
                .build();
    }
}
