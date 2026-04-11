package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusCliente;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ClienteFilterRequest {
    
    private String nome;
    private String cpf;
    private String cnpj;
    private String email;
    private StatusCliente status;
    
    public boolean hasNome() {
        return nome != null && !nome.isBlank();
    }
    
    public boolean hasCpf() {
        return cpf != null && !cpf.isBlank();
    }
    
    public boolean hasCnpj() {
        return cnpj != null && !cnpj.isBlank();
    }
    
    public boolean hasEmail() {
        return email != null && !email.isBlank();
    }
    
    public boolean hasStatus() {
        return status != null;
    }
    
    public boolean hasAnyFilter() {
        return hasNome() || hasCpf() || hasCnpj() || hasEmail() || hasStatus();
    }
}
