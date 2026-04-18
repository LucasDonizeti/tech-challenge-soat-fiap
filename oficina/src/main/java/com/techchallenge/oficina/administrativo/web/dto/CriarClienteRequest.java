package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.application.usecases.commands.CriarClienteCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CriarClienteRequest {
    
    @NotBlank(message = "Nome é obrigatório")
    @Schema(description = "Nome completo do cliente", example = "João Silva", required = true)
    private String nome;
    
    @Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 dígitos")
    @Schema(description = "CPF do cliente (apenas números)", example = "12345678909")
    private String cpf;
    
    @Pattern(regexp = "\\d{14}", message = "CNPJ deve ter 14 dígitos")
    @Schema(description = "CNPJ do cliente (apenas números)", example = "12345678000190")
    private String cnpj;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Schema(description = "Email válido do cliente", example = "joao.silva@exemplo.com", required = true)
    private String email;
    
    public CriarClienteCommand toCommand() {
        return new CriarClienteCommand(nome, cpf, cnpj, email);
    }
}
