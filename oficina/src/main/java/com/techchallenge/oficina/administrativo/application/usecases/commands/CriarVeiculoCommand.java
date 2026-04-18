package com.techchallenge.oficina.administrativo.application.usecases.commands;

import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoVeiculoException;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CriarVeiculoCommand {

    private final Placa placa;
    private final String marca;
    private final String modelo;
    private final Integer ano;
    private final String cor;
    private final UUID clienteId;

    public CriarVeiculoCommand(String placa, String marca, String modelo, Integer ano, String cor, UUID clienteId) {
        validate(placa, marca, modelo, ano, clienteId);

        this.placa = Placa.of(placa);
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.cor = cor;
        this.clienteId = clienteId;
    }

    private void validate(String placa, String marca, String modelo, Integer ano, UUID clienteId) {
        if (placa == null || placa.isBlank()) {
            throw new ValidacaoVeiculoException("Placa é obrigatória");
        }
        if (marca == null || marca.isBlank()) {
            throw new ValidacaoVeiculoException("Marca é obrigatória");
        }
        if (modelo == null || modelo.isBlank()) {
            throw new ValidacaoVeiculoException("Modelo é obrigatório");
        }
        if (ano == null) {
            throw new ValidacaoVeiculoException("Ano é obrigatório");
        }
        if (clienteId == null) {
            throw new ValidacaoVeiculoException("Cliente ID é obrigatório");
        }
    }
}
