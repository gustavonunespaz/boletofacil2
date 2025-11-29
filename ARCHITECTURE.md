# Boleto Fácil 2 – Visão de Arquitetura e Projeto (Java)

Este documento resume a visão arquitetural do Boleto Fácil 2, reimplementação do sistema em Java com foco em arquitetura limpa, testabilidade e extensibilidade. Ele consolida objetivos, escopo inicial e tecnologias planejadas para orientar o desenvolvimento.

## 1. Visão Geral

O sistema automatiza o processamento de boletos em PDF e a geração de arquivos para envio pelos Correios (PDF compilado, TXT e Excel). A reescrita em Java prioriza a separação de camadas, aplicando princípios SOLID e mantendo frameworks como detalhes de infraestrutura.

## 2. Objetivos do Sistema

### 2.1 Objetivos Funcionais
- Processar boletos em PDF, extraindo cliente, endereço, venda/parcela e vencimento.
- Gerar PDFs finais organizados em pastas por ano/mês.
- Registrar boletos em banco de dados e permitir consultas por período, cliente ou status.
- Gerar lotes para Correios com PDF compilado, TXT e planilha Excel.
- Permitir ajustes de endereço de boletos processados.

### 2.2 Objetivos Não Funcionais
- Código organizado em camadas (domínio, aplicação, infraestrutura e interface).
- Alto nível de legibilidade, manutenibilidade e cobertura de testes.
- Independência relativa de frameworks, mantendo o domínio livre de anotações.
- Empacotamento simples (JAR) e preparo para uso em contêineres.

## 3. Escopo Inicial (MVP)
- Backend Java + Spring Boot.
- Processamento de PDF com Apache PDFBox (ou equivalente).
- Geração de Excel com Apache POI.
- Banco de dados PostgreSQL (produção) e H2 (desenvolvimento/testes).
- Interface inicial REST ou CLI para acionar os casos de uso.

## 4. Estilo Arquitetural

Adota-se Clean Architecture/Hexagonal:
- Domínio independente de frameworks.
- Casos de uso (aplicação) orquestram operações.
- Infraestruturas plugáveis via interfaces (ports/adapters).
- Interfaces REST/CLI acessam os casos de uso.

### 4.1 Organização de Pacotes
```
com/nunespaz/boletofacil2/
  domain/
    entity/
    valueobject/
  application/
    usecase/
    port/
  infrastructure/
    pdf/
    persistence/
    file/
    config/
  interfaceadapters/
    rest/
    cli/
```

## 5. Modelo de Domínio

- **Boleto**: cliente, vendaParcela, dataVencimento, caminhos de PDF, status (NOVO, PROCESSADO, ENVIADO, CANCELADO).
- **Cliente**: nome e endereço.
- **Endereco** (Value Object): logradouro, número, complemento, bairro, CEP, cidade e estado.
- **LoteEnvio**: boletos incluídos, caminhos dos artefatos gerados e data de criação.

## 6. Casos de Uso Principais

### 6.1 ProcessarBoletoPdfUseCase
- Recebe o caminho de um PDF.
- Usa `PdfService` para extrair dados e gerar PDF processado em pasta organizada.
- Persiste o boleto via `BoletoRepository` e retorna o resultado.

### 6.2 GerarLoteCorreiosUseCase
- Recebe IDs ou critérios para buscar boletos.
- Usa `PdfService` para mesclar PDFs, `PlanilhaService` para gerar Excel e `StorageService` para gerar TXT.
- Cria e salva `LoteEnvio` via `LoteEnvioRepository`.

### 6.3 AtualizarEnderecoBoletoUseCase
- Recebe o ID do boleto e novos dados de endereço.
- Atualiza o valor-objeto `Endereco`, regenera o PDF e persiste o boleto atualizado.

## 7. Tecnologias
- Java 17 ou 21.
- Spring Boot (Web, Data JPA), PostgreSQL (prod) e H2 (dev/teste).
- Apache PDFBox (PDF) e Apache POI (Excel).
- Maven para build e JUnit 5 + Mockito para testes.

## 8. Plano de Implementação

1. **Núcleo em Java sem framework**: entidades, serviços fake e casos de uso básicos com testes.
2. **PDF real**: integração PDFBox, parsing de dados e geração de PDFs.
3. **Excel e TXT**: serviços com POI e storage, fechando o fluxo de lote.
4. **Spring Boot + Banco**: adapters JPA e endpoints REST para processar boletos e gerar lotes.
5. **Interface de Usuário**: front-end Web ou JavaFX para upload, listagem e geração de lotes.

## 9. Diretrizes de Qualidade
- Aplicar SOLID, princípios ACID ao lidar com persistência e Código Limpo.
- Manter domínio isolado de detalhes de infraestrutura.
- Garantir testabilidade dos casos de uso e serviços.

