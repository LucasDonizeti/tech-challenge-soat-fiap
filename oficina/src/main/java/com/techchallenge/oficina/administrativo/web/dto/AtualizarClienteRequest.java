package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarClienteCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AtualizarClienteRequest {
    
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;
    
    public AtualizarClienteCommand toCommand() {
        return new AtualizarClienteCommand(nome, email);
    }
}
