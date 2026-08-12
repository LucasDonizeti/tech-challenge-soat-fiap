package com.techchallenge.oficina.os.domain.events;

import java.util.UUID;

/**
 * Evento de domínio emitido quando o cliente recusa o orçamento da OS.
 * Dispara notificação e transiciona o status para CANCELADA.
 */
public record OrcamentoRecusadoEvent(
    UUID ordemServicoId,
    String clienteNome,
    String clienteEmail,
    String veiculoMarca,
    String veiculoModelo,
    String veiculoPlaca,
    String motivoRecusa
) {}
