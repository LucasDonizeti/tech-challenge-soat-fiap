# ADR-005: Implementação de Logging com SLF4J

**Número do ADR:** 005  
**Título:** Implementação de Logging com SLF4J  
**Data:** 2026-03-28  
**Responsável:** Arquiteto do Projeto  
**Status:** Aceito

## Contexto
Para monitoramento, debug e auditoria do sistema, era necessário implementar uma solução de logging estruturado que atendesse aos requisitos de observabilidade.

## Decisão
Foi decidido usar SLF4J com Logback como implementação, formato JSON em produção, níveis configuráveis por ambiente e logs de auditoria para ações críticas.

## Justificativa
A decisão de usar SLF4J baseou-se nas seguintes razões:

- **Padrão de Mercado:** SLF4J é o padrão de fato para logging em aplicações Java modernas.
- **Performance:** Excelente performance com baixo overhead em produção.
- **Flexibilidade:** Permite troca de implementações sem mudar o código.
- **Integração:** Nativa com Spring Boot e ecossistema Java.
- **Estruturação:** Formato JSON facilita integração com sistemas de logs centralizados.

## Alternativas Consideradas
Foram consideradas as seguintes alternativas:

- **Log4j2:** Alternativa popular, mas SLF4J tem melhor integração com Spring Boot.
- **java.util.logging:** Padrão Java, mas menos flexível e com comunidade menor.
- **System.out.println:** Simples para protótipos, mas inadequado para produção.

## Consequências
A escolha de SLF4J traz as seguintes consequências:

- **Benefícios:** Padrão de mercado, performance excelente, alta configurabilidade, bom ecossistema.
- **Desafios:** Configuração inicial pode ser complexa, necessidade de planejamento de níveis de log.
- **Impacto no Desenvolvimento:** Requer disciplina para usar níveis apropriados e evitar informações sensíveis.

## Referências
- SLF4J Documentation: http://www.slf4j.org/
- Spring Boot Logging Guide: https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.logging
