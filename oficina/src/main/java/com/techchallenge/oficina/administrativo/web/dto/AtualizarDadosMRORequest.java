package com.techchallenge.oficina.administrativo.web.dto;

import com.techchallenge.oficina.administrativo.application.usecases.commands.AtualizarDadosMROCommand;
import com.techchallenge.oficina.administrativo.domain.model.valueobjects.TipoMRO;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class AtualizarDadosMRORequest {
    
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    
    private String descricao;
    
    @NotBlank(message = "Tipo é obrigatório")
    private String tipo;
    
    public AtualizarDadosMROCommand toCommand(UUID id) {
        TipoMRO tipoEnum = TipoMRO.valueOf(tipo.toUpperCase());
        return new AtualizarDadosMROCommand(id, nome, descricao, tipoEnum);
    }
}
