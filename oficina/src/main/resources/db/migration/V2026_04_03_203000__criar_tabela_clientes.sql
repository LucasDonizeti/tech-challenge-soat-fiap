-- Migration: Criar tabela de clientes
-- Descrição: Criação da tabela clientes para armazenar dados de clientes da oficina
-- Autor: Arquiteto do Projeto
-- Data: 2026-04-03 20:30:00

CREATE TABLE IF NOT EXISTS clientes (
    id BINARY(16) NOT NULL PRIMARY KEY COMMENT 'UUID identificador único do cliente',
    nome VARCHAR(100) NOT NULL COMMENT 'Nome completo do cliente',
    cpf VARCHAR(11) NULL COMMENT 'CPF do cliente (11 dígitos sem formatação)',
    cnpj VARCHAR(14) NULL COMMENT 'CNPJ do cliente (14 dígitos sem formatação)',
    email VARCHAR(100) NOT NULL COMMENT 'Email do cliente',
    status ENUM('ATIVO', 'INATIVO') NOT NULL DEFAULT 'ATIVO' COMMENT 'Status do cliente',
    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Data e hora de criação',
    atualizado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Data e hora da última atualização'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Tabela de clientes da oficina';

-- Índices para performance e integridade básica
CREATE UNIQUE INDEX uk_clientes_email ON clientes(email) COMMENT 'Índice único para email';
CREATE INDEX idx_clientes_status ON clientes(status) COMMENT 'Índice para busca por status';
CREATE INDEX idx_clientes_nome ON clientes(nome) COMMENT 'Índice para busca por nome';
CREATE INDEX idx_clientes_cpf ON clientes(cpf) COMMENT 'Índice para busca por CPF';
CREATE INDEX idx_clientes_cnpj ON clientes(cnpj) COMMENT 'Índice para busca por CNPJ';
CREATE INDEX idx_clientes_criado_em ON clientes(criado_em) COMMENT 'Índice para ordenação por data de criação';

-- Comentários adicionais
ALTER TABLE clientes COMMENT = 'Tabela de clientes da oficina mecânica';
