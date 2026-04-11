package com.techchallenge.oficina.administrativo.domain.model.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public final class Nome {
    private final String valor;

    private Nome(String valor) {
        this.valor = validar(valor);
    }

    public static Nome of(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }
        return new Nome(valor);
    }

    private String validar(String nome) {
        String nomeTrim = nome.trim();
        
        if (nomeTrim.length() < 3) {
            throw new IllegalArgumentException("Nome deve ter no mínimo 3 caracteres");
        }
        
        if (nomeTrim.length() > 100) {
            throw new IllegalArgumentException("Nome deve ter no máximo 100 caracteres");
        }
        
        // Verifica se contém apenas letras, espaços e caracteres permitidos
        if (!nomeTrim.matches("^[a-zA-ZÀ-ÿ\\s]+$")) {
            throw new IllegalArgumentException("Nome deve conter apenas letras e espaços");
        }
        
        return nomeTrim;
    }

    public String getSobrenome() {
        if (valor == null || valor.isBlank()) {
            return "";
        }
        
        String[] partes = valor.trim().split("\\s+");
        if (partes.length <= 1) {
            return "";
        }
        
        StringBuilder sobrenome = new StringBuilder();
        for (int i = 1; i < partes.length; i++) {
            if (i > 1) {
                sobrenome.append(" ");
            }
            sobrenome.append(partes[i]);
        }
        
        return sobrenome.toString();
    }

    public String getPrimeiroNome() {
        if (valor == null || valor.isBlank()) {
            return "";
        }
        
        String[] partes = valor.trim().split("\\s+");
        return partes[0];
    }

    @Override
    public String toString() {
        return valor;
    }
}
