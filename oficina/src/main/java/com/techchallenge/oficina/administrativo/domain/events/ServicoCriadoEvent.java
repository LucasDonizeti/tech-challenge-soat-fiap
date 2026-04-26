package com.techchallenge.oficina.administrativo.domain.events;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ServicoCriadoEvent {
    
    private final UUID servicoId;
    private final String nome;
    private final BigDecimal preco;
    private final LocalDateTime ocorridoEm;
    
    public ServicoCriadoEvent(UUID servicoId, String nome, BigDecimal preco) {
        this.servicoId = servicoId;
        this.nome = nome;
        this.preco = preco;
        this.ocorridoEm = LocalDateTime.now();
    }
}
