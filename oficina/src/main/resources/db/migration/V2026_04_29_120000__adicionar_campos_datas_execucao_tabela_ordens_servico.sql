-- Migration: Adicionar campos de datas de execução na tabela ordens_servico
-- Descrição: Adiciona campos data_inicio_execucao e data_finalizacao para controle de tempo de execução da ordem de serviço
-- Autor: Arquiteto do Projeto
-- Data: 2026-04-29 12:00:00

-- Adicionar campo data_inicio_execucao
ALTER TABLE ordens_servico 
ADD COLUMN data_inicio_execucao DATETIME(6) NULL 
COMMENT 'Data e hora de início da execução da OS (quando status muda para EM_EXECUCAO)' 
AFTER status;

-- Adicionar campo data_finalizacao
ALTER TABLE ordens_servico 
ADD COLUMN data_finalizacao DATETIME(6) NULL 
COMMENT 'Data e hora de finalização da OS (quando status muda para FINALIZADA)' 
AFTER data_inicio_execucao;

-- Criar índice para busca por data de início de execução
CREATE INDEX idx_ordens_servico_data_inicio_execucao ON ordens_servico(data_inicio_execucao) COMMENT 'Índice para busca por data de início de execução';

-- Criar índice para busca por data de finalização
CREATE INDEX idx_ordens_servico_data_finalizacao ON ordens_servico(data_finalizacao) COMMENT 'Índice para busca por data de finalização';

-- Atualizar comentário da tabela
ALTER TABLE ordens_servico COMMENT = 'Tabela de ordens de serviço da oficina mecânica com controle de datas de execução';
