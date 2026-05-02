package com.techchallenge.oficina.os.domain.events;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Evento de domínio emitido quando os serviços são finalizados.
 */
public record ServicoFinalizadoEvent(
    UUID ordemServicoId,
    String clienteNome,
    String clienteEmail,
    String veiculoMarca,
    String veiculoModelo,
    String veiculoPlaca,
    BigDecimal valorTotal
) {}
