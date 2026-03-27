# API Gateway

## Description

The application represents a reactive API Gateway and is used as a centralized endpoint for all microservices. Through its security configuration, the service can validate users’ JWT tokens and authorize access to specific resources based on their respective roles. Additionally, the API Gateway includes a reactive rate limiter that stores data in a Redis database and blocks requests according to the application's configuration.

### Software

**Tools/libraries:** Java, Spring, Spring cloud, Spring actuator, Redis, Maven

### Default Configuration

- **Default spring profile:** `local`
- **Default server port:** `5000`

### Set up the environment

- Set JWT secret for variable `TOKEN_SECRET_KEY`
- Set Users service URL (Optional) `USERS_SERVICE_URL`

### Start the application

- Via terminal:
    ```bash
    mvn spring-boot:run
    ```

    Or with parameters

    ```bash
    mvn spring-boot:run -Dtoken.secret-key="<YOUR_TOKEN_SECRET_KEY>"
    ```