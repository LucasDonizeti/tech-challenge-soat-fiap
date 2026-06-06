package com.techchallenge.oficina.administrativo.application.usecases.commands;

import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoServicoException;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CriarServicoCommand {

    private final String nome;
    private final String descricao;
    private final BigDecimal preco;

    public CriarServicoCommand(String nome, String descricao, BigDecimal preco) {
        validate(nome, preco);

        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
    }

    private void validate(String nome, BigDecimal preco) {
        if (nome == null || nome.isBlank()) {
            throw new ValidacaoServicoException("Nome é obrigatório");
        }
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoServicoException("Preço deve ser maior que zero");
        }
    }
}