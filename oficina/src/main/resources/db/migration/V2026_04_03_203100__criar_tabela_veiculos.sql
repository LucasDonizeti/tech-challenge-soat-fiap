-- Migration: Criar tabela de veículos
-- Descrição: Criação da tabela veiculos para armazenar dados de veículos dos clientes
-- Autor: Arquiteto do Projeto
-- Data: 2026-04-03 20:32:00

CREATE TABLE IF NOT EXISTS veiculos (
    id BINARY(16) NOT NULL PRIMARY KEY COMMENT 'UUID identificador único do veículo',
    placa VARCHAR(8) NOT NULL UNIQUE COMMENT 'Placa do veículo (8 caracteres sem formatação)',
    marca VARCHAR(50) NOT NULL COMMENT 'Marca do veículo',
    modelo VARCHAR(50) NOT NULL COMMENT 'Modelo do veículo',
    ano INT NOT NULL COMMENT 'Ano de fabricação do veículo',
    cor VARCHAR(30) NULL COMMENT 'Cor do veículo',
    cliente_id BINARY(16) NOT NULL COMMENT 'ID do cliente proprietário',
    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Data e hora de criação',
    atualizado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Data e hora da última atualização',
    
    CONSTRAINT fk_veiculos_cliente FOREIGN KEY (cliente_id) 
        REFERENCES clientes(id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Tabela de veículos dos clientes';

-- Índices para performance
CREATE INDEX idx_veiculos_cliente_id ON veiculos(cliente_id) COMMENT 'Índice para busca por cliente';
CREATE INDEX idx_veiculos_placa ON veiculos(placa) COMMENT 'Índice para busca por placa';
CREATE INDEX idx_veiculos_marca ON veiculos(marca) COMMENT 'Índice para busca por marca';
CREATE INDEX idx_veiculos_ano ON veiculos(ano) COMMENT 'Índice para busca por ano';
CREATE INDEX idx_veiculos_criado_em ON veiculos(criado_em) COMMENT 'Índice para ordenação por data de criação';

-- Comentários adicionais
ALTER TABLE veiculos COMMENT = 'Tabela de veículos dos clientes da oficina mecânica';
