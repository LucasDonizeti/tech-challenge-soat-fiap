package com.techchallenge.oficina.administrativo.domain.model.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public final class Placa {
    private final String valor;

    private Placa(String valor) {
        this.valor = validar(valor);
    }

    public static Placa of(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Placa não pode ser nula ou vazia");
        }
        return new Placa(valor);
    }

    private String validar(String placa) {
        String placaLimpa = placa.replaceAll("[^A-Z0-9]", "").toUpperCase();
        
        if (!isValidPlaca(placaLimpa)) {
            throw new IllegalArgumentException("Placa inválida: " + placa);
        }
        
        return placaLimpa;
    }

    private boolean isValidPlaca(String placa) {
        if (placa == null || placa.length() != 7) {
            return false;
        }
        
        // Validação do formato Mercosul (LLLNLNN)
        if (placa.matches("[A-Z]{3}[0-9][A-Z][0-9]{2}")) {
            return true;
        }
        
        // Validação do formato antigo (LLLNNNN)
        if (placa.matches("[A-Z]{3}[0-9]{4}")) {
            return true;
        }
        
        return false;
    }

    public String getFormatada() {
        if (valor == null || valor.length() != 7) {
            return valor;
        }
        
        // Formato Mercosul (LLLNLNN -> LLL-NLNN)
        if (valor.matches("[A-Z]{3}[0-9][A-Z][0-9]{2}")) {
            return valor.substring(0, 3) + "-" + valor.substring(3, 5) + valor.substring(5);
        }
        
        // Formato antigo (LLLNNNN -> LLL-NNNN)
        if (valor.matches("[A-Z]{3}[0-9]{4}")) {
            return valor.substring(0, 3) + "-" + valor.substring(3);
        }
        
        return valor;
    }

    public boolean isMercosul() {
        return valor != null && valor.matches("[A-Z]{3}[0-9][A-Z][0-9]{2}");
    }

    public boolean isFormatoAntigo() {
        return valor != null && valor.matches("[A-Z]{3}[0-9]{4}");
    }

    @Override
    public String toString() {
        return getFormatada();
    }
}
