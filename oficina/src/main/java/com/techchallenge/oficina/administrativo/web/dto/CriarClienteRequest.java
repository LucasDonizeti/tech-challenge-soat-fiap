package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.application.usecases.commands.CriarClienteCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CriarClienteRequest {
    
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    
    @Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 dígitos")
    private String cpf;
    
    @Pattern(regexp = "\\d{14}", message = "CNPJ deve ter 14 dígitos")
    private String cnpj;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;
    
    public CriarClienteCommand toCommand() {
        return new CriarClienteCommand(nome, cpf, cnpj, email);
    }
}
