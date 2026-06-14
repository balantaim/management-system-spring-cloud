# Management System - 8 projects in 1 project

[License: Apache 2.0](LICENSE)

## Description

**Management System** is a Spring Cloud–based microservices application designed to demonstrate a scalable, secure, and
distributed system architecture. The project follows modern cloud-native principles and integrates several Spring Cloud
components to handle service discovery, configuration management, security, and communication between services.

The system uses **Spring Cloud Config Server** for centralized configuration management, with **HashiCorp Vault** as a
secure backend for storing sensitive secrets such as credentials and tokens. Configuration updates are dynamically
propagated across services using **Spring Cloud Bus** with **RabbitMQ**, enabling real-time refresh without service
restarts.

**Eureka Discovery Server** is used for service registration and discovery, allowing microservices to locate and
communicate with each other dynamically. An **API Gateway** acts as a single entry point for all client requests,
providing routing, load balancing, and centralized request handling.

**Software architecture:** Microservices-based architecture packaged by feature, using the MVC design pattern in the presentation layer.

**Included projects:**

1. Users service - [Setup guide](./management-system-users/README.md) (Required)
2. Config server - [Setup guide](./management-system-config-server/README.md) (Required)
3. Eureka Discovery server - [Readme](./management-system-eureka-discovery/README.md)
4. API Gateway - [Readme](./management-system-api-gateway/README.md)
5. Analytics service - [Readme](./management-system-analytics/README.md)
6. Image Creator service - [Readme](./management-system-image-creator/README.md)
7. Web frontend (Planned for future release)
8. Desktop Client - [Readme](./management-system-desktop-client/README.md)

**Key Features:**

- Spring Cloud microservices architecture
- Centralized configuration using Spring Cloud Config Server
- Secure secrets management with HashiCorp Vault
- Dynamic configuration refresh with Spring Cloud Bus and RabbitMQ
- Service discovery with Eureka
- API Gateway for routing and load balancing
- Redis RateLimiter for the API Gateway
- Decoupled and scalable microservice design

**Software Architecture:** Microservice architecture with layered design, organized and packaged by feature.

## Software and Requirements

Tools/libraries: Java, Spring (Cloud, Config Server, Security, JPA, RestClient, API Gateway), Maven, Docker, RabbitMQ, Redis, Kafka, H2, PostgreSQL, Lombok, git

Requirements: Java 21+, Docker, git

### Config Server

Supported implementations:

- [x] HashiCorp Vault
- [ ] Git
- [ ] AWS Secrets Manager
- [ ] AWS Parameter Store
- [ ] AWS S3 Backend

### Client side usage

Supported implementations:

- [ ] Web frontend (Planned release)
- [x] Desktop client (Java, Swing, Flatlaf and MigLayout)
- [x] Postman as UI client
- [ ] Mobile application
- [ ] OpenAPI/Swagger

### Service endpoints

- Local env:

  - Config Server: http://localhost:8888
  - API Gateway: http://localhost:5000
  - Eureka Discovery: http://localhost:8761
  - PostgreSQL (Users): http://localhost:5432
  - Vault: http://localhost:8200
  - RabbitMQ: http://localhost:5672
  - RabbitMQ UI: http://localhost:15672 (Requires image rabbitmq:x.x.x-management-alpine in the docker compose file)
  - Redis http://localhost:6379
  - Kafka: http://localhost:9092
  - Kafka UI: http://localhost:8090

- Docker env:

  - Config Server: http://management-system-config-server:8888 (No configuration yet)
  - API Gateway: http://management-system-api-gateway:5000 (No configuration yet)
  - Eureka Discovery: http://management-system-discovery-server:8761 (No configuration yet)
  - PostgreSQL (Users): http://datasource_users:5432
  - Vault: http://management-system-vault:8200
  - RabbitMQ: http://management-system-rabbitmq:5672
  - RabbitMQ UI: http://management-system-rabbitmq:15672 (Requires image rabbitmq:x.x.x-management-alpine in the docker compose file)
  - Redis http://management-system-redis:6379
  - Kafka: http://management-system-kafka:9092
  - Kafka UI: http://management-system-kafka-ui:8090

### Endpoints Startup Order (Local)

1. Startup Vault, Redis, RabbitMQ (via Docker)
2. Unseal the Vault
3. Start Config Server
4. Start Eureka Discovery
5. Start microservices (Users, Analytics, Image creator)
6. Start API Gateway

### Postman's resources

- *Postman collection:* [collection](postman/management-system-spring-cloud.postman_collection.json)
- *Postman locale env:* [locale env](postman/management-system-local.postman_environment.json)

### Validate Token

https://www.jwt.io/

## Contact me

[![Static Badge](https://img.shields.io/badge/Github-%2366099c?style=for-the-badge&logo=github&logoColor=black&labelColor=white)](https://github.com/balantaim)
[![Static Badge](https://img.shields.io/badge/google_play-%23057308?style=for-the-badge)](https://play.google.com/store/apps/dev?id=4991626043223074729)
[![Static Badge](https://img.shields.io/badge/Linkedin-%23321ee6?style=for-the-badge&logoColor=black&labelColor=white)](https://www.linkedin.com/in/martin-atanasov-47550b1a2/)

