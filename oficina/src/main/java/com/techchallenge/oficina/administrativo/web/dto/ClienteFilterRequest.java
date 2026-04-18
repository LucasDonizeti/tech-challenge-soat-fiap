package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.domain.model.valueobjects.StatusCliente;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ClienteFilterRequest {
    
    @Schema(description = "Nome do cliente para filtro", example = "João")
    private String nome;
    @Schema(description = "CPF do cliente para filtro (apenas números)", example = "12345678909")
    private String cpf;
    @Schema(description = "CNPJ do cliente para filtro (apenas números)", example = "12345678000190")
    private String cnpj;
    @Schema(description = "Email do cliente para filtro", example = "joao@exemplo.com")
    private String email;
    @Schema(description = "Status do cliente para filtro", example = "ATIVO")
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
