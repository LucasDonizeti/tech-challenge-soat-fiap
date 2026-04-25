-- Migration: Criar tabela de ordens de serviço
-- Descrição: Criação da tabela ordens_servico para armazenar dados de ordens de serviço da oficina
-- Autor: Arquiteto do Projeto
-- Data: 2026-04-23 20:00:00

CREATE TABLE IF NOT EXISTS ordens_servico (
    id BINARY(16) NOT NULL PRIMARY KEY COMMENT 'UUID identificador único da ordem de serviço',
    cliente_id BINARY(16) NOT NULL COMMENT 'ID do cliente',
    veiculo_id BINARY(16) NOT NULL COMMENT 'ID do veículo',
    status ENUM('RECEBIDA', 'EM_DIAGNOSTICO', 'AGUARDANDO_APROVACAO', 'EM_EXECUCAO', 'FINALIZADA', 'ENTREGUE') NOT NULL DEFAULT 'RECEBIDA' COMMENT 'Status da ordem de serviço',
    data_criacao DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Data e hora de criação da OS',
    
    CONSTRAINT fk_ordens_servico_cliente FOREIGN KEY (cliente_id) 
        REFERENCES clientes(id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    CONSTRAINT fk_ordens_servico_veiculo FOREIGN KEY (veiculo_id) 
        REFERENCES veiculos(id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Tabela de ordens de serviço da oficina';

-- Índices para performance e integridade básica
CREATE INDEX idx_ordens_servico_cliente_id ON ordens_servico(cliente_id) COMMENT 'Índice para busca por cliente';
CREATE INDEX idx_ordens_servico_veiculo_id ON ordens_servico(veiculo_id) COMMENT 'Índice para busca por veículo';
CREATE INDEX idx_ordens_servico_status ON ordens_servico(status) COMMENT 'Índice para busca por status';
CREATE INDEX idx_ordens_servico_data_criacao ON ordens_servico(data_criacao) COMMENT 'Índice para ordenação por data de criação';

-- Comentários adicionais
ALTER TABLE ordens_servico COMMENT = 'Tabela de ordens de serviço da oficina mecânica';
