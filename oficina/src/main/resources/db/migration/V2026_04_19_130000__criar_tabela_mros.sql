-- Migration: Criar tabela de MROs (Materiais, Reparos e Óleo)
-- Descrição: Criação da tabela mros para armazenar dados de materiais, reparos e óleo da oficina
-- Autor: Arquiteto do Projeto
-- Data: 2026-04-19 13:00:00

CREATE TABLE IF NOT EXISTS mros (
    id BINARY(16) NOT NULL PRIMARY KEY COMMENT 'UUID identificador único do MRO',
    nome VARCHAR(100) NOT NULL COMMENT 'Nome do MRO (peça, insumo, etc)',
    descricao VARCHAR(500) NULL COMMENT 'Descrição detalhada do MRO',
    tipo VARCHAR(20) NOT NULL COMMENT 'Tipo do MRO (PECA ou INSUMO)',
    quantidade_estoque INT NOT NULL DEFAULT 0 COMMENT 'Quantidade em estoque',
    preco_unitario DECIMAL(10,2) NOT NULL COMMENT 'Preço unitário do MRO',
    ativo BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Flag para soft-delete (TRUE=ativo, FALSE=inativo)',
    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Data e hora de criação',
    atualizado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Data e hora da última atualização'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Tabela de MROs (Materiais, Reparos e Óleo) da oficina';

-- Índices para performance e integridade básica
CREATE INDEX idx_mros_ativo ON mros(ativo) COMMENT 'Índice para busca por status ativo/inativo';
CREATE INDEX idx_mros_tipo ON mros(tipo) COMMENT 'Índice para busca por tipo (PECA/INSUMO)';
CREATE INDEX idx_mros_nome ON mros(nome) COMMENT 'Índice para busca por nome';
CREATE INDEX idx_mros_quantidade_estoque ON mros(quantidade_estoque) COMMENT 'Índice para busca por quantidade em estoque';
CREATE INDEX idx_mros_preco_unitario ON mros(preco_unitario) COMMENT 'Índice para ordenação por preço unitário';
CREATE INDEX idx_mros_criado_em ON mros(criado_em) COMMENT 'Índice para ordenação por data de criação';

-- Comentários adicionais
ALTER TABLE mros COMMENT = 'Tabela de MROs (Materiais, Reparos e Óleo) da oficina mecânica';
