package com.techchallenge.oficina.os.infrastructure.acl.servico;

import com.techchallenge.oficina.os.infrastructure.acl.dto.ServicoIntegrationDto;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface do Anti-Corruption Layer para Serviço
 * Define as operações que o contexto OS precisa do contexto administrativo
 */
public interface ServicoAdapter {
    
    /**
     * Busca um Serviço por ID
     */
    Optional<ServicoIntegrationDto> buscarPorId(UUID id);
}
