# Segurança e Autenticação de APIs

Este documento detalha o fluxo de segurança, autenticação e autorização implementados para as APIs do **Sistema de Gestão de Oficina**.

---

## 🔐 Como Funciona a Autenticação

A segurança do sistema é implementada utilizando o **Spring Security** com autenticação **JWT (JSON Web Token)** sem estado (stateless).

```
[Cliente] ── POST /v1/auth/login ──> [API]
[Cliente] <─── Retorna Token JWT ──── [API]

[Cliente] ── GET /v1/admin/... + Token ─> [API (Valida Token)]
[Cliente] <─── Retorna Dados Protegidos ── [API]
```

- A API é protegida e exige autenticação para a maioria dos endpoints administrativos.
- Tokens gerados têm um tempo de expiração padrão de **24 horas (86400 segundos)**.
- O algoritmo de assinatura utilizado é o **HMAC-SHA256 (HS256)**.

---

## 👤 Credenciais Padrão de Administrador

Para testes locais e primeiro acesso, a aplicação inicia com as seguintes credenciais padrão:

- **Usuário:** `admin`
- **Senha:** `secret123`
- **Role:** `ADMIN`

---

## 🛠️ Passo a Passo para Autenticação

### 1. Obter o Token JWT (Login)

Envie uma requisição POST para o endpoint `/v1/auth/login`:

```http
POST /v1/auth/login HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "username": "admin",
  "password": "secret123"
}
```

**Resposta de Sucesso (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcyMDIzNDU2MCwiZXhwIjoxNzIwMzIwOTYwfQ.xyz...",
  "type": "Bearer",
  "username": "admin"
}
```

### 2. Usar o Token em Endpoints Protegidos

Para consumir endpoints protegidos, inclua o token obtido no cabeçalho `Authorization` de cada requisição no formato `Bearer <token>`:

```http
GET /v1/admin/clientes HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcyMDIzNDU2MCwiZXhwIjoxNzIwMzIwOTYwfQ.xyz...
```

---

## 🌐 Mapeamento de Endpoints

### 🟢 Endpoints Públicos (Sem Autenticação)

Os seguintes endpoints são públicos e podem ser acessados livremente (por exemplo, pelo cliente final ou para monitoramento):

- **Autenticação:** `/v1/auth/**` (Login e registro)
- **Ordens de Serviço:** `/v1/os/**` (Endpoints de acompanhamento de OS públicos)
- **Monitoramento e Saúde:**
  - `/v1/admin/health` (Endpoint público de saúde)
  - `/actuator/**` (Métricas e health checks do Spring Boot Actuator)
- **Documentação de API:**
  - `/swagger-ui/**` (Painel interativo do Swagger UI)
  - `/v3/api-docs/**` (Especificação OpenAPI JSON)

### 🔴 Endpoints Protegidos (Requer Autenticação ADMIN)

Todos os endpoints administrativos que realizam modificações ou listagens gerais de dados exigem o perfil de `ADMIN`:

- **Administrativo Geral:** `/v1/admin/**` (ex: Cadastro e edição de clientes, veículos, peças, insumos e listagem completa de ordens de serviço).

---

## ⚙️ Configurações do JWT (Spring Boot)

As configurações do JWT podem ser ajustadas no arquivo [../oficina/src/main/resources/application.yaml](../oficina/src/main/resources/application.yaml):

```yaml
security:
  jwt:
    secret: chavesecreta              # Chave de assinatura (alterar em produção)
    expiration: 86400                 # Tempo de expiração (24 horas em segundos)
```
