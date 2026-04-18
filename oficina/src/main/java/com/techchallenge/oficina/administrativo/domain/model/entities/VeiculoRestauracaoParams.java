package com.techchallenge.oficina.administrativo.domain.model.entities;

import com.techchallenge.oficina.administrativo.domain.model.valueobjects.Placa;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusVeiculo;
import java.time.LocalDateTime;
import java.util.UUID;

public record VeiculoRestauracaoParams(
    UUID id,
    Placa placa,
    String marca,
    String modelo,
    Integer ano,
    String cor,
    StatusVeiculo status,
    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm
) {}
