# ADR-016: Análise de Segurança de Dependências com OWASP Dependency Check

**Número do ADR:** 016  
**Título:** Análise de Segurança de Dependências com OWASP Dependency Check  
**Data:** 2026-04-22  
**Responsável:** Arquiteto do Projeto  
**Status:** Aceito

## Contexto
O Tech Challenge Fase 1 estabelece como entregável obrigatório um **relatório de análise de vulnerabilidades** do código e das dependências. Aplicações Spring Boot modernas carregam dezenas de dependências transitivas (Spring Framework, Hibernate, Jackson, Netty, etc.) que podem conter vulnerabilidades conhecidas (CVEs) listadas no banco de dados NVD (National Vulnerability Database).

Sem automação, a verificação manual de vulnerabilidades em dependências é impraticável e propensa a falhas. O projeto precisava de uma solução integrada ao build Maven que gerasse evidências auditáveis e que pudesse falhar o build quando vulnerabilidades críticas fossem detectadas.

## Decisão
Foi decidido adotar o **OWASP Dependency Check** (plugin Maven `dependency-check-maven` versão 12.2.0) com as seguintes configurações:

- **Integração Maven:** Plugin configurado no `pom.xml` com execução no goal `check`
- **Threshold de falha:** `<failBuildOnCVSS>7</failBuildOnCVSS>` — build falha para vulnerabilidades com CVSS ≥ 7.0 (High e Critical)
- **Formatos de relatório:** HTML, XML e JSON — permitindo visualização humana e integração com ferramentas externas
- **Arquivo de supressões:** `owasp-suppressions.xml` na raiz do projeto para suprimir falsos positivos documentados
- **Controle via propriedade Maven:** `<owasp.skip>true</owasp.skip>` — análise desabilitada por padrão no build local (lenta, consome dados NVD); habilitada explicitamente quando necessário

```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.owasp</groupId>
    <artifactId>dependency-check-maven</artifactId>
    <version>12.2.0</version>
    <executions>
        <execution>
            <goals><goal>check</goal></goals>
            <configuration>
                <skip>${owasp.skip}</skip>
            </configuration>
        </execution>
    </executions>
    <configuration>
        <suppressionFile>owasp-suppressions.xml</suppressionFile>
        <failBuildOnCVSS>7</failBuildOnCVSS>
        <formats>
            <format>HTML</format>
            <format>XML</format>
            <format>JSON</format>
        </formats>
    </configuration>
</plugin>
```

**Execução manual:**
```bash
# Habilitar análise explicitamente
mvn verify -Dowasp.skip=false

# Ou via goal direto
mvn dependency-check:check -Dowasp.skip=false
```

## Justificativa
- **Entregável obrigatório Fase 1:** O Tech Challenge exige "relatório com análise de vulnerabilidades encontradas no sistema" como PDF de entrega
- **Automatização do processo:** Integração no build Maven garante que análise possa ser executada de forma reproduzível e documentada
- **CVSS ≥ 7.0 como threshold:** Vulnerabilidades High e Critical são acionáveis imediatamente; Medium e Low são registradas no relatório mas não bloqueiam o desenvolvimento
- **Supressões documentadas:** `owasp-suppressions.xml` força que cada falso positivo ou vulnerabilidade suprimida seja justificada — cria trilha de auditoria
- **Desabilitado por padrão:** A análise OWASP consulta o banco NVD online e pode demorar vários minutos — manter `owasp.skip=true` por padrão preserva a velocidade do ciclo de desenvolvimento cotidiano
- **Múltiplos formatos de saída:** HTML para revisão manual, XML/JSON para integração com SonarQube e ferramentas de CI/CD

## Alternativas Consideradas

### Snyk
- **Vantagem:** Interface moderna, sugestões automáticas de upgrade, integração direta com GitHub PR checks
- **Desvantagem:** Free Tier limitado (400 testes/mês); requer conta e token de API para uso em CI/CD; não gera relatório PDF para entrega acadêmica de forma direta
- **Decisão:** Descartada pela limitação do free tier e necessidade de conta adicional

### GitHub Dependabot
- **Vantagem:** Nativo ao GitHub, abre PRs automáticos para upgrades de segurança
- **Desvantagem:** Não gera relatório auditável para entrega; não integra ao build Maven; apenas abre PRs — não bloqueia o build em vulnerabilidades críticas
- **Decisão:** Descartada por não atender ao requisito de relatório de entrega

### Trivy (Aqua Security)
- **Vantagem:** Analisa tanto código-fonte quanto imagens Docker; open source; muito rápido
- **Desvantagem:** Foco em imagens de container; menos preciso para análise de dependências Maven do que OWASP DC
- **Decisão:** Descartada; pode ser adicionada futuramente para análise de imagens Docker no pipeline

### Verificação manual do NVD
- **Vantagem:** Nenhuma dependência de ferramenta
- **Desvantagem:** Impossível para dezenas de dependências transitivas; propenso a omissões; sem trilha de auditoria
- **Decisão:** Descartada

## Consequências

### Benefícios
- Relatório auditável gerado como entregável obrigatório do Tech Challenge
- Build falha automaticamente para vulnerabilidades CVSS ≥ 7.0, prevenindo deploy de software com vulnerabilidades críticas conhecidas
- Falsos positivos gerenciados de forma explícita e documentada via `owasp-suppressions.xml`
- Múltiplos formatos de saída compatíveis com SonarQube e processos de auditoria

### Desafios
- Análise lenta (1–5 minutos dependendo do tamanho do classpath e conectividade com NVD)
- Banco NVD pode estar temporariamente indisponível — análise falha se não conseguir atualizar os dados
- Manutenção periódica do arquivo `owasp-suppressions.xml` quando novas versões de dependências forem adicionadas
- Dependências com versão gerenciada pelo Spring Boot parent (`<parent>`) podem reportar CVEs resolvidos internamente — requerem supressão documentada

### Impacto no Desenvolvimento
- Executar `mvn verify -Dowasp.skip=false` antes de cada release ou entrega de fase
- Revisar o relatório HTML gerado em `target/dependency-check-report.html`
- Documentar no arquivo `owasp-suppressions.xml` cada CVE suprimido com justificativa e data
- Manter dependências atualizadas para minimizar o número de CVEs reportados

## Referências
- OWASP Dependency Check: https://owasp.org/www-project-dependency-check/
- OWASP Dependency Check Maven Plugin: https://jeremylong.github.io/DependencyCheck/dependency-check-maven/
- NVD — National Vulnerability Database: https://nvd.nist.gov/
- CVSS v3 Scoring: https://www.first.org/cvss/v3-1/specification-document
- Tech Challenge SOAT FIAP — Requisito de relatório de vulnerabilidades
- ADR-013: Implementação de SonarQube para Análise Estática de Código
