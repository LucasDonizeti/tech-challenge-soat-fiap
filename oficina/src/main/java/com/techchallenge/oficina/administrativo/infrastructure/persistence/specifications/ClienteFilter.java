package com.techchallenge.oficina.administrativo.infrastructure.persistence.specifications;

import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusCliente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteFilter {
    
    private String nome;
    private String cpf;
    private String cnpj;
    private String email;
    private StatusCliente status;
    
    public static ClienteFilter from(String nome, String cpf, String cnpj, String email, StatusCliente status) {
        return ClienteFilter.builder()
                .nome(nome)
                .cpf(cpf)
                .cnpj(cnpj)
                .email(email)
                .status(status)
                .build();
    }
}
