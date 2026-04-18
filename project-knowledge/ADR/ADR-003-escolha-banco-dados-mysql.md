# ADR-003: Escolha do Banco de Dados MySQL

**Número do ADR:** 003  
**Título:** Escolha do Banco de Dados MySQL  
**Data:** 2026-03-28  
**Responsável:** Arquiteto do Projeto  
**Status:** Aceito

## Contexto
Para o MVP do sistema da oficina, era necessário escolher um banco de dados que atendesse aos requisitos de persistência, transações e fosse adequado para uma arquitetura monolítica inicial.

## Decisão
Foi decidido utilizar MySQL 8.0 como banco de dados relacional, com Flyway para migrations, JPA/Hibernate como ORM e pool de conexões HikariCP.

## Justificativa
A decisão de usar MySQL baseou-se nas seguintes razões:

- **Modelagem Relacional:** O domínio da oficina possui relações bem definidas (Cliente-Veículo, OS-Serviço-Peca) que se beneficiam de modelo relacional.
- **Transações ACID:** Criticamente importante para consistência de dados financeiros e de estoque.
- **Maturidade:** MySQL é amplamente utilizado, bem documentado e com grande comunidade.
- **Performance:** Adequado para o volume esperado no MVP, com bom desempenho em consultas relacionais.
- **Compatibilidade:** Excelente integração com Spring Boot e ecossistema Java.

## Alternativas Consideradas
Foram consideradas as seguintes alternativas:

- **PostgreSQL:** Mais avançado em alguns aspectos, mas MySQL é suficientemente capaz para os requisitos atuais.
- **MongoDB:** Flexível para dados não estruturados, mas a consistência transacional é crucial para o domínio.
- **H2 Database:** Ótimo para testes, mas inadequado para produção persistente.

## Consequências
A escolha de MySQL traz as seguintes consequências:

- **Benefícios:** Maturidade, boa performance, transações ACID, excelente integração com Spring.
- **Desafios:** Schema rígido requer planejamento, escalabilidade vertical limitada, migrações futuras podem ser complexas.
- **Impacto no Desenvolvimento:** Requer planejamento cuidadoso do schema e uso de migrations versionadas.

## Referências
- MySQL Documentation: https://dev.mysql.com/doc/
- Spring Boot JPA Guide: https://spring.io/guides/gs/accessing-data-jpa/
