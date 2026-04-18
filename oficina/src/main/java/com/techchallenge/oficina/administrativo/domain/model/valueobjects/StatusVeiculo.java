package com.techchallenge.oficina.administrativo.domain.model.valueobjects;

public enum StatusVeiculo {
    ATIVO("Ativo"),
    INATIVO("Inativo");

    private final String descricao;

    StatusVeiculo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
