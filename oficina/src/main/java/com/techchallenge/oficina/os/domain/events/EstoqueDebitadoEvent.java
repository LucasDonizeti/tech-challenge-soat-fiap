package com.techchallenge.oficina.os.domain.events;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class EstoqueDebitadoEvent {
    
    private final UUID mroId;
    private final String nome;
    private final Integer quantidadeDebitada;
    private final Integer estoqueAtual;
    private final LocalDateTime ocorridoEm;
    
    public EstoqueDebitadoEvent(UUID mroId, String nome, Integer quantidadeDebitada, Integer estoqueAtual) {
        this.mroId = mroId;
        this.nome = nome;
        this.quantidadeDebitada = quantidadeDebitada;
        this.estoqueAtual = estoqueAtual;
        this.ocorridoEm = LocalDateTime.now();
    }
}
