package com.techchallenge.oficina.administrativo.domain.model.valueobjects;

public enum StatusCliente {
    ATIVO("Ativo"),
    INATIVO("Inativo");

    private final String descricao;

    StatusCliente(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
