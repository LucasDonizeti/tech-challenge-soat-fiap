package com.techchallenge.oficina.os.domain.events;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class EstoqueRepostoEvent {
    
    private final UUID mroId;
    private final String nome;
    private final Integer quantidadeReposta;
    private final Integer estoqueAtual;
    private final LocalDateTime ocorridoEm;
    
    public EstoqueRepostoEvent(UUID mroId, String nome, Integer quantidadeReposta, Integer estoqueAtual) {
        this.mroId = mroId;
        this.nome = nome;
        this.quantidadeReposta = quantidadeReposta;
        this.estoqueAtual = estoqueAtual;
        this.ocorridoEm = LocalDateTime.now();
    }
}
