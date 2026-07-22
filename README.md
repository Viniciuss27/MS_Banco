# MS_Banco — Wallet Core (v1: Monólito)

Simulação de sistema bancário: contas, depósito, saque e transferência,
com validação de negócio e tratamento de erro completo.

## Stack
Java 21, Spring Boot 3.2.5, Spring Data JPA, MapStruct, Lombok,
Bean Validation, Flyway, PostgreSQL, Docker.

## Decisões de arquitetura
- Monólito modular (não microsserviços): a transferência entre contas
  precisa de atomicidade real (débito+crédito na mesma transação `@Transactional`).
  Fatiar isso exigiria Saga pattern com compensação — decisão consciente
  de não implementar nessa fase.
- Separação AccountService (regras de saldo) / TransactionService (só leitura).
- Flyway com `ddl-auto: validate` — schema versionado, Hibernate só confere.

## Como rodar
`docker compose up --build`

## Roadmap
Próxima versão evolui para arquitetura de microsserviços
(Eureka, Gateway, Auth com JWT) — ver tag `v1-monolito` para
a versão monolito de referência.