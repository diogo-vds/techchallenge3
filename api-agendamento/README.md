# TECHCHALLENGE FASE 3 - Módulo de Agendamento

---

## COMO EXECUTAR O PROJETO

### **Pré-requisitos**
```bash
✅ Java 17 ou superior
✅ Maven 3.6+
✅ IDE (IntelliJ IDEA, Eclipse, VS Code)
✅ Postman (opcional, para testes)
```

### **Passo 1: Clonar/Verificar Projeto**
```bash
https://github.com/diogo-vds/techchallenge3
```

### **Passo 2: Compilar Projeto**
```bash
mvn clean compile
```

### **Passo 3: Executar Testes**
```bash
mvn test
# Resultado esperado: BUILD SUCCESS
```

### **Passo 4: Executar Aplicação**
```bash
mvn spring-boot:run
# Resultado esperado: Tomcat started on port 8080
```

### **Passo 5: Testar API**
```bash
# Opção 1: Via Postman
# Importar arquivo: src/main/resources/TechChallenge Fase3.postman_collection.json

# Opção 2: Via cURL
curl -X GET http://localhost:8080/v1/agendamentos
```

---

## 📊 ENDPOINTS DISPONÍVEIS

### **Endpoints Principais**
```
✅ POST   /v1/agendamentos                    - Criar consulta
✅ GET    /v1/agendamentos                    - Listar consultas
✅ GET    /v1/agendamentos/{id}               - Buscar por ID
✅ PUT    /v1/agendamentos/{id}               - Atualizar consulta
✅ DELETE /v1/agendamentos/{id}               - Deletar consulta
✅ POST   /v1/agendamentos/{id}/reagendar     - Reagendar consulta
✅ GET    /v1/agendamentos/{id}/reagendamentos- Ver histórico reagendamentos
✅ POST   /v1/agendamentos/{id}/cancelar      - Cancelar consulta
```

### **Endpoints de Teste (Postman)**
```
🧪 Criar consulta fora horário comercial
🧪 Criar consulta no passado
🧪 Criar consulta em sábado
🧪 Reagendar com menos de 24h
🧪 Cancelar com menos de 2h
```

---
###  **AUTENTICAÇÃO JWT**

A API utiliza:

- Spring Security
- JWT Stateless Authentication

Todos os endpoints de agendamento são protegidos.

---

# COMO USAR TOKEN JWT

Após obter o token, envie no header:

Authorization: Bearer SEU_TOKEN

Exemplo:

GET /v1/agendamentos
Authorization: Bearer eyJhbGciOi...
---

## 🎯 VALIDAÇÕES IMPLEMENTADAS

### **Regras de Negócio**
1. ✅ **Data no passado** - Rejeita agendamentos passados
2. ✅ **Horário comercial** - Apenas 08:00-18:00, segunda-sexta
3. ✅ **Profissional ativo** - Validação implementada
4. ✅ **Conflito profissional** - Mesmo horário = erro
5. ✅ **Mesmo horário paciente** - Paciente não pode ter 2 simultâneas
6. ✅ **Limite simultâneas** - Máximo 3 consultas ativas por paciente
7. ✅ **Antecedência reagendamento** - Mínimo 24 horas
8. ✅ **Limite reagendamentos** - Máximo 3 por consulta
9. ✅ **Antecedência cancelamento** - Mínimo 2 horas

---

## 🧪 COMO TESTAR

### **Teste Básico - Criar Consulta**
```bash
curl -X POST http://localhost:8080/v1/agendamentos \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": "11111111-1111-1111-1111-111111111111",
    "profissionalId": "22222222-2222-2222-2222-222222222222",
    "dataHora": "2026-05-15T14:30:00",
    "descricao": "Consulta de rotina"
  }'
```

### **Teste de Validação - Fora Horário**
```bash
curl -X POST http://localhost:8080/v1/agendamentos \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": "11111111-1111-1111-1111-111111111111",
    "profissionalId": "22222222-2222-2222-2222-222222222222",
    "dataHora": "2026-05-15T22:00:00",
    "descricao": "Consulta fora horário"
  }'
# Resultado esperado: 400 Bad Request
```

### **Teste Reagendamento**
```bash
# 1. Criar consulta primeiro
# 2. Reagendar
curl -X POST http://localhost:8080/v1/agendamentos/{id}/reagendar \
  -H "Content-Type: application/json" \
  -d '{
    "novaDataHora": "2026-05-20T15:00:00",
    "motivo": "Conflito de horário"
  }'
```

---

## 🔧 BANCO DE DADOS

### **Configuração**
- **Banco:** H2 (provisório)
- **Console:** http://localhost:8080/h2-console
- **JDBC URL:** jdbc:h2:mem:agendamento-db
- **User:** sa
- **Password:** (vazio)

### **Tabelas Criadas**
```sql
- consultas (agendamentos)
- reagendamentos (histórico)
```

---

## 🎯 ARQUITETURA

### **Clean Architecture Implementada**
```
Domain Layer     → Regras de negócio
Application Layer → Use Cases
Adapter Layer     → Controllers, Repositories
Infrastructure    → Database, Configuration
```

### **Tecnologias**
- **Framework:** Spring Boot 3.5.13
- **Banco:** H2 Database
- **ORM:** JPA/Hibernate
- **Testes:** JUnit 5 + Mockito
- **Documentação:** Postman Collection

---


