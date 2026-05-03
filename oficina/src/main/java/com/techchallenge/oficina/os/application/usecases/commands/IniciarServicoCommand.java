package com.techchallenge.oficina.os.application.usecases.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IniciarServicoCommand {
    
    private UUID ordemServicoId;
    private UUID itemServicoId;
}
