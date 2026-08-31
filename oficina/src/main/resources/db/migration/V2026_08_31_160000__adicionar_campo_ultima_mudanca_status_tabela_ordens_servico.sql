-- Migration: Adicionar campo data_ultima_mudanca_status na tabela ordens_servico
-- Descrição: Adiciona campo data_ultima_mudanca_status para rastrear timestamp da última mudança de status da OS
-- Autor: Arquiteto do Projeto
-- Data: 2026-08-31 16:00:00

-- Adicionar campo data_ultima_mudanca_status
ALTER TABLE ordens_servico 
ADD COLUMN data_ultima_mudanca_status DATETIME(6) NULL 
COMMENT 'Data e hora da última mudança de status da OS (usado para cálculo de tempo entre mudanças)' 
AFTER data_finalizacao;

-- Criar índice para busca por data de última mudança de status
CREATE INDEX idx_ordens_servico_data_ultima_mudanca_status ON ordens_servico(data_ultima_mudanca_status) COMMENT 'Índice para busca por data de última mudança de status';

-- Atualizar comentário da tabela
ALTER TABLE ordens_servico COMMENT = 'Tabela de ordens de serviço da oficina mecânica com controle de tempo entre mudanças de status';
