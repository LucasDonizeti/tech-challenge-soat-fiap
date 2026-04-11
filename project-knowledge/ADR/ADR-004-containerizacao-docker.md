# ADR-004: Containerização com Docker

**Número do ADR:** 004  
**Título:** Containerização com Docker  
**Data:** 2026-03-28  
**Responsável:** Arquiteto do Projeto  
**Status:** Aceito

## Contexto
O projeto especifica explicitamente nos requisitos técnicos "Dockerfile para build da aplicação" e "docker-compose.yml para orquestrar ambiente completo". Além disso, exige "Configuração para execução local simples" e "docker-compose.yml configurados" nos entregáveis. Era necessário implementar containerização que atendesse a estes requisitos obrigatórios e garantisse ambiente consistente.

## Decisão
Foi decidido usar Docker + Docker Compose com multi-stage builds, imagem base OpenJDK 21 e volumes para persistência de dados do banco.

## Justificativa
A decisão de usar Docker baseou-se nas seguintes razões:

- **Requisitos Obrigatórios:** O projeto exige explicitamente "Dockerfile para build da aplicação" e "docker-compose.yml para orquestrar ambiente completo" nos requisitos técnicos.
- **Entregável Obrigatório:** "docker-compose.yml configurados" é listado nos entregáveis da Fase 1.
- **Consistência:** Garante ambiente idêntico em desenvolvimento, teste e produção.
- **Simplicidade:** Atende ao requisito de "configuração para execução local simples".
- **Portabilidade:** Facilita deploy em diferentes ambientes e cloud providers.
- **Isolamento:** Separa dependências e evita conflitos com outras aplicações.
- **Padrão de Mercado:** Docker é o padrão de fato para containerização de aplicações Java.

## Alternativas Consideradas
Foram consideradas as seguintes alternativas:

- **Instalação Local:** Simples para desenvolvimento inicial, mas inconsistente entre ambientes.
- **Podman:** Alternativa ao Docker, mas com menor adoção e ecossistema.
- **Kubernetes:** Poderoso para produção, mas excessivamente complexo para MVP.

## Consequências
A escolha de Docker traz as seguintes consequências:

- **Benefícios:** Portabilidade, reproduzibilidade, isolamento, ecossistema maduro.
- **Desafios:** Curva de aprendizado inicial, tamanho de imagens, necessidade de otimização.
- **Impacto no Desenvolvimento:** Requer conhecimento de Docker e otimização de Dockerfiles.

## Referências
- Docker Documentation: https://docs.docker.com/
- Docker Compose Guide: https://docs.docker.com/compose/
