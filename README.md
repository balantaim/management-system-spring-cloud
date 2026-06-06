# Management System - 6 projects in 1 project

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
6. Image Converter (Planned for future release)
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

Tools/libraries: Java, Spring (Cloud, Config Server, Security, JPA, WebFlux), Maven, Docker, RabbitMQ, Redis, git

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

- Config Server: http://localhost:8888/
- API Gateway: http://localhost:5000/
- Eureka Discovery: http://localhost:8761/
- Vault: http://localhost:8200/
- RabbitMQ: http://localhost:5672/
- RabbitMQ UI: http://localhost:15672/ (Requires image rabbitmq:x.x.x-management-alpine in the docker compose file)

### Endpoints Startup Order (Local)

1. Startup Vault, Redis, RabbitMQ (via Docker)
2. Unseal the Vault
3. Start Config Server
4. Start Eureka Discovery
5. Start microservices (Users, Analytics)
6. Start API Gateway

### Validate Token

https://www.jwt.io/

### Postman's resources

*Postman locale env:* [locale env](postman/management-system-local.postman_environment.json)

*Postman collection:* [collection](postman/management-system-spring-cloud.postman_collection.json)

## Contact me

[![Static Badge](https://img.shields.io/badge/Github-%2366099c?style=for-the-badge&logo=github&logoColor=black&labelColor=white)](https://github.com/balantaim)
[![Static Badge](https://img.shields.io/badge/google_play-%23057308?style=for-the-badge)](https://play.google.com/store/apps/dev?id=4991626043223074729)
[![Static Badge](https://img.shields.io/badge/Linkedin-%23321ee6?style=for-the-badge&logoColor=black&labelColor=white)](https://www.linkedin.com/in/martin-atanasov-47550b1a2/)

