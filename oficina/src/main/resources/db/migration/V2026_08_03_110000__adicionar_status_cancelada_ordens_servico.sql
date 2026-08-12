-- Migration: Adicionar status CANCELADA na tabela ordens_servico
-- Descrição: Suporte ao fluxo de recusa de orçamento pelo cliente
-- Data: 2026-08-03 11:00:00

ALTER TABLE ordens_servico
MODIFY COLUMN status ENUM(
    'RECEBIDA',
    'EM_DIAGNOSTICO',
    'AGUARDANDO_APROVACAO',
    'EM_EXECUCAO',
    'FINALIZADA',
    'ENTREGUE',
    'CANCELADA'
) NOT NULL DEFAULT 'RECEBIDA'
COMMENT 'Status da ordem de serviço';
