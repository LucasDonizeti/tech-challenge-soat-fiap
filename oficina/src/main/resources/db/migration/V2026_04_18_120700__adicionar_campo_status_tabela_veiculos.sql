-- Migration: Adicionar campo status na tabela veiculos
-- Descrição: Adiciona o campo status para controle de ativação/inativação de veículos
-- Autor: Arquiteto do Projeto
-- Data: 2026-04-18 12:07:00

-- Adicionar campo status
ALTER TABLE veiculos 
ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ATIVO' 
COMMENT 'Status do veículo (ATIVO ou INATIVO)' 
AFTER cor;

-- Criar índice para busca por status
CREATE INDEX idx_veiculos_status ON veiculos(status) COMMENT 'Índice para busca por status';

-- Atualizar comentário da tabela
ALTER TABLE veiculos COMMENT = 'Tabela de veículos dos clientes da oficina mecânica com controle de status';
