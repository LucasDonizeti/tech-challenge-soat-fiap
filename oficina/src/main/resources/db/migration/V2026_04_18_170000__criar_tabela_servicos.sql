-- Migration: Criar tabela de serviços
-- Descrição: Criação da tabela servicos para armazenar dados de serviços da oficina
-- Autor: Arquiteto do Projeto
-- Data: 2026-04-18 17:00:00

CREATE TABLE IF NOT EXISTS servicos (
    id BINARY(16) NOT NULL PRIMARY KEY COMMENT 'UUID identificador único do serviço',
    nome VARCHAR(100) NOT NULL COMMENT 'Nome do serviço',
    descricao VARCHAR(500) NULL COMMENT 'Descrição detalhada do serviço',
    preco DECIMAL(10,2) NOT NULL COMMENT 'Valor base do serviço para orçamento',
    ativo BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Flag para soft-delete (TRUE=ativo, FALSE=inativo)',
    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Data e hora de criação',
    atualizado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Data e hora da última atualização'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Tabela de serviços da oficina';

-- Índices para performance e integridade básica
CREATE INDEX idx_servicos_ativo ON servicos(ativo) COMMENT 'Índice para busca por status ativo/inativo';
CREATE INDEX idx_servicos_nome ON servicos(nome) COMMENT 'Índice para busca por nome';
CREATE INDEX idx_servicos_preco ON servicos(preco) COMMENT 'Índice para ordenação por preço';
CREATE INDEX idx_servicos_criado_em ON servicos(criado_em) COMMENT 'Índice para ordenação por data de criação';

-- Comentários adicionais
ALTER TABLE servicos COMMENT = 'Tabela de serviços da oficina mecânica';
