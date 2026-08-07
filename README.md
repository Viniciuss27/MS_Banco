# MS_Banco

Simulação de sistema bancário construída em etapas, evoluindo de monólito para uma arquitetura de microsserviços com mensageria assíncrona. Projeto de portfólio, com foco em decisões de arquitetura defensáveis e histórico real de evolução via tags Git.

> **Histórico completo:** `git checkout v1-monolito` (versão monólito) → `v2-microservices` (Eureka, Config Server, Auth, Gateway) → `v3-notification-tests` (versão atual: Kafka + Notification Service + testes automatizados)

## Stack

Java 21 · Spring Boot 3.4.5 · Spring Cloud 2024.0.1 · Spring Data JPA · Spring Security · JWT (JJWT) · Apache Kafka · MapStruct · Lombok · Bean Validation · Flyway · PostgreSQL · H2 · Docker & Docker Compose · JUnit 5 · Mockito

## Arquitetura (v3)

```
Cliente
  │  JWT no header Authorization
  ▼
Gateway (8765)  ── valida o JWT localmente, sem chamar o Auth a cada requisição
  │
  ├──► Auth (login, cadastro, emissão de JWT)
  │
  └──► Wallet Core (contas, depósito, saque, transferência)
         │
         │  evento publicado após o commit (AFTER_COMMIT)
         ▼
       Kafka (transfer-events, deposit-events, withdraw-events)
         │
         ▼
       Notification (consome e loga a notificação)

Eureka + Config Server: descoberta de serviço e configuração
centralizada (via repositório Git privado), usados por todos os serviços acima
```

| Serviço | Responsabilidade | Porta |
|---|---|---|
| Discovery (Eureka) | Service discovery | 8761 |
| Config Server | Configuração centralizada (Git privado) | 8888 |
| Auth | Cadastro, login, emissão/validação de JWT | dinâmica |
| Wallet Core | Contas, depósito, saque, transferência | 8080 |
| Gateway | Ponto único de entrada, valida JWT, roteia | 8765 |
| Notification | Consome eventos Kafka, notifica de forma assíncrona | dinâmica |

## Decisões de arquitetura

- **Conta e transação ficam no mesmo serviço (Wallet Core).** A transferência exige atomicidade real entre débito e crédito — fatiar isso em serviços diferentes exigiria Saga pattern com compensação, decisão consciente de não implementar nesta fase.
- **Auth e Notification são serviços separados** porque toleram consistência eventual — não participam da transação financeira.
- **Gateway valida o JWT localmente** (chave HS256 compartilhada via Config Server), sem chamar o Auth a cada requisição — preserva a vantagem de um token stateless.
- **Eureka e Config Server rodam em processos separados** (evoluído a partir de uma tentativa inicial de combiná-los no mesmo processo, que gerava conflito de dependências web).
- **Cada serviço tem seu próprio banco Postgres** (`db_wallet`, `db_auth`) — sem acesso cruzado a tabela de outro serviço.
- **Wallet Core publica o evento só depois do commit** (`@TransactionalEventListener(phase = AFTER_COMMIT)`) — evita notificar uma transferência que, por algum motivo, não foi persistida de verdade.
- **Ordenação determinística ao buscar contas na transferência** (menor ID primeiro) — evita deadlock em transferências concorrentes entre as mesmas duas contas.

## Como rodar

```bash
# gera o .jar de cada serviço (Dockerfiles são single-stage)
cd Eureka && ./mvnw clean package -DskipTests && cd ..
cd Config_Server && ./mvnw clean package -DskipTests && cd ..
cd Wallet_Core && ./mvnw clean package -DskipTests && cd ..
cd Auth && ./mvnw clean package -DskipTests && cd ..
cd Gateway && ./mvnw clean package -DskipTests && cd ..
cd Notification && ./mvnw clean package -DskipTests && cd ..

docker compose up --build
```

Requer um arquivo `.env` na raiz com: `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION`, `GIT_USERNAME`, `GIT_PASSWORD`.

Painel do Eureka: `http://localhost:8761` · Swagger do Wallet Core: `http://localhost:8080/docs` · Todas as chamadas de API via Gateway: `http://localhost:8765`

## Testes

```bash
mvn test
```
em cada módulo. Cobertura atual: testes unitários (Mockito) das regras de negócio do Wallet Core — depósito, saque, transferência, saldo insuficiente, transferência para a mesma conta, valor inválido, conta inexistente.

## Bugs encontrados e corrigidos ao longo do projeto

| Sintoma | Causa raiz | Correção |
|---|---|---|
| Insert falha com "value too long" | CPF formatado gravado em coluna `VARCHAR(11)` | Normalizar removendo não-dígitos antes de persistir |
| Campos `error`/`path` trocados no JSON de erro | Construtor posicional com ordem divergente da classe | Setters nomeados / `@Builder` em vez de construtor posicional |
| `500` genérico em vez de `409` no CPF/email duplicado | Deixar o banco estourar a constraint `UNIQUE` | Checagem explícita no Service antes do insert |
| `Circular depends-on` entre Flyway e `entityManagerFactory` | `ddl-auto` assume `create-drop` por padrão em banco embarcado | `hibernate.ddl-auto: validate` explícito |
| Build falha com `TypeTag :: UNKNOWN` | Versão do Lombok incompatível com o JDK instalado | Fixar `lombok.version` (1.18.38) em todos os módulos |
| Flyway não aplica migration | `flyway-database-postgresql` ausente (obrigatório a partir do Flyway 10 / Boot 3.3+) | Adicionar a dependência ao subir para Spring Boot 3.4.x |
| `404` ao rotear via Gateway | Múltiplos `Path=` na mesma rota funcionam como "E", não "OU" | Um único `Path=` com padrões separados por vírgula |
| Notification não consumia nenhuma mensagem | Kafka de nó único sem `OFFSETS_TOPIC_REPLICATION_FACTOR=1` — coordenador de grupo nunca eleito | Forçar replication factor 1 nos tópicos internos do Kafka |

## Limitações conhecidas / Roadmap (v4)

- **Contrato de evento Kafka ainda frágil**: erro `No type information in headers and no default type provided` ao desserializar em alguns cenários — o `JsonSerializer`/`JsonDeserializer` do Spring Kafka dependem de headers de tipo que nem sempre chegam de forma confiável entre serviços com classes duplicadas (sem módulo de contrato compartilhado). Próximo passo: formalizar o contrato do evento (`eventType` explícito no payload, ou Schema Registry com Avro/Protobuf).
- Sem circuit breaker entre Gateway e serviços internos.
- Sem correlation ID entre serviços para rastrear uma requisição de ponta a ponta.
- Sem pipeline de CI/CD.

## Histórico de versões

- **v1-monolito**: contas, transações, validação, tratamento de erro, Docker + Postgres, tudo em um único serviço.
- **v2-microservices**: divisão em Eureka, Config Server, Auth (JWT), Wallet Core e Gateway, comunicação via service discovery.
- **v3-notification-tests** *(atual)*: Notification Service consumindo eventos via Kafka de forma assíncrona, testes unitários das regras de negócio do Wallet Core.
