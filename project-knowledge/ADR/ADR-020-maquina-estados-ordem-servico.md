# ADR-020: Máquina de Estados da Ordem de Serviço

**Número do ADR:** 020  
**Título:** Máquina de Estados da Ordem de Serviço (OS)  
**Data:** 2026-05-05  
**Responsável:** Arquiteto do Projeto  
**Status:** Aceito

## Contexto
A Ordem de Serviço (OS) é o **aggregate raiz central** do sistema e possui um fluxo de vida complexo com seis status distintos. Cada transição tem pré-condições, pós-ações e regras de bloqueio específicas. Além disso, cada Item de Serviço (`ItemServico`) dentro da OS possui seu próprio ciclo de status independente.

Era necessário formalizar:
1. Quais transições são válidas entre status da OS
2. Quais ações são permitidas (ou bloqueadas) em cada status
3. Onde implementar as regras de transição (domínio vs aplicação)
4. Como tratar falhas de transição inválidas
5. Como integrar o cálculo de orçamento e controle de estoque ao fluxo

## Decisão
Foi decidido implementar a máquina de estados da OS **diretamente no aggregate `OrdemServico`** como comportamento do domínio, com transições encapsuladas em métodos de negócio:

### Status da OS e transições válidas

```
RECEBIDA → EM_DIAGNOSTICO → AGUARDANDO_APROVACAO → EM_EXECUCAO → FINALIZADA → ENTREGUE
```

| Transição | Pré-condição | Pós-ação |
|-----------|-------------|---------|
| `RECEBIDA → EM_DIAGNOSTICO` | Pelo menos 1 serviço adicionado | — |
| `EM_DIAGNOSTICO → AGUARDANDO_APROVACAO` | Pelo menos 1 serviço com MROs definidos (se necessário) | Dispara notificação ao cliente |
| `AGUARDANDO_APROVACAO → EM_EXECUCAO` | Aprovação recebida via API `/v1/os/{id}/decisao-orcamento` | Dispara notificação de início |
| `AGUARDANDO_APROVACAO → CANCELADA` | Recusa recebida via API `/v1/os/{id}/decisao-orcamento` | Dispara notificação de cancelamento |
| `EM_EXECUCAO → FINALIZADA` | Todos os `ItemServico` em status `CONCLUIDO` | Dispara notificação de conclusão |
| `FINALIZADA → ENTREGUE` | — | Dispara notificação de entrega |

### Status de ItemServico

```
PENDENTE → EM_ANDAMENTO → CONCLUIDO
PENDENTE → CANCELADO
```

| Transição | Pré-condição | Pós-ação |
|-----------|-------------|---------|
| `PENDENTE → EM_ANDAMENTO` | OS em `EM_EXECUCAO` + estoque suficiente para todos os MROs | Debita estoque dos MROs |
| `EM_ANDAMENTO → CONCLUIDO` | — | Verifica se todos itens concluídos → transiciona OS para `FINALIZADA` |
| `PENDENTE → CANCELADO` | MROs não debitados | — |

### Bloqueios por status (ações proibidas)

| Ação | Permitida em |
|------|-------------|
| Alterar Cliente/Veículo | Apenas `RECEBIDA` |
| Adicionar/remover Serviços | Apenas `RECEBIDA` |
| Adicionar/remover MROs | `RECEBIDA` e `EM_DIAGNOSTICO` |
| Atualizar observações de serviço | `RECEBIDA`, `EM_DIAGNOSTICO` e `EM_EXECUCAO` |
| Alterar status de ItemServico | Apenas `EM_EXECUCAO` |

### Implementação no aggregate

As validações de transição são implementadas no próprio aggregate `OrdemServico`, lançando `OrdemServicoStatusInvalidoException` para transições inválidas:

```java
// Domain — OrdemServico.java
public void enviarParaDiagnostico() {
    if (this.status != StatusOS.RECEBIDA) {
        throw new OrdemServicoStatusInvalidoException(
            "Transição inválida: " + this.status + " → EM_DIAGNOSTICO");
    }
    if (this.itensServico.isEmpty()) {
        throw new ValidacaoOrdemServicoException(
            "OS deve ter ao menos um serviço para ir a diagnóstico");
    }
    this.status = StatusOS.EM_DIAGNOSTICO;
}
```

Os Use Cases da camada de aplicação orquestram as chamadas ao aggregate e às dependências externas (gateway de estoque, serviço de notificação):

```java
// Application — EnviarParaDiagnosticoUseCase.java
public OrdemServicoResponse execute(EnviarParaDiagnosticoCommand command) {
    OrdemServico os = gateway.findById(command.getId())
        .orElseThrow(() -> new OrdemServicoNaoEncontradaException(command.getId()));
    os.enviarParaDiagnostico();          // valida no domínio
    return gateway.save(os);             // persiste
}
```

### Notificações via Domain Events

Transições que disparam notificações ao cliente utilizam o padrão de **Domain Events** (Observer), implementado com `NotificacaoEventHandler` e `ConsoleNotificacaoService`:

- `OrcamentoProntoEvent` → notifica cliente que orçamento está disponível
- `ServicoIniciadoEvent` → notifica cliente que execução começou
- `ServicoFinalizadoEvent` → notifica cliente que veículo está pronto
- `VeiculoEntregueEvent` → notifica cliente da entrega
- `OrcamentoRecusadoEvent` → notifica cliente do cancelamento

### Cálculo de orçamento

O valor total da OS é calculado lazily pelo método `calcularValorTotal()` no aggregate:
```
valorTotal = Σ (valorServico + Σ (quantidade × precoUnitario dos MROs)) por ItemServico
```

## Justificativa
- **Regra de negócio no domínio:** As regras de transição são parte central do domínio da oficina — pertencem ao aggregate, não aos use cases ou controllers. Isso garante que nenhuma transição inválida seja possível independente de qual use case invoque o aggregate
- **Exceções específicas do domínio:** `OrdemServicoStatusInvalidoException` usa linguagem ubíqua e é tratada pelo `GlobalExceptionHandler` (ADR-010) para retornar HTTP 400 com mensagem descritiva
- **Domain Events para notificações:** Desacopla a lógica de notificação do aggregate — o aggregate apenas emite eventos, o handler decide como notificar. Facilita substituir `ConsoleNotificacaoService` por integração real (e-mail, SMS, push notification) sem alterar o domínio
- **`AGUARDANDO_APROVACAO` → `CANCELADA` via callback:** O endpoint `POST /v1/os/{id}/decisao-orcamento` centraliza a decisão do cliente (aprovação ou recusa) em um único ponto de entrada, conforme exigido pelo Tech Challenge Fase 2 ("endpoint para receber notificações externas de aprovação ou recusa")
- **Debito de estoque ao iniciar serviço:** Vincular o débito ao momento de início (`PENDENTE → EM_ANDAMENTO`) garante que o estoque só é comprometido quando o serviço efetivamente começa, evitando reservas desnecessárias em OSs que podem ser canceladas

## Alternativas Consideradas

### Spring State Machine
- **Vantagem:** Framework robusto para máquinas de estado complexas; suporte a persistência de estado, histórico e guards
- **Desvantagem:** Dependência adicional; adiciona complexidade de configuração (states, transitions, guards, actions em beans Spring); para a complexidade atual da OS, é overkill. Viola o princípio de manter o domínio livre de dependências de frameworks
- **Decisão:** Descartada; a máquina de estados é simples o suficiente para ser implementada diretamente no aggregate

### Status como colunas booleanas na entidade
- **Vantagem:** Simples de implementar
- **Desvantagem:** Perde a clareza de estado único; permite estados inconsistentes (ex.: `recebida=true` e `entregue=true` ao mesmo tempo)
- **Decisão:** Descartada

### Transições gerenciadas pelos Use Cases (fora do domínio)
- **Vantagem:** Use Cases com lógica mais rica
- **Desvantagem:** O aggregate deixa de garantir sua própria consistência — múltiplos use cases poderiam criar transições inconsistentes se um deles não verificar o status corretamente
- **Decisão:** Descartada; as validações de transição pertencem ao aggregate (invariante do domínio)

### Status como string (sem enum)
- **Vantagem:** Flexível para adicionar novos status sem recompilar
- **Desvantagem:** Sem type safety; erros de digitação não detectados em compile time; ADR-008 define uso de `@Enumerated(EnumType.STRING)` para enums no MySQL
- **Decisão:** Descartada em favor de enums `StatusOS` e `StatusItemServico`

## Consequências

### Benefícios
- Invariantes do domínio garantidos pelo próprio aggregate — impossível criar estados inválidos via qualquer caminho de código
- Linguagem ubíqua expressa em exceções (`OrdemServicoStatusInvalidoException`) e métodos de negócio (`enviarParaDiagnostico()`, `aprovarOrcamento()`)
- Domain Events desacoplam o aggregate das notificações — substituição por integração real sem impacto no domínio
- Testes unitários do aggregate cobrem 100% das transições válidas e inválidas sem dependência de framework

### Desafios
- Cada nova transição requer alteração no aggregate (método + validação) + use case + controller + migration de status se necessário
- A listagem de OSs prevê ordenação específica: `EM_EXECUCAO > AGUARDANDO_APROVACAO > EM_DIAGNOSTICO > RECEBIDA` — requer ordenação customizada na query JPA
- OSs `FINALIZADA` e `ENTREGUE` devem ser excluídas logicamente (não fisicamente) da listagem padrão

### Impacto no Desenvolvimento
- **Sempre** invocar o método de negócio do aggregate para transições (ex.: `os.enviarParaDiagnostico()`) — nunca atribuir `os.setStatus(StatusOS.EM_DIAGNOSTICO)` diretamente
- Novos fluxos de negócio que necessitem transições devem adicionar o método no aggregate com validação completa
- Testes unitários do aggregate são a primeira linha de defesa — devem cobrir todos os cenários de transição válidos e inválidos

## Referências
- Domain-Driven Design — Eric Evans (Aggregates e Domain Events)
- Implementing Domain-Driven Design — Vaughn Vernon
- ADR-001: Adoção de Clean Architecture com DDD
- ADR-006: Estrutura de Pacotes
- ADR-010: Padronização de Exceções no Domínio
- Arquivo `fluxo-os-regras.md` — Regras de negócio detalhadas do fluxo de OS
