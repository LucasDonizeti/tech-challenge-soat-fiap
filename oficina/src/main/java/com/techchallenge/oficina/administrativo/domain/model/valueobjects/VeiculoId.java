package com.techchallenge.oficina.administrativo.domain.model.valueobjects;

import com.techchallenge.oficina.administrativo.domain.exceptions.ValidacaoValorException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode
public final class VeiculoId {
    private final UUID value;

    private VeiculoId(UUID value) {
        if (value == null) {
            throw new ValidacaoValorException("VeiculoId não pode ser nulo");
        }
        this.value = value;
    }

    public static VeiculoId of(UUID value) {
        return new VeiculoId(value);
    }

    public static VeiculoId generate() {
        return new VeiculoId(UUID.randomUUID());
    }

    public static VeiculoId fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new ValidacaoValorException("VeiculoId não pode ser nulo ou vazio");
        }
        try {
            return new VeiculoId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new ValidacaoValorException("VeiculoId inválido: " + value);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
