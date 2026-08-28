# 📚 StudyMais

> Transforme sua rotina de estudos com organização, foco e clareza.

O **StudyMais** é uma aplicação web desenvolvida para ajudar estudantes a organizarem sua rotina de forma eficiente. Com ele, você pode gerenciar suas matérias, definir e acompanhar metas, e organizar suas tarefas diárias com praticidade.

---

## 🚀 Sobre o Projeto

Muitos estudantes enfrentam dificuldades para manter a consistência nos estudos por falta de planejamento e clareza sobre suas prioridades. O **StudyMais** oferece uma interface simples e funcional integrada a uma **API backend** para persistência e gerenciamento seguro dos dados acadêmicos.

### ✨ Principais Funcionalidades

- 📁 **Organização por Matérias:** Cadastre e categorize suas disciplinas para manter o conteúdo estruturado.
- 🎯 **Definição de Metas:** Estabeleça metas de estudo diárias, semanais ou mensais para se manter motivado.
- 📋 **Gestão de Tarefas:** Crie, edite e conclua tarefas associadas a cada matéria (leituras, exercícios, revisões, etc.).
- 📊 **Acompanhamento de Progresso:** Visualize seu avanço e mantenha o foco nos seus objetivos acadêmicos.
- 💡 **Rotina Personalizada:** Adapte o cronograma de estudos de acordo com o seu ritmo e necessidade.

---

## 🛠️ Tecnologias Utilizadas

### **Frontend (Aplicação Web)**
- **HTML5 & CSS3** — Estrutura e estilização da interface.
- **JavaScript (ES6+)** — Consumo da API REST e manipulação dinâmica do DOM.

### **Backend (API RESTful)**
- **Java** — Linguagem principal do backend.
- **Spring Boot** — Framework para criação dos endpoints REST e regras de negócio.
- **Banco de Dados (PostgreSQL/Supabase)** — Armazenamento das matérias, metas e tarefas.
- **Maven** — Gerenciamento de dependências.

---

## 🔗 Repositórios do Projeto

- 🖥️ **Frontend:** [StudyMais Web](https://github.com/fedeveloper009/StudyMais) (Em breve)
- ⚙️ **Backend / API:** [StudyMais API (https://github.com/fedeveloper009/StudyMais)](https://github.com/fedeveloper009/StudyMais)

---

## 🏁 Como Executar o Projeto

### 1. Backend (API Java)
1. Clone o repositório da API:
   ```bash
   git clone [https://github.com/fedeveloper009/StudyMais](https://github.com/fedeveloper009/StudyMais)
   ```

## Autenticacao e integracao

O cadastro e o login sao publicos. As demais rotas `/api/**` exigem um token JWT.

### Cadastrar usuario

```http
POST /api/usuarios
Content-Type: application/json
```

```json
{
  "nome": "Carlos",
  "email": "carlos@email.com",
  "senha": "123456"
}
```

### Fazer login

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "email": "carlos@email.com",
  "senha": "123456"
}
```

A resposta contem um campo `token`. Envie-o nas demais requisicoes:

```http
Authorization: Bearer SEU_TOKEN
```

As origens permitidas pelo CORS sao configuradas em `CORS_ALLOWED_ORIGINS`, separadas por virgulas. Por padrao, a API aceita as portas locais mais comuns dos front-ends.
