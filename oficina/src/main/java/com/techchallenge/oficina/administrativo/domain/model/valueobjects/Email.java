package com.techchallenge.oficina.administrativo.domain.model.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public final class Email {
    private final String endereco;

    private Email(String endereco) {
        this.endereco = validar(endereco);
    }

    public static Email of(String endereco) {
        if (endereco == null || endereco.isBlank()) {
            throw new IllegalArgumentException("Email não pode ser nulo ou vazio");
        }
        return new Email(endereco);
    }

    private String validar(String email) {
        String emailTrim = email.trim().toLowerCase();
        
        if (emailTrim.length() > 100) {
            throw new IllegalArgumentException("Email deve ter no máximo 100 caracteres");
        }
        
        // Validação básica de formato de email
        if (!isValidEmail(emailTrim)) {
            throw new IllegalArgumentException("Email inválido");
        }
        
        return emailTrim;
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        
        // Expressão regular para validação de email
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    public String getUsuario() {
        if (endereco == null || !endereco.contains("@")) {
            return "";
        }
        return endereco.substring(0, endereco.indexOf("@"));
    }

    public String getDominio() {
        if (endereco == null || !endereco.contains("@")) {
            return "";
        }
        return endereco.substring(endereco.indexOf("@") + 1);
    }

    @Override
    public String toString() {
        return endereco;
    }
}
