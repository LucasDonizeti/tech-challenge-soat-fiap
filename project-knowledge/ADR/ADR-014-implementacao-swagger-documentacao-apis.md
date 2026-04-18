# ADR-014: Implementação de Swagger para Documentação de APIs

**Número do ADR:** 014  
**Título:** Implementação de Swagger para Documentação de APIs  
**Data:** 2026-04-17  
**Responsável:** Arquiteto do Projeto  
**Status:** Aceito

## Contexto
O projeto Tech Challenge SOAT FIAP possui múltiplas APIs REST distribuídas em diferentes bounded contexts (OrdemServico, Cliente, Estoque, Administrativo). Atualmente, não existe uma documentação formal e padronizada das APIs, o que dificulta:

- A integração entre diferentes contextos delimitados
- O onboarding de novos desenvolvedores
- A comunicação com consumidores externos das APIs
- A validação de contratos de API
- A manutenção e evolução das interfaces

A ausência de documentação clara e atualizada pode gerar inconsistências entre o contrato definido e a implementação real, além de aumentar o tempo de desenvolvimento e integração.

## Decisão
Foi decidido implementar o Swagger (OpenAPI Specification) para documentação automática e interativa das APIs REST do sistema com as seguintes configurações:

- **Framework:** SpringDoc OpenAPI (integração nativa com Spring Boot)
- **Versão OpenAPI:** 3.0
- **Configurações globais:** Título, versão, descrição, informações de contato e licença
- **Agrupamento de endpoints:** Organização por bounded context/tags (OrdemServico, Cliente, Estoque, Administrativo)
- **Segurança:** Documentação dos endpoints JWT com esquema Bearer Token
- **Validação de esquemas:** Integração com Bean Validation para documentar restrições
- **UI Interativa:** Swagger UI disponível em `/swagger-ui.html`
- **JSON Specification:** OpenAPI JSON disponível em `/v3/api-docs`
- **Anotações personalizadas:** Uso de `@Operation`, `@ApiResponse`, `@Parameter` para enriquecer a documentação

## Justificativa
A escolha do Swagger baseia-se nos seguintes fatores:

- **Padrão de mercado:** OpenAPI Specification é o padrão mais amplamente adotado para documentação de APIs REST
- **Integração nativa:** SpringDoc OpenAPI integra-se perfeitamente com Spring Boot, Spring MVC e Spring WebFlux
- **Documentação viva:** A documentação é gerada automaticamente a partir do código, mantendo-se sempre atualizada
- **UI interativa:** Swagger UI permite testar os endpoints diretamente no navegador, facilitando debugging e integração
- **Geração de clientes:** A especificação OpenAPI pode ser usada para gerar automaticamente clientes em diversas linguagens
- **Validação de contratos:** Permite validar se a implementação está conforme o contrato documentado
- **Suporte a segurança:** Documentação nativa de esquemas de autenticação/autorização (JWT, OAuth2, etc.)
- **Comunidade ativa:** Ampla documentação, exemplos e suporte da comunidade
- **Alinhamento com Clean Architecture:** Não viola os princípios de arquitetura, sendo uma preocupação de infraestrutura

## Alternativas Consideradas

### RAML (RESTful API Modeling Language)
- **Vantagem:** Sintaxe mais concisa e legível, suporte nativo a recursos e tipos
- **Desvantagem:** Menor adoção no mercado, ferramentas menos maduras, comunidade menor
- **Decisão:** Descartada pela menor adoção e ecossistema de ferramentas

### API Blueprint (Markdown)
- **Vantagem:** Baseado em Markdown, fácil de escrever e versionar
- **Desvantagem:** Requer manutenção manual separada do código, risco de desatualização
- **Decisão:** Descartada pela necessidade de documentação automatizada

### Documentação manual (Confluence, Markdown, etc.)
- **Vantagem:** Flexibilidade total para personalização
- **Desvantagem:** Alta manutenção, facilmente desatualizada, não testável
- **Decisão:** Descartada pela dificuldade de manter sincronia com o código

### Postman Collections
- **Vantagem:** Excelente para testes, compartilhamento fácil entre equipes
- **Desvantagem:** Foco em testes, não em documentação formal, manutenção manual
- **Decisão:** Descartada por não ser uma solução de documentação formal

## Consequências
A implementação do Swagger trará as seguintes consequências:

### Benefícios
- **Documentação atualizada:** Sempre sincronizada com o código fonte
- **Testes interativos:** Desenvolvedores podem testar endpoints diretamente via Swagger UI
- **Integração facilitada:** Clientes externos podem entender e consumir as APIs facilmente
- **Onboarding acelerado:** Novos desenvolvedores compreendem rapidamente as APIs disponíveis
- **Geração de clientes:** Possibilidade de gerar automaticamente SDKs clientes
- **Contratos versionados:** OpenAPI JSON pode ser versionado e validado
- **Segurança documentada:** Esquemas de autenticação claramente especificados
- **Padronização:** Força a equipe a manter APIs RESTful e bem estruturadas

### Desafios
- **Anotações adicionais:** Necessidade de adicionar anotações Swagger nos controllers
- **Curva de aprendizado:** Equipe precisa aprender as anotações e boas práticas do OpenAPI
- **Performance:** Sobrecarga mínima na inicialização da aplicação
- **Manutenção:** Anotações precisam ser mantidas atualizadas com as mudanças nos endpoints
- **Exposição em produção:** Necessidade de configurar ambientes onde Swagger UI deve/será exposto

### Impacto no Desenvolvimento
- **Setup inicial:** Configuração da dependência SpringDoc OpenAPI no projeto
- **Anotação de controllers:** Adição de `@Operation`, `@ApiResponse`, `@Parameter` nos endpoints
- **Organização por tags:** Definição de tags para agrupar endpoints por bounded context
- **Documentação de segurança:** Configuração de esquemas JWT para endpoints protegidos
- **Configuração de ambientes:** Swagger UI habilitado em dev/test, desabilitado em produção
- **Padrões de nomenclatura:** Definição de padrões consistentes para nomes de operações e descrições

## Referências
- OpenAPI Specification 3.0: https://swagger.io/specification/
- SpringDoc OpenAPI Documentation: https://springdoc.org/
- Swagger UI: https://swagger.io/tools/swagger-ui/
- Best Practices for Designing a Pragmatic RESTful API: https://github.com/microsoft/api-guidelines/blob/vNext/Guidelines.md
- ADR-001: Adoção de Clean Architecture com Domain-Driven Design
- ADR-002: Implementação de Autenticação JWT
- Tech Challenge SOAT FIAP Requirements Document
