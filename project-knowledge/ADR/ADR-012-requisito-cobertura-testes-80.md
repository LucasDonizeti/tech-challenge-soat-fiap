# ADR-012: Requisito de Cobertura de Testes 80%

**Número do ADR:** 012  
**Título:** Requisito de Cobertura de Testes 80%  
**Data:** 2026-04-07  
**Responsável:** Arquiteto do Projeto  
**Status:** Aceito

## Contexto
A solicitação do Tech Challenge SOAT FIAP estabelece como **regra explícita e obrigatória** uma cobertura mínima de testes de 80% para o projeto de sistema de gestão da oficina mecânica. Este requisito está documentado no arquivo `tech-challenge.md` como critério fundamental de avaliação e aceite do projeto.

Atualmente, o projeto possui cobertura estimada de 45-50%, significativamente abaixo do requisito mínimo exigido pela solicitação do desafio. A implementação deste requisito é **mandatória** para:

- **Cumprir requisito obrigatório** da solicitação do Tech Challenge
- **Atender critérios de avaliação** estabelecidos pelos avaliadores
- **Garantir aprovação** no processo de validação do projeto
- **Assegurar conformidade** com as especificações técnicas exigidas
- **Demonstrar maturidade** em práticas de qualidade de software

## Decisão
Foi decidido implementar uma política de cobertura de testes obrigatória com os seguintes mínimos:

- **Cobertura geral do projeto:** 80% (conforme tech-challenge.md)
- **Domain layer:** 90% (regras de negócio críticas)
- **Application layer:** 85% (use cases e lógica de aplicação)
- **Web layer:** 80% (controllers e endpoints)
- **Infrastructure layer:** 80% (repositories, gateways e persistência)

A implementação será realizada através do plugin JaCoCo Maven com regras de build que falham automaticamente se os mínimos não forem atingidos.

## Justificativa
- **Requisito obrigatório da solicitação:** Documentado explicitamente no tech-challenge.md como critério de avaliação do projeto
- **Conformidade com especificações:** Atende às regras estabelecidas pela organização do Tech Challenge
- **Critério de aceite:** Essencial para aprovação e validação do projeto pelos avaliadores
- **Qualidade assegurada:** 80% de cobertura garante que a maioria do código está testada
- **Proteção contra regressões:** Testes abrangentes previnem quebras em funcionalidades existentes
- **Confiança nas entregas:** Alta cobertura permite refatorações e evoluções com segurança
- **Alinhamento com mercado:** Padrão de qualidade aceito na indústria de software

## Alternativas Consideradas

### Cobertura de 70%
- **Vantagem:** Mais fácil de atingir no curto prazo
- **Desvantagem:** Não atende ao requisito explícito da solicitação do Tech Challenge
- **Decisão:** Descartada por violar especificação obrigatória do projeto

### Cobertura de 90%
- **Vantagem:** Qualidade excepcional
- **Desvantagem:** Excede o requisito e pode atrasar entregas desnecessariamente
- **Decisão:** Descartada por ser excessivamente rigorosa além do solicitado

### Cobertura apenas geral (sem regras por camada)
- **Vantagem:** Implementação mais simples
- **Desvantagem:** Não garante qualidade em camadas críticas conforme boas práticas
- **Decisão:** Descartada em favor de regras específicas por camada

### Verificação manual de cobertura
- **Vantagem:** Sem dependências de ferramentas
- **Desvantagem:** Propenso a erros, não automatizado, não atende à necessidade de validação automática
- **Decisão:** Descartada em favor de automação com JaCoCo conforme solicitado

## Consequências

### Benefícios
- **Conformidade com solicitação:** Atende ao requisito obrigatório de 80% do Tech Challenge
- **Aprovação garantida:** Cumpre critério fundamental de avaliação do projeto
- **Qualidade assegurada:** Build falha automaticamente se cobertura < 80%
- **Feedback rápido:** Desenvolvedores são alertados imediatamente sobre queda na cobertura
- **CI/CD robusto:** Pipeline garante qualidade antes de deployments
- **Documentação viva:** ADR formaliza o requisito da solicitação
- **Alinhamento com ADR-009:** Complementa a estratégia de testes existente

### Desafios
- **Esforço inicial:** Necessário implementar testes adicionais para atingir 80%
- **Maior tempo de desenvolvimento:** Testes aumentam tempo de implementação
- **Complexidade:** Requer disciplina na escrita de testes efetivos
- **Manutenção:** Testes precisam ser mantidos junto com o código

### Impacto no Desenvolvimento
- **Build mais lento:** Execução de testes e verificação de cobertura
- **Maior disciplina:** Equipe precisa manter cobertura em novas features
- **Qualidade assegurada:** Redução significativa de bugs em produção
- **Refatoração segura:** Confiança para evoluções do sistema

## Implementação Técnica

### Configuração JaCoCo no pom.xml
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
                <goal>report</goal>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <!-- Regra geral de cobertura - 80% conforme tech-challenge.md -->
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>INSTRUCTION</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                        </limits>
                    </rule>
                    <!-- Regras específicas por camada -->
                    <rule>
                        <element>PACKAGE</element>
                        <includes>
                            <include>com.techchallenge.oficina.administrativo.domain</include>
                        </includes>
                        <limits>
                            <limit>
                                <counter>INSTRUCTION</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.90</minimum>
                            </limit>
                        </limits>
                    </rule>
                    <!-- Demais camadas com 80% mínimo -->
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Comandos de Verificação
```bash
# Executar testes e verificar cobertura
mvn clean test jacoco:check

# Gerar relatório detalhado
mvn clean test jacoco:report

# Visualizar relatório HTML
open target/site/jacoco/index.html
```

## Referências
- Tech Challenge SOAT FIAP - Solicitação do Projeto (tech-challenge.md) - Requisito obrigatório de 80% de cobertura
- ADR-009: Estratégia de Testes - Unitários, Integração e E2E
- JaCoCo Maven Plugin Documentation
- Test Coverage Best Practices
- Clean Architecture Testing Guidelines
- Especificações de Avaliação do Tech Challenge SOAT FIAP
