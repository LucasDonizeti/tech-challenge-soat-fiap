package com.techchallenge.oficina.os.domain.events;

import java.util.UUID;

/**
 * Evento de domínio emitido quando o veículo é entregue.
 */
public record VeiculoEntregueEvent(
    UUID ordemServicoId,
    String clienteNome,
    String clienteEmail,
    String veiculoMarca,
    String veiculoModelo,
    String veiculoPlaca
) {}
