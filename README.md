
# Florescer - Sistema de Vendas e Entregas de Lembranças

Este é um sistema de vendas e entregas de lembranças, desenvolvido com o framework **Spring Boot**. Ele inclui funcionalidades de autenticação, persistência de dados, documentação de API e migrações de banco de dados.

## Tecnologias Utilizadas

- **Spring Boot 3.1.1**: Framework principal para construção da aplicação.
- **Java 17**: Versão do JDK utilizada.
- **Spring Data JPA**: Para acesso ao banco de dados.
- **MySQL**: Banco de dados relacional.
- **Flyway**: Ferramenta para gerenciamento de migrações de banco de dados.
- **Spring Security**: Para autenticação e controle de acesso.
- **JWT (JSON Web Tokens)**: Para autenticação baseada em token.
- **SpringDoc OpenAPI**: Para gerar e documentar a API automaticamente.
- **Jacoco**: Para análise de cobertura de testes.

## Requisitos

- Java 17
- MySQL 8.0 ou superior
- Maven 3.8.1 ou superior

## Configuração do Banco de Dados

A aplicação utiliza **MySQL** como banco de dados. A configuração do banco de dados pode ser definida no arquivo `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/florescer
spring.datasource.username=root
spring.datasource.password=senha
spring.jpa.hibernate.ddl-auto=update
spring.flyway.enabled=true

