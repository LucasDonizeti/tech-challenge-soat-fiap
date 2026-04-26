package com.techchallenge.oficina.administrativo.domain.events;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class MROCriadoEvent {
    
    private final UUID mroId;
    private final String nome;
    private final String tipo;
    private final Integer quantidadeEstoque;
    private final BigDecimal precoUnitario;
    private final LocalDateTime ocorridoEm;
    
    public MROCriadoEvent(UUID mroId, String nome, String tipo, Integer quantidadeEstoque, BigDecimal precoUnitario) {
        this.mroId = mroId;
        this.nome = nome;
        this.tipo = tipo;
        this.quantidadeEstoque = quantidadeEstoque;
        this.precoUnitario = precoUnitario;
        this.ocorridoEm = LocalDateTime.now();
    }
}
