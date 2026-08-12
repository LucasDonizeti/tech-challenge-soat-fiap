package com.techchallenge.oficina.os.infrastructure.acl.mro;

import com.techchallenge.oficina.os.infrastructure.acl.dto.MROIntegrationDto;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface do Anti-Corruption Layer para MRO
 * Define as operações que o contexto OS precisa do contexto administrativo
 */
public interface MROAdapter {
    
    /**
     * Busca um MRO por ID
     */
    Optional<MROIntegrationDto> buscarPorId(UUID id);

    /**
     * Busca um MRO por código
     */
    Optional<MROIntegrationDto> buscarPorCodigo(String codigo);
    
    /**
     * Verifica se um MRO tem estoque suficiente
     */
    boolean temEstoqueSuficiente(UUID mroId, Integer quantidade);
    
    /**
     * Debita estoque de um MRO
     */
    void debitarEstoque(UUID mroId, Integer quantidade);
    
    /**
     * Repõe estoque de um MRO
     */
    void reporEstoque(UUID mroId, Integer quantidade);
}
