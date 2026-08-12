-- Migration: Adicionar coluna senha_hash na tabela clientes
-- Descrição: Suporte à autenticação de clientes via CPF + senha para geração de JWT
-- Data: 2026-08-03 12:00:00

ALTER TABLE clientes
ADD COLUMN senha_hash VARCHAR(255) NULL
COMMENT 'Hash BCrypt da senha do cliente — nunca trafega nas APIs de resposta'
AFTER email;
