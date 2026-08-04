# MS_Banco — v2: Microsserviços

Evolução do monólito (ver tag `v1-monolito`) para arquitetura de
microsserviços: Eureka, Config Server, Auth com JWT, Wallet Core
e API Gateway.

## Serviços
- **Discovery** (Eureka): service discovery, porta 8761
- **Config Server**: configuração centralizada via Git privado, porta 8888
- **Auth**: cadastro, login, emissão de JWT (HS256)
- **Wallet Core**: contas, depósito, saque, transferência
- **Gateway**: ponto único de entrada, valida JWT localmente, roteia via Eureka

## Decisões de arquitetura
- Auth e Wallet Core mantêm bancos Postgres separados (um por serviço)
- Gateway valida o JWT localmente (chave compartilhada via Config Server),
  sem chamar o Auth a cada requisição — mantém o token stateless
- Conta e transação permanecem no mesmo serviço (Wallet Core) para
  preservar atomicidade via `@Transactional`

## Como rodar
`docker compose up --build`

## Roadmap
Ver tag `v1-monolito` para a versão anterior, de referência.