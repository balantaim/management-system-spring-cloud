# Management System
[License: Apache 2.0](LICENSE)

## Description

**Management System** is a Spring Cloud–based microservices application designed to demonstrate a scalable, secure, and distributed system architecture. The project follows modern cloud-native principles and integrates several Spring Cloud components to handle service discovery, configuration management, security, and communication between services.

The system uses **Spring Cloud Config Server** for centralized configuration management, with **HashiCorp Vault** as a secure backend for storing sensitive secrets such as credentials and tokens. Configuration updates are dynamically propagated across services using **Spring Cloud Bus** with **RabbitMQ**, enabling real-time refresh without service restarts.

**Eureka Discovery Server** is used for service registration and discovery, allowing microservices to locate and communicate with each other dynamically. An **API Gateway** acts as a single entry point for all client requests, providing routing, load balancing, and centralized request handling.

The architecture is designed to be modular and extensible, making it easy to add new microservices or integrate additional cloud components in the future.

**Key Features:**
- Spring Cloud microservices architecture
- Centralized configuration using Spring Cloud Config Server
- Secure secrets management with HashiCorp Vault
- Dynamic configuration refresh with Spring Cloud Bus and RabbitMQ
- Service discovery with Eureka
- API Gateway for routing and load balancing
- Decoupled and scalable microservice design

**Software Architecture:** Microservice architecture with layered design, organized and packaged by feature.

## Software and Requirements

Tools/libraries: Java, Spring (Cloud, Config Server, WebFlux), Maven, Docker, RabbitMQ, git

Requirements: Java, Docker, git

### Config Server

Supported implementations:

- [x] HashiCorp Vault
- [ ] Git

Setup Config Server: [Guide](ManagementSystemConfigServer/README.md)


### Init HashiCorp Vault


```bash
cd vault/
docker compose -p management-system up -d
```

### Initialize & Unseal Vault (ONE TIME)

**Variant 1: Use the CLI to unseal the vault (Recommend):**

Enter the vault's CLI mode

```bash
docker exec -it management-system-vault sh
```

Init the Vault

```bash
vault operator init
```

You will receive:

- 5 unseal keys
- 1 root token

Credentials Example:

```text
Unseal Key 1: xxxxxx
Unseal Key 2: xxxxxx
Unseal Key 3: xxxxxx
Unseal Key 4: xxxxxx
Unseal Key 5: xxxxxx

Initial Root Token: xxxxxx
```

> [!IMPORTANT]
> You are responsible for securely saving the keys in the file for future use.

You need to execute the following command at least `3 TIMES` and enter the unused key:

```bash
vault operator unseal
```

Check if the Vault is unsealed:

```bash
vault status
```

Export the root token and create a new engine `kv` version 2:

```bash
export VAULT_TOKEN="<YOUR_ROOT_TOKEN>"

vault secrets enable -path=secret kv-v2
```

Create add new secrets:

```bash
vault kv put secret/management-system \
private.key="111" \
spring.rabbitmq.host="localhost" \
spring.rabbitmq.port="5672" \
spring.rabbitmq.username="user" \
spring.rabbitmq.password="password"
```

TEST

```bash
vault kv put secret/management-system-docker \
private.key="222" \
spring.rabbitmq.host="rabbitmq" \
spring.rabbitmq.port="5672" \
spring.rabbitmq.username="user" \
spring.rabbitmq.password="password"
```

Get the secrets:

```bash
vault kv get secret/management-system
```


**Variant 2: Use the UI via browser and generate:**

- Root Token (At least 1)
- Unseal Keys (At least 1, but recommended 3 or more for production)

Use the url: http://127.0.0.1:8200

Credentials Example:

```json
{
  "keys": [
    "rw5432T4H46H64HJ46Y435Y543T34T43TR43RT34T35T34T3453453SDF"
  ],
  "keys_base64": [
    "nadfldfpowfjkwpefjki98324oj2p34="
  ],
  "root_token": "ala.asdfwe423523345235"
}
```
Login to the Vault and create a new secret engine:

 - engine: kv
 - version: 2
 - url: management-system

TODO variables...


### Service endpoints

- Config Server: http://localhost:8888/
- API Gateway: http://localhost:5000/
- Eureka Discovery: http://localhost:8761/
- Vault: http://localhost:8200/
- RabbitMQ: http://localhost:5672/
- RabbitMQ UI: http://localhost:15672/


## Contact me

[![Static Badge](https://img.shields.io/badge/Github-%2366099c?style=for-the-badge&logo=github&logoColor=black&labelColor=white)](https://github.com/balantaim)
[![Static Badge](https://img.shields.io/badge/google_play-%23057308?style=for-the-badge)](https://play.google.com/store/apps/dev?id=4991626043223074729)
[![Static Badge](https://img.shields.io/badge/Linkedin-%23321ee6?style=for-the-badge&logoColor=black&labelColor=white)](https://www.linkedin.com/in/martin-atanasov-47550b1a2/)

