# Boleto Fácil 2

Reescrita do Boleto Fácil em Java, priorizando arquitetura limpa, testabilidade e extensibilidade. Este repositório reúne a visão de alto nível do projeto e um landing page simples para apresentar objetivos e tecnologias.

## O que contém
- **Landing page** (`landing.html` + `landing.css`) apresentando MVP, arquitetura e stack tecnológica.
- **Documento de arquitetura** (`ARCHITECTURE.md`) detalhando objetivos funcionais, camadas propostas, casos de uso e plano de implementação.
- **Esqueleto Java com Maven** (`src/main/java`) seguindo a organização da arquitetura limpa e casos de uso iniciais.

## Próximos passos
- Evoluir adapters (REST/JPA) a partir das portas definidas em aplicação.
- Integrar serviços reais de PDF e persistência seguindo as interfaces já criadas.
- Configurar pipeline de testes automatizados (JUnit/Mockito) antes de evoluir para Spring Boot.

## Registro de alterações
- Corrigido o preenchimento da área de endereço nos PDFs gerados para usar cor branca, evitando a aparição de retângulos pretos sobre o conteúdo original.
- Inclusão do valor do boleto na extração e exibição do console web, permitindo visualizar montante e vencimento na lista de boletos processados.
- Ajustada a heurística de leitura de valor para priorizar montantes mais próximos ao campo "Nosso número", reduzindo falhas de captura em PDFs com múltiplos valores.

## Como visualizar
Abra `landing.html` no navegador para acessar o resumo do projeto. Ajustes visuais podem ser feitos em `landing.css`.

Para executar localmente via navegador com o Maven Wrapper:

1. Na raiz do projeto, rode `./mvnw spring-boot:run`.
2. Acesse `http://localhost:8080/health` para validar se o backend está respondendo.
3. Utilize o console web em `http://localhost:8080/` para selecionar um ou vários PDFs locais (via diálogo do navegador) e processá-los de uma só vez.
4. Utilize o endpoint como base para conectar futuras páginas ou integrações.
5. O Maven Wrapper fará o download automático do `maven-wrapper.jar` se não existir, evitando a necessidade de versionar binários.
