package com.techchallenge.oficina.administrativo.domain.model.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public final class Placa {
    private static final String FORMATO_MERCOSUL_REGEX = "[A-Z]{3}\\d[A-Z]\\d{2}";
    private static final String FORMATO_ANTIGO_REGEX = "[A-Z]{3}\\d{4}";
    
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
        String placaLimpa = placa.replaceAll("[^A-Z\\d]", "").toUpperCase();
        
        if (!isValidPlaca(placaLimpa)) {
            throw new IllegalArgumentException("Placa inválida: " + placa);
        }
        
        return placaLimpa;
    }

    private boolean isValidPlaca(String placa) {
        if (placa == null || placa.length() != 7) {
            return false;
        }
        
        return placa.matches(FORMATO_MERCOSUL_REGEX) || placa.matches(FORMATO_ANTIGO_REGEX);
    }

    public String getFormatada() {
        if (valor == null || valor.length() != 7) {
            return valor;
        }
        
        // Formato Mercosul (LLLNLNN -> LLL-NLNN)
        if (valor.matches(FORMATO_MERCOSUL_REGEX)) {
            return valor.substring(0, 3) + "-" + valor.substring(3, 5) + valor.substring(5);
        }
        
        // Formato antigo (LLLNNNN -> LLL-NNNN)
        if (valor.matches(FORMATO_ANTIGO_REGEX)) {
            return valor.substring(0, 3) + "-" + valor.substring(3);
        }
        
        return valor;
    }

    public boolean isMercosul() {
        return valor != null && valor.matches(FORMATO_MERCOSUL_REGEX);
    }

    public boolean isFormatoAntigo() {
        return valor != null && valor.matches(FORMATO_ANTIGO_REGEX);
    }

    @Override
    public String toString() {
        return getFormatada();
    }
}
