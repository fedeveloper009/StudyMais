# 📚 StudyMais API

> API REST para gerenciamento e organização de estudos, desenvolvida com Java e Spring Boot.

O **StudyMais** é uma plataforma criada para ajudar estudantes a organizar sua rotina acadêmica, permitindo o gerenciamento de **usuários, matérias e tarefas** em um único sistema.

Esta API representa o **backend da aplicação**, sendo responsável pelas regras de negócio, persistência dos dados, validações e comunicação com o banco de dados.

---

## 🚀 Sobre o projeto

A rotina de estudos pode envolver diversas matérias, tarefas e atividades diferentes. O StudyMais busca centralizar essas informações para tornar o planejamento mais simples e organizado.

A API fornece uma estrutura REST para que o frontend possa:

* 👤 Gerenciar usuários
* 📚 Cadastrar e administrar matérias
* ✅ Criar e gerenciar tarefas
* 🔐 Realizar autenticação e controle de acesso
* 🗄️ Persistir os dados em PostgreSQL
* ✔️ Validar informações recebidas pelas requisições
* ⚠️ Tratar erros e exceções da aplicação

---

## 🏗️ Arquitetura

A aplicação segue uma arquitetura baseada na separação de responsabilidades:

```text
src/
└── main/
    └── java/
        └── com/
            └── projeto/
                └── studymais/
                    ├── controller/
                    ├── service/
                    ├── repository/
                    ├── model/
                    ├── dto/
                    ├── exception/
                    ├── security/
                    └── StudyMaisApplication.java
```

### Responsabilidade das camadas

| Camada                 | Responsabilidade                                 |
| ---------------------- | ------------------------------------------------ |
| `controller`           | Recebe e responde às requisições HTTP            |
| `service`              | Contém as regras de negócio                      |
| `repository`           | Comunicação com o banco através do JPA           |
| `model`                | Entidades e estruturas persistidas               |
| `dto`                  | Objetos utilizados para entrada e saída de dados |
| `exception`            | Tratamento das exceções da aplicação             |
| `security`             | Autenticação e autorização                       |
| `StudyMaisApplication` | Inicialização da aplicação                       |

Essa divisão facilita a manutenção, os testes e a evolução da API.

---

## 🛠️ Tecnologias

### Backend

* ☕ **Java 21**
* 🌱 **Spring Boot 4.1.1**
* 🌐 **Spring Web MVC**
* 🗃️ **Spring Data JPA**
* 🔐 **Spring Security**
* 🔑 **JWT — JSON Web Token**
* ✅ **Bean Validation**
* 🐘 **PostgreSQL**
* 📦 **Maven**

O projeto utiliza Java 21 e possui dependências para JPA, Security, Validation, Web MVC, PostgreSQL e JJWT no `pom.xml`.

---

## 🔐 Autenticação

A API utiliza **Spring Security** juntamente com **JWT** para trabalhar com autenticação baseada em tokens.

O fluxo esperado é:

```text
Cliente
   │
   │ Login
   ▼
API
   │
   │ Validação das credenciais
   ▼
JWT
   │
   │ Token
   ▼
Cliente
   │
   │ Requisição autenticada
   │ Authorization: Bearer <token>
   ▼
API
```

Isso permite proteger endpoints que exigem um usuário autenticado.

---

## 📌 Principais recursos

### 👤 Usuários

Responsável pelo gerenciamento das informações dos usuários da plataforma.

Operações previstas:

```http
POST   /usuarios
GET    /usuarios
GET    /usuarios/{id}
PUT    /usuarios/{id}
DELETE /usuarios/{id}
```

### 📚 Matérias

Permite organizar os estudos por disciplinas ou matérias.

Operações:

```http
POST   /materias
GET    /materias
GET    /materias/{id}
PUT    /materias/{id}
DELETE /materias/{id}
```

### ✅ Tarefas

Permite criar atividades relacionadas à rotina de estudos.

Exemplos:

* exercícios;
* trabalhos;
* revisões;
* leituras;
* atividades acadêmicas.

Operações:

```http
POST   /tarefas
GET    /tarefas
GET    /tarefas/{id}
PUT    /tarefas/{id}
DELETE /tarefas/{id}
```

> **Observação:** os endpoints acima devem ser ajustados caso os `@RequestMapping` e `@GetMapping`, `@PostMapping`, etc. presentes nos controllers utilizem caminhos diferentes.

---

## 🗄️ Banco de dados

O StudyMais utiliza **PostgreSQL** como banco de dados.

A comunicação com o banco é realizada através do:

```text
Spring Data JPA
       │
       ▼
Hibernate
       │
       ▼
PostgreSQL
```

O projeto possui o driver oficial do PostgreSQL configurado como dependência Maven.

### Configuração

Crie as variáveis de ambiente necessárias para a conexão com o banco.

Exemplo:

```properties
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}
```

**Não coloque senhas ou chaves privadas diretamente no repositório.**

---

## ▶️ Como executar

### Pré-requisitos

Antes de executar a aplicação, tenha instalado:

* Java 21 ou superior
* Maven
* PostgreSQL
* Git

### 1. Clone o projeto

```bash
git clone https://github.com/fedeveloper009/StudyMais.git
```

### 2. Entre na pasta

```bash
cd StudyMais
```

### 3. Configure o banco de dados

Configure as credenciais do PostgreSQL de acordo com o arquivo de configuração da aplicação.

### 4. Execute a aplicação

No Windows:

```bash
./mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Ou execute diretamente pela IDE através da classe:

```text
StudyMaisApplication
```

---

## 🧪 Testando a API

A API pode ser testada utilizando ferramentas como:

* **Postman**
* **Insomnia**
* **cURL**
* frontend do StudyMais

### Exemplo de requisição

```http
GET http://localhost:8080/materias
```

Exemplo de resposta:

```json
[
  {
    "id": 1,
    "nome": "Programação"
  }
]
```

> Os exemplos de JSON devem ser mantidos sincronizados com os DTOs reais da aplicação.

---

## 📂 Estrutura do projeto

```text
StudyMais/
│
├── .mvn/
│   └── wrapper/
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/projeto/studymais/
│   │           ├── controller/
│   │           ├── dto/
│   │           ├── exception/
│   │           ├── model/
│   │           ├── repository/
│   │           ├── security/
│   │           ├── service/
│   │           └── StudyMaisApplication.java
│   │
│   └── test/
│       └── java/
│           └── com/projeto/studymais/
│
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

---

## 🧩 Validação e tratamento de erros

A API utiliza recursos do Spring para validar os dados recebidos pelas requisições.

O objetivo é impedir que informações inválidas sejam persistidas e fornecer respostas HTTP adequadas para diferentes situações.

Exemplos:

| Status                      | Significado                                   |
| --------------------------- | --------------------------------------------- |
| `200 OK`                    | Requisição realizada com sucesso              |
| `201 Created`               | Recurso criado                                |
| `204 No Content`            | Operação realizada sem conteúdo para retornar |
| `400 Bad Request`           | Dados enviados são inválidos                  |
| `401 Unauthorized`          | Usuário não autenticado                       |
| `403 Forbidden`             | Usuário não possui permissão                  |
| `404 Not Found`             | Recurso não encontrado                        |
| `500 Internal Server Error` | Erro inesperado no servidor                   |

---

## 🔄 Fluxo da aplicação

```text
             ┌──────────────┐
             │   Frontend   │
             └──────┬───────┘
                    │
                 HTTP/JSON
                    │
                    ▼
             ┌──────────────┐
             │  Controller  │
             └──────┬───────┘
                    │
                    ▼
             ┌──────────────┐
             │   Service    │
             └──────┬───────┘
                    │
                    ▼
             ┌──────────────┐
             │  Repository  │
             └──────┬───────┘
                    │
                    ▼
             ┌──────────────┐
             │  PostgreSQL  │
             └──────────────┘
```

---

## 🎯 Objetivos do projeto

O StudyMais também funciona como um projeto de aprendizado e aplicação prática de conceitos de desenvolvimento backend, incluindo:

* Desenvolvimento de APIs REST
* Programação Orientada a Objetos
* Arquitetura em camadas
* Spring Boot
* Spring Data JPA
* Spring Security
* Autenticação com JWT
* DTOs
* Validação de dados
* Tratamento de exceções
* Persistência com PostgreSQL
* Testes de API
* Versionamento com Git e GitHub

---

## 🗺️ Próximos passos

Algumas funcionalidades que podem ser adicionadas futuramente:

* [ ] Documentação com Swagger/OpenAPI
* [ ] Testes unitários dos Services
* [ ] Testes de integração
* [ ] Paginação de resultados
* [ ] Filtros e ordenação
* [ ] Melhorias no sistema de autenticação
* [ ] Refresh Token
* [ ] Dockerização da aplicação
* [ ] CI/CD com GitHub Actions
* [ ] Deploy da API
* [ ] Integração completa com o frontend

---

## 📖 Documentação da API

A documentação dos endpoints pode ser expandida futuramente com **Swagger/OpenAPI**, permitindo visualizar e testar as requisições diretamente pelo navegador.

---

## 👨‍💻 Desenvolvimento

Projeto desenvolvido como uma aplicação voltada à organização de estudos e à prática de desenvolvimento de APIs REST com Java e Spring Boot.

---

## 📄 Licença

Este projeto ainda não possui uma licença definida.

Se o projeto for disponibilizado como código aberto, recomenda-se adicionar uma licença adequada, como MIT, de acordo com os objetivos do projeto.
