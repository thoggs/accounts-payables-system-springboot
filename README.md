# Accounts Payables

<p align="center" width="100%">
    <img width="22%" src="https://www.vectorlogo.zone/logos/springio/springio-ar21.svg" alt="Spring Boot Logo">
</p>

## Descrição do Projeto

Este projeto é uma API RESTful utilizando Spring Boot e Java 21. Ele inclui a configuração inicial do projeto, modelos
básicos e endpoints para manipulação de dados em um banco de dados PostgreSQL.

## Estrutura do Projeto

```
project-root/
├── application/
│   ├── config/
│   ├── controllers/
│   ├── dtos/
│   ├── mappers/
│   ├── services/
│   └── validators/
├── domain/
│   ├── inbound/
│   ├── models/
│   └── outbound/
├── infrastructure/
│   ├── adapters/
│   │   ├── inbound/
│   │   ├── outbound/
│   └── persistence/
├── shared/
│   ├── enums/
│   ├── exceptions/
│   └── parsers/
├── db.migration/
│   ├── V1__create_tables.sql
│   ├── V2__create_accounts_tables.sql
└── application.properties
```

## Pré-requisitos

- Java 21
- Gradle 8.8
- Banco de dados PostgreSQL
- Docker (apenas para inicialização rápida)
- Docker Compose (apenas para inicialização rápida)

## Inicialização Rápida

Subir o projeto com Docker:

### 1. Clone o repositório e execute o docker compose:

```bash
git clone https://github.com/thoggs/accounts-payables-system-springboot.git && cd accounts-payables-system-springboot && docker compose up -d
```

### 2. Acesse o Projeto:

> O projeto estará disponível em http://localhost:8080/api/{endpoint}

## Descrição da API

### Endpoints:

### **Auth** (Autenticação)

- **POST /auth/signin**: autentica um usuário e retorna dados do usuário e um token de acesso.
- **POST /auth/signup**: registra um novo usuário e retorna dados do usuário e um token de acesso.
- **POST /auth/github-signin**: autentica um usuário com o GitHub e retorna dados do usuário e um token de acesso.

#### JSON Body Exemplo para `/auth/signup` e `/auth/signin`:

- **POST /auth/signup**:

```json
{
  "firstName": "YOUR_FIRST_NAME",
  "lastName": "YOUR_LAST_NAME",
  "email": "YOUR_EMAIL",
  "password": "YOUR_PASSWORD",
  "role": "user"
}
```

Roles disponíveis: `user`, `admin`, `manager`, `guest`.

- **POST /auth/signin**:

```json
{
  "email": "YOUR_EMAIL",
  "password": "YOUR_PASSWORD"
}
```

- **POST /auth/github-signin**:

```json
{
  "githubToken": "YOUR_GITHUB_TOKEN"
}
```

### **Users** (Usuários)

- **GET /api/users**: retorna uma lista paginada de todos os usuarios registrados. É possível personalizar a
  página e a quantidade de resultados exibidos na lista adicionando os seguintes parâmetros à URL:
    - **page**: número da página a ser exibida.
        - Exemplo: `http://localhost:8080/api/users?page=2` exibe a segunda página de resultados.

    - **pageSize**: quantidade de resultados exibidos por página.
        - Exemplo: `http://localhost:8080/api/users?perPage=5&page=3` exibe a terceira página com até 5
          usuários por página.

    - **searchTerm**: termo de pesquisa para filtrar resultados.
        - Será executado `LIKE` no banco de dados pelo termo informado.
        - Exemplo: `http://localhost:8080/api/users?searchTerm=John` filtra resultados contendo "John".
    - **sorting**: ordena os resultados por uma coluna específica.
        - Exemplo: `http://localhost:8080/api/users?sorting=sorting=[{"id":"firstName","desc":false}]`

- **GET /api/users/{id}**: retorna informações detalhadas sobre um usuário específico.

- **POST /api/users**: cria um novo registro de usuário.

- **PUT /api/users/{id}**: atualiza as informações de um usuário existente.

- **DELETE /api/users/{id}**: exclui um registro de usuário existente.

### **Accounts** (Gerenciamento de Contas)

- **GET /api/accounts**: retorna uma lista paginada de todas as contas registradas. É possível personalizar a
  página e a quantidade de resultados exibidos na lista adicionando os seguintes parâmetros à URL:
    - **page**: número da página a ser exibida.
        - Exemplo: `http://localhost:8080/api/accounts?page=2` exibe a segunda página de resultados.

    - **pageSize**: quantidade de resultados exibidos por página.
        - Exemplo: `http://localhost:8080/api/accounts?page=2&pageSize=10` exibe a segunda página com até 10 contas.

    - **searchTerm**: termo de pesquisa para filtrar resultados.
        - Será executado `LIKE` no banco de dados pelo termo informado.
        - Exemplo: `http://localhost:8080/api/accounts?searchTerm=Payment` filtra resultados contendo "Payment".
    - **sorting**: ordena os resultados por uma coluna específica.
        - Exemplo: `http://localhost:8080/api/accounts?sorting=[{"id":"dueDate","desc":true}]`
    - **startDate**: data inicial para filtrar contas com vencimento após esta data.
        - Exemplo: `http://localhost:8080/api/accounts?startDate=2025-01-01` filtra resultados após o dia 1º de janeiro
          de 2025.
    - **endDate**: data final para filtrar contas com vencimento antes desta data.
        - Exemplo: `http://localhost:8080/api/accounts?endDate=2025-12-31` filtra resultados até o dia 31 de dezembro de
          2025.

- **GET /api/accounts/{id}**: retorna informações detalhadas sobre uma conta específica.

- **POST /api/accounts**: cria um novo registro de conta.

- **PUT /api/accounts/{id}**: atualiza as informações de uma conta existente.

- **DELETE /api/accounts/{id}**: exclui um registro de conta existente.

- **PATCH /api/accounts/{id}/status**: atualiza o status de uma conta existente.

- **GET /api/accounts/total-paid**: calcula o total pago dentro de um período especificado.

- **POST /api/accounts/import**: importa registros de contas a partir de um arquivo CSV.

#### **Exemplo de Arquivo CSV para Importação**

Para importar registros de contas através do endpoint `/api/accounts/import`, o arquivo CSV deve seguir o seguinte formato:

```csv
2025-01-09,2025-01-14,500.75,Payment for January utilities,PAID
2025-02-10,2025-02-15,300.50,Payment for February rent,PENDING
2025-03-11,,200.00,Payment for March services,UNPAID
```

### **Health** (Status)

- **GET /health/check**: retorna o status da aplicação.

## Estrutura de Resposta

### **Resposta de Sucesso**:

```json
{
  "data": [
    {
      "id": "73002090-73b7-4461-b7d6-b66fc64c7096",
      "dueDate": "2025-01-09",
      "paymentDate": "2025-01-14",
      "amount": 500.75,
      "description": "Payment for January utilities",
      "status": "PAID"
    }
  ],
  "success": true,
  "metadata": {
    "pagination": {
      "currentPage": 1,
      "itemsPerPage": 10,
      "totalItems": 101,
      "totalPages": 11
    },
    "messages": [
      {
        "errorCode": "INFO",
        "errorMessage": "Operation completed successfully.",
        "field": null
      }
    ]
  }
}
```

### **Resposta de Erro**:

```json
{
  "data": [],
  "success": false,
  "metadata": {
    "messages": [
      {
        "errorCode": "RESOURCE_NOT_FOUND",
        "errorMessage": "Resource not found in the database",
        "field": null
      }
    ]
  }
}
```

## Configuração para Desenvolvimento

### Passo 1: Clonar o Repositório

```bash
git clone https://github.com/thoggs/accounts-payables-system-springboot.git && cd accounts-payables-system-springboot
```

### Passo 2: Instalar Dependências

```bash
./gradlew build
```

### Passo 3: Configurar Variáveis de Ambiente

Crie ou edite o arquivo `.env` na raiz do projeto e adicione suas configurações do banco de dados:

```env
JWT_SECRET=mysecretkey
JWT_EXPIRATION_TIME=86400000
GITHUB_API_URL=api.github.com
GITHUB_USER_PATH=/user
GITHUB_SCHEME=https
```

### Passo 5: Rodar o Projeto

#### Para rodar o projeto em modo de desenvolvimento:

```bash
./gradlew bootRun
```

#### Para rodar o projeto em modo de produção:

```bash
./gradlew build && java -jar build/libs/accounts-payables-system-springboot-0.0.1-SNAPSHOT.jar
```

## Tecnologias Utilizadas

- **Spring Boot**: Framework para criação de aplicações Java
- **Hibernate JPA**: ORM para mapeamento objeto-relacional
- **PostgreSQL**: Banco de dados relacional
- **Spring Security**: Framework para segurança e autenticação
- **Spring Web**: Framework para desenvolvimento web e APIs REST
- **Spring Data JPA**: Abstração para simplificar o acesso a dados com JPA
- **Spring Batch**: Framework para processamento em lote
- **java-jwt**: Biblioteca para geração e validação de tokens JWT
- **Spring Boot Starter Test**: Dependência para testes unitários e de integração
- **Docker**: Plataforma de contêineres
- **Gradle**: Ferramenta de automação de build e gerenciamento de dependências

## License

Project license [Apache-2.0](https://opensource.org/license/apache-2-0)
