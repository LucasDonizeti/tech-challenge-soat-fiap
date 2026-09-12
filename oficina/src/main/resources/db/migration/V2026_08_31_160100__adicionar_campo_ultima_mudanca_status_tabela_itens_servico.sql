-- Migration: Adicionar campo data_ultima_mudanca_status na tabela itens_servico
-- Descrição: Adiciona campo data_ultima_mudanca_status para rastrear timestamp da última mudança de status do item de serviço
-- Autor: Arquiteto do Projeto
-- Data: 2026-08-31 16:01:00

-- Adicionar campo data_ultima_mudanca_status
ALTER TABLE itens_servico 
ADD COLUMN data_ultima_mudanca_status DATETIME(6) NULL 
COMMENT 'Data e hora da última mudança de status do item de serviço (usado para cálculo de tempo entre mudanças)' 
AFTER data_finalizacao;

-- Criar índice para busca por data de última mudança de status
CREATE INDEX idx_itens_servico_data_ultima_mudanca_status ON itens_servico(data_ultima_mudanca_status) COMMENT 'Índice para busca por data de última mudança de status';

-- Atualizar comentário da tabela
ALTER TABLE itens_servico COMMENT = 'Tabela de itens de serviço da oficina mecânica com controle de tempo entre mudanças de status';
