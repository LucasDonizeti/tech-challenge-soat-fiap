package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarClienteCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AtualizarClienteRequest {
    
    @NotBlank(message = "Nome é obrigatório")
    @Schema(description = "Nome completo do cliente", example = "João Silva", required = true)
    private String nome;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Schema(description = "Email válido do cliente", example = "joao.silva@exemplo.com", required = true)
    private String email;
    
    public AtualizarClienteCommand toCommand() {
        return new AtualizarClienteCommand(nome, email);
    }
}
