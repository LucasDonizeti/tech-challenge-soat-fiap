package com.techchallenge.oficina.administrativo.application.usecases.commands;

import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoMROException;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.TipoMRO;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CriarMROCommand {
    
    private final String nome;
    private final String codigo;
    private final String descricao;
    private final TipoMRO tipo;
    private final Integer quantidadeEstoque;
    private final BigDecimal precoUnitario;
    
    public CriarMROCommand(String nome, String codigo, String descricao, TipoMRO tipo, Integer quantidadeEstoque, BigDecimal precoUnitario) {
        validate(nome, codigo, tipo, quantidadeEstoque, precoUnitario);
        
        this.nome = nome;
        this.codigo = codigo;
        this.descricao = descricao;
        this.tipo = tipo;
        this.quantidadeEstoque = quantidadeEstoque != null ? quantidadeEstoque : 0;
        this.precoUnitario = precoUnitario;
    }
    
    private void validate(String nome, String codigo, TipoMRO tipo, Integer quantidadeEstoque, BigDecimal precoUnitario) {
        if (nome == null || nome.isBlank()) {
            throw new ValidacaoMROException("Nome é obrigatório");
        }
        if (codigo == null || codigo.isBlank()) {
            throw new ValidacaoMROException("Código é obrigatório");
        }
        if (tipo == null) {
            throw new ValidacaoMROException("Tipo é obrigatório");
        }
        if (quantidadeEstoque != null && quantidadeEstoque < 0) {
            throw new ValidacaoMROException("Quantidade de estoque não pode ser negativa");
        }
        if (precoUnitario == null || precoUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoMROException("Preço unitário deve ser maior que zero");
        }
    }
}
