package com.techchallenge.oficina.os.infrastructure.acl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO de integração para Serviço (Anti-Corruption Layer)
 * Usado pelo contexto OS para consumir Serviço do contexto administrativo
 * sem dependência direta do domínio externo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicoIntegrationDto {
    
    private UUID id;
    private String nome;
    private String codigo;
    private String descricao;
    private BigDecimal preco;
    private Boolean ativo;
}
