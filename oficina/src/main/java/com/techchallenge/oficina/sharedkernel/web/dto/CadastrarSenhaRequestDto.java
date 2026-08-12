package com.techchallenge.oficina.sharedkernel.web.dto;

import com.techchallenge.oficina.sharedkernel.application.usecases.commands.CadastrarSenhaClienteCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Requisição para cadastro ou redefinição de senha do cliente")
public class CadastrarSenhaRequestDto {

    @NotBlank(message = "Documento é obrigatório")
    @Schema(description = "CPF ou CNPJ do cliente (com ou sem formatação)", example = "529.982.247-25", required = true)
    private String cpf;

    @NotBlank(message = "Senha é obrigatória")
    @Schema(description = "Nova senha (mínimo 6 caracteres)", required = true)
    private String senha;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public CadastrarSenhaClienteCommand toCommand() {
        return new CadastrarSenhaClienteCommand(cpf, senha);
    }
}
