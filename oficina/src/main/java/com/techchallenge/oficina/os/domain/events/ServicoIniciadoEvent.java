package com.techchallenge.oficina.os.domain.events;

import java.util.UUID;

/**
 * Evento de domínio emitido quando os serviços são iniciados.
 */
public record ServicoIniciadoEvent(
    UUID ordemServicoId,
    String clienteNome,
    String clienteEmail,
    String veiculoMarca,
    String veiculoModelo,
    String veiculoPlaca
) {}
