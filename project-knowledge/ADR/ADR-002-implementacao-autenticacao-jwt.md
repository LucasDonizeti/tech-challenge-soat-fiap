# ADR-002: Implementação de Autenticação JWT

**Número do ADR:** 002  
**Título:** Implementação de Autenticação JWT  
**Data:** 2026-03-28  
**Responsável:** Arquiteto do Projeto  
**Status:** Aceito

## Contexto
O requisito do projeto especifica explicitamente "Implementação de autenticação JWT para APIs administrativas" na seção de Segurança e Qualidade. Era necessário definir uma estratégia de autenticação que atendesse a este requisito obrigatório e aos requisitos de segurança e escalabilidade do sistema.

## Decisão
Foi decidido implementar autenticação baseada em JWT (JSON Web Tokens) com assinatura HMAC-SHA256, expiração de 24 horas e armazenamento no header Authorization.

## Justificativa
A decisão de usar JWT baseou-se nas seguintes razões:

- **Requisito Obrigatório:** O projeto exige explicitamente "Implementação de autenticação JWT para APIs administrativas" conforme seção de Segurança e Qualidade dos requisitos.
- **Stateless:** JWT permite autenticação sem estado, facilitando escalabilidade horizontal.
- **Padrão de Mercado:** JWT é amplamente adotado e bem documentado na comunidade.
- **Integração:** Funciona bem com Spring Security e não requer sessões no servidor.
- **Performance:** Elimina necessidade de consultas ao banco para validação de sessão.

## Alternativas Consideradas
Foram consideradas as seguintes alternativas:

- **Session-based Authentication:** Simples de implementar, mas requer estado no servidor.
- **OAuth 2.0:** Mais robusto para integração com terceiros, mas complexo demais para uso interno.
- **API Keys:** Simples, mas não oferece informações de contexto do usuário.

## Consequências
A escolha de JWT traz as seguintes consequências:

- **Benefícios:** Stateless, escalável, padrão de mercado, boa performance.
- **Desafios:** Revogação complexa de tokens, necessidade de refresh tokens para sessões longas, gerenciamento seguro da chave secreta.
- **Impacto na Arquitetura:** Requer middleware para validação em todas as requisições protegidas.

## Referências
- JWT RFC 7519: https://tools.ietf.org/html/rfc7519
- Spring Security JWT Guide: https://spring.io/guides/tutorials/spring-boot-oauth2-resource-server/
