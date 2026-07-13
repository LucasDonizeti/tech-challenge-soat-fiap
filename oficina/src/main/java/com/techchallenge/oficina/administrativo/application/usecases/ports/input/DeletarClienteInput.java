package com.techchallenge.oficina.administrativo.application.usecases.ports.input;

import java.util.UUID;

public interface DeletarClienteInput {
   void execute(UUID clienteId);
}
