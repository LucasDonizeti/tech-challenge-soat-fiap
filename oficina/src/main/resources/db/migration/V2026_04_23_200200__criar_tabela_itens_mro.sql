-- Migration: Criar tabela de itens de MRO
-- Descrição: Criação da tabela itens_mro para armazenar MROs usados em itens de serviço
-- Autor: Arquiteto do Projeto
-- Data: 2026-04-23 20:02:00

CREATE TABLE IF NOT EXISTS itens_mro (
    id BINARY(16) NOT NULL PRIMARY KEY COMMENT 'UUID identificador único do item de MRO',
    item_servico_id BINARY(16) NOT NULL COMMENT 'ID do item de serviço',
    mro_id BINARY(16) NOT NULL COMMENT 'ID do MRO do catálogo',
    quantidade INT NOT NULL COMMENT 'Quantidade consumida',
    valor_unitario DECIMAL(10,2) NOT NULL COMMENT 'Valor unitário no momento do uso (snapshot)',
    
    CONSTRAINT fk_itens_mro_item_servico FOREIGN KEY (item_servico_id) 
        REFERENCES itens_servico(id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    CONSTRAINT fk_itens_mro_mro FOREIGN KEY (mro_id) 
        REFERENCES mros(id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Tabela de itens de MRO';

-- Índices para performance e integridade básica
CREATE INDEX idx_itens_mro_item_servico_id ON itens_mro(item_servico_id) COMMENT 'Índice para busca por item de serviço';
CREATE INDEX idx_itens_mro_mro_id ON itens_mro(mro_id) COMMENT 'Índice para busca por MRO';

-- Comentários adicionais
ALTER TABLE itens_mro COMMENT = 'Tabela de itens de MRO usados nos serviços';
