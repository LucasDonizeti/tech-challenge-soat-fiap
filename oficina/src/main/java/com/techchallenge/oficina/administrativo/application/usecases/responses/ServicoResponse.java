package com.techchallenge.oficina.administrativo.application.usecases.responses;

import com.techchallenge.oficina.administrativo.domain.model.entities.Servico;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ServicoResponse {
    
    private final UUID id;
    private final String nome;
    private final String descricao;
    private final BigDecimal preco;
    private final Boolean ativo;
    private final LocalDateTime criadoEm;
    private final LocalDateTime atualizadoEm;
    
    public static ServicoResponse from(Servico servico) {
        if (servico == null) {
            return null;
        }
        
        return ServicoResponse.builder()
                .id(servico.getId())
                .nome(servico.getNome())
                .descricao(servico.getDescricao())
                .preco(servico.getPreco())
                .ativo(servico.getAtivo())
                .criadoEm(servico.getCriadoEm())
                .atualizadoEm(servico.getAtualizadoEm())
                .build();
    }
}
