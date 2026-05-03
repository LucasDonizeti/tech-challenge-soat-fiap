-- Adicionar campos de data de início e finalização de execução na tabela itens_servico
-- Isso permite rastrear o tempo de execução individual de cada serviço

ALTER TABLE itens_servico 
ADD COLUMN data_inicio_execucao DATETIME NULL COMMENT 'Data e hora em que o serviço foi iniciado (status EM_ANDAMENTO)';

ALTER TABLE itens_servico 
ADD COLUMN data_finalizacao DATETIME NULL COMMENT 'Data e hora em que o serviço foi finalizado (status CONCLUIDO)';
