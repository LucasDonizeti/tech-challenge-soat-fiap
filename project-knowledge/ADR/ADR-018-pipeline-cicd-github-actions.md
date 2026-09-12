# ADR-018: Pipeline de CI/CD com GitHub Actions

**Número do ADR:** 018  
**Título:** Pipeline de CI/CD com GitHub Actions  
**Data:** 2026-04-28  
**Responsável:** Arquiteto do Projeto  
**Status:** Aceito

## Contexto
O Tech Challenge Fase 2 exige uma pipeline de CI/CD que execute, de forma automatizada e encadeada:
- Build e testes da aplicação
- Build e push da imagem Docker para registro de container
- Deploy no cluster Kubernetes

O projeto usa GitHub como plataforma de versionamento (repositório privado compartilhado com `soat-architecture`), o que torna o GitHub Actions a solução de CI/CD mais natural — sem necessidade de infraestrutura adicional de CI.

## Decisão
Foi decidido adotar o **GitHub Actions** com uma pipeline de três jobs encadeados, disparada em pushes para a branch `develop`:

### Estrutura da pipeline (`.github/workflows/pipeline.yml`)

```
on:
  push:
    branches: ["develop"]

Job 1: build          → mvn verify (testes + JaCoCo)
Job 2: push-image-ecr → build Docker + push para ECR (depende de: build)
Job 3: deploy         → helm upgrade --install no EKS (depende de: push-image-ecr)
```

### Job 1 — Build & Test
- Runner: `ubuntu-latest`
- JDK: Amazon Corretto 21 (alinhado com a imagem de produção)
- Comando: `mvn -B verify --file pom.xml` (inclui compilação, testes e verificação JaCoCo)
- Artefato: JAR publicado como GitHub Artifact (retenção 1 dia) para reutilização no Job 2

### Job 2 — Build & Push Docker Image
- Autentica na AWS via `aws-actions/configure-aws-credentials@v4` com `AWS_SESSION_TOKEN` (suporte ao AWS Academy)
- Login no ECR via `aws-actions/amazon-ecr-login@v2`
- Tag da imagem: SHORT_SHA (7 chars do commit) + `latest`
- Push de ambas as tags para garantir rastreabilidade e facilidade de rollback

### Job 3 — Deploy Helm → EKS
- Configura `kubectl` via `aws eks update-kubeconfig`
- Recupera endpoint do RDS via `aws rds describe-db-instances`
- Recupera senha do RDS do **AWS Secrets Manager** — mascarada nos logs com `::add-mask::`
- Cria/atualiza `imagePullSecret` do ECR no namespace `default`
- Executa `helm upgrade --install` com:
  - `--rollback-on-failure` — reverte automaticamente em falha
  - `--timeout 10m` — aguarda até 10 minutos
  - `--wait` — confirma pods prontos antes de concluir
- Valida o rollout com `kubectl rollout status deployment/oficina-api --timeout=5m`

### Segredos GitHub Actions necessários
| Secret | Uso |
|--------|-----|
| `AWS_ACCESS_KEY_ID` | Autenticação AWS |
| `AWS_SECRET_ACCESS_KEY` | Autenticação AWS |
| `AWS_SESSION_TOKEN` | Sessão temporária AWS Academy |
| `DB_USERNAME` | Username do RDS (injetado via Helm) |
| `JWT_SECRET` | Chave de assinatura JWT |
| `SECURITY_USER_NAME` | Username admin da aplicação |
| `SECURITY_USER_PASSWORD` | Senha admin da aplicação |
| `NEW_RELIC_LICENSE_KEY` | License key do New Relic APM |

## Justificativa
- **Requisito explícito Fase 2:** Pipeline de CI/CD é um entregável obrigatório, cobrindo build, testes, build da imagem Docker e deploy no Kubernetes
- **GitHub Actions nativo:** O projeto já está no GitHub — sem infraestrutura adicional de CI, sem custo adicional no plano gratuito para repositórios públicos/acadêmicos
- **Actions oficiais da AWS:** `aws-actions/configure-aws-credentials`, `amazon-ecr-login` e integração com `kubectl`/`helm` são bem mantidos e documentados
- **Três jobs independentes:** Separação clara de responsabilidades; falha no Job 1 impede push desnecessário de imagem com testes quebrados; falha no Job 2 impede tentativa de deploy de imagem inexistente
- **Short SHA como tag:** Garante rastreabilidade entre commit Git e imagem Docker em produção; tag `latest` facilita rollback manual rápido
- **Senha RDS via Secrets Manager:** A senha nunca é armazenada em texto claro em variáveis de ambiente permanentes ou no repositório; é recuperada dinamicamente a cada deploy
- **`--rollback-on-failure`:** Preserva disponibilidade da versão anterior em caso de falha no deploy da nova versão

## Alternativas Consideradas

### GitLab CI/CD
- **Vantagem:** Pipeline como código com sintaxe mais expressiva; runners auto-hospedados nativos
- **Desvantagem:** O projeto usa GitHub — migrar para GitLab apenas para o CI adicionaria complexidade desnecessária (dois sistemas de versionamento)
- **Decisão:** Descartada

### Jenkins
- **Vantagem:** Amplamente adotado no mercado, altamente configurável, plugins ricos
- **Desvantagem:** Requer infraestrutura própria para o servidor Jenkins (EC2, ECS ou self-hosted); custo adicional e complexidade operacional desnecessária para projeto acadêmico
- **Decisão:** Descartada pelo overhead operacional

### AWS CodePipeline + CodeBuild
- **Vantagem:** Nativo à AWS, integração direta com ECR e EKS sem configuração de credenciais
- **Desvantagem:** Configuração mais complexa (múltiplos recursos Terraform adicionais — CodePipeline, CodeBuild, roles IAM específicas); não disponível completamente na `LabRole` do Academy; menos familiar que GitHub Actions
- **Decisão:** Descartada pela complexidade de IAM e pela disponibilidade do Academy

### ArgoCD (GitOps)
- **Vantagem:** Padrão GitOps — deploy declarativo, reconciliação contínua, UI de visualização de estado do cluster
- **Desvantagem:** Requer instalação do ArgoCD no cluster (overhead para nodes `t3.medium`); adiciona complexidade operacional; curva de aprendizado alta para o contexto acadêmico
- **Decisão:** Descartada; identificada como evolução natural em ambiente de produção real

## Consequências

### Benefícios
- Deploy completamente automatizado — push para `develop` aciona toda a cadeia até o EKS
- Testes obrigatórios antes de qualquer push de imagem — sem regressões não detectadas em produção
- Rastreabilidade total: cada imagem Docker é identificada pelo commit Git que a gerou
- Senhas e segredos nunca expostos nos logs ou no código
- Rollback automático em falha preserva a disponibilidade

### Desafios
- Sessões temporárias do AWS Academy exigem atualização frequente dos três secrets AWS (`ACCESS_KEY_ID`, `SECRET_ACCESS_KEY`, `SESSION_TOKEN`) — processo manual a cada nova sessão
- `--timeout 10m` no Helm pode não ser suficiente se o cluster estiver sobrecarregado durante o deploy
- Pipeline acoplada à branch `develop` — deploy em produção requer ajuste para a branch `main`
- Custo de runners GitHub Actions (2.000 minutos/mês gratuitos no plano Free; suficiente para o volume acadêmico)

### Impacto no Desenvolvimento
- **Não fazer push diretamente na branch de deploy** com código que não compilou localmente — o pipeline falha no Job 1
- **Atualizar os três secrets AWS** a cada sessão do Academy antes de disparar o pipeline
- **Não commitar segredos** no repositório — todos os valores sensíveis são injetados via GitHub Secrets
- A verificação de `kubectl rollout status` no Job 3 confirma que os pods subiram corretamente — falha aqui indica problema na aplicação (crashloop, liveness probe falhando, imagem incorreta)

## Referências
- GitHub Actions Documentation: https://docs.github.com/en/actions
- AWS Actions for GitHub: https://github.com/aws-actions
- Helm Upgrade Reference: https://helm.sh/docs/helm/helm_upgrade/
- ADR-004: Containerização com Docker
- ADR-012: Requisito de Cobertura de Testes 80%
- ADR-017: Orquestração de Containers com Kubernetes (EKS) e Helm
- RFC-001: Escolha do Provedor de Nuvem — AWS
