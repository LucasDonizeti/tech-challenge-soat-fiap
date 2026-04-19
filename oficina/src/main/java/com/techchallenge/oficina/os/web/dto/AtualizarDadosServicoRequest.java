package com.techchallenge.oficina.os.web.dto;

import com.techchallenge.oficina.os.application.usecases.commands.AtualizarDadosServicoCommand;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AtualizarDadosServicoRequest {
    
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    
    private String descricao;
    
    public AtualizarDadosServicoCommand toCommand() {
        return new AtualizarDadosServicoCommand(nome, descricao);
    }
}
