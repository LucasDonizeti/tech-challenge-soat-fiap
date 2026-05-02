package com.techchallenge.oficina.os.domain.events;

import java.util.UUID;

/**
 * Evento de domínio emitido quando o orçamento está pronto para aprovação.
 */
public record OrcamentoProntoEvent(
    UUID ordemServicoId,
    String clienteNome,
    String clienteEmail,
    String veiculoMarca,
    String veiculoModelo,
    String veiculoPlaca
) {}
