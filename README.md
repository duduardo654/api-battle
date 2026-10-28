# API FIFA – API Battle (Arquiteturas de Software 2026/B)

API REST em **Java 21 + Spring Boot 3 + PostgreSQL** com os 5 endpoints do enunciado.

| Método | Rota | Descrição |
|---|---|---|
| POST | `/fifa` | insere um jogador (JSON no formato do CSV) |
| GET | `/fifa/count` | quantidade de jogadores → `{"count":2800}` |
| GET | `/fifa` | todos os jogadores |
| GET | `/fifa/id/{id}` | 1 jogador pelo `player_id` (404 se não existir) |
| GET | `/fifa/nat/{exp}` | `nationality = {exp}` (ex.: `/fifa/nat/Brazil`) |

Documentação (Swagger UI): `http://<IP-DA-VM>:8080/swagger-ui.html` · OpenAPI JSON: `/v3/api-docs`

## Checklist da pontuação

- [x] Executar na VM → passo 4
- [x] Usar banco de dados → PostgreSQL
- [x] Todos os endpoints
- [x] Consultas rápidas → cache em memória do JSON já serializado (invalidado a cada POST), índice na nacionalidade, JDBC puro (sem JPA), threads virtuais
- [x] API autenticada (+10) → header `X-API-KEY` (defina a variável `API_KEY`; vazia = sem autenticação)
- [x] Código de inserção em lote → `CsvLoader` (batch de 500)
- [x] Coleção Postman/Insomnia → `postman/fifa-api.postman_collection.json`

## Passo a passo

### 1. Pré-requisitos na VM
```bash
sudo apt-get update && sudo apt-get install -y openjdk-21-jdk maven git
```

### 2. Banco de dados
Opção A (instala o Postgres e cria banco/usuário `fifa`/`fifa`):
```bash
./scripts/setup_db.sh
```
Opção B (Docker): `docker compose up -d`

A tabela `players` e o índice são criados automaticamente quando a API sobe (`schema.sql`).

### 3. Build
```bash
mvn clean package -DskipTests
```
Gera `target/fifa-api-1.0.0.jar`.

### 4. Carga em lote do CSV e execução
```bash
# carrega os 2800 registros e encerra
java -jar target/fifa-api-1.0.0.jar --app.load-csv=data/fifa.csv

# sobe a API (porta 8080)
API_KEY=minha-chave java -jar target/fifa-api-1.0.0.jar
```
Variáveis opcionais: `PORT`, `DB_URL`, `DB_USER`, `DB_PASSWORD`, `API_KEY`.

Para deixar rodando como serviço, use `scripts/fifa-api.service` (systemd).
Libere a porta 8080 no firewall/Security Group da VM.

### 5. Testar
```bash
curl -H "X-API-KEY: minha-chave" http://localhost:8080/fifa/count
curl -H "X-API-KEY: minha-chave" http://localhost:8080/fifa/id/1
curl -H "X-API-KEY: minha-chave" http://localhost:8080/fifa/nat/Brazil
```
No Postman/Insomnia: *Import* → `postman/fifa-api.postman_collection.json` e ajuste as variáveis `baseUrl` e `apiKey`.

## Observações
- Passe a `X-API-KEY` ao grupo que vai validar sua API (o Swagger fica público, as rotas `/fifa/**` não).
- Troque o e-mail de contato em `OpenApiConfig.java` (o enunciado pede um e-mail por grupo).
- Estrutura: `Player` (modelo) → `PlayerRepository` (JDBC) → `PlayerService` (cache) → `PlayerController` (REST).
