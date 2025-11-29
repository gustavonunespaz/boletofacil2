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

## Como visualizar
Abra `landing.html` no navegador para acessar o resumo do projeto. Ajustes visuais podem ser feitos em `landing.css`.

Para executar localmente via navegador com o Maven Wrapper:

1. Na raiz do projeto, rode `./mvnw spring-boot:run`.
2. Acesse `http://localhost:8080` para abrir o console web que chama os endpoints REST.
3. Use o formulário para informar o caminho absoluto do PDF a ser processado ou recarregue a tabela para ver os boletos salvos em memória.
4. Valide a saúde da aplicação em `http://localhost:8080/health`.
5. O Maven Wrapper fará o download automático do `maven-wrapper.jar` se não existir, evitando a necessidade de versionar binários.
