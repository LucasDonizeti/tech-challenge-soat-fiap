-- Migration: Adicionar campo codigo nas tabelas servicos e mros
-- Descrição: Cria código único de identificação para serviços e MROs,
--            permitindo a abertura de OS informando códigos ao invés de UUIDs
-- Autor: Arquiteto do Projeto
-- Data: 2026-08-03 10:00:00

-- Adicionar campo codigo na tabela servicos
ALTER TABLE servicos
ADD COLUMN codigo VARCHAR(30) NULL
COMMENT 'Código único do serviço para identificação na abertura de OS'
AFTER nome;

-- Preencher valores padrão para registros existentes (SVC-<primeiros 8 chars do UUID>)
UPDATE servicos
SET codigo = CONCAT('SVC-', LOWER(SUBSTR(HEX(id), 1, 8)))
WHERE codigo IS NULL;

-- Tornar o campo NOT NULL e UNIQUE após preencher valores existentes
ALTER TABLE servicos
MODIFY COLUMN codigo VARCHAR(30) NOT NULL
COMMENT 'Código único do serviço para identificação na abertura de OS';

ALTER TABLE servicos
ADD CONSTRAINT uq_servicos_codigo UNIQUE (codigo);

CREATE INDEX idx_servicos_codigo ON servicos(codigo) COMMENT 'Índice para busca por código do serviço';

-- Adicionar campo codigo na tabela mros
ALTER TABLE mros
ADD COLUMN codigo VARCHAR(30) NULL
COMMENT 'Código único do MRO (peça/insumo) para identificação na abertura de OS'
AFTER nome;

-- Preencher valores padrão para registros existentes
UPDATE mros
SET codigo = CONCAT('MRO-', LOWER(SUBSTR(HEX(id), 1, 8)))
WHERE codigo IS NULL;

-- Tornar o campo NOT NULL e UNIQUE após preencher valores existentes
ALTER TABLE mros
MODIFY COLUMN codigo VARCHAR(30) NOT NULL
COMMENT 'Código único do MRO (peça/insumo) para identificação na abertura de OS';

ALTER TABLE mros
ADD CONSTRAINT uq_mros_codigo UNIQUE (codigo);

CREATE INDEX idx_mros_codigo ON mros(codigo) COMMENT 'Índice para busca por código do MRO';
