# Config Server

## Description

This project is a simple Spring Boot application that acts as a Config Server. It allows other applications to retrieve configuration properties from the Vault service. It uses RabbitMQ to publish and subscribe to configuration changes.

### Software

**Tools/libraries:** Java, Spring, Config server, Spring actuator, RabbitMQ, Redis, HashiCorp Vault, Maven, Docker

### Base configuration

- **Default spring profile:** `vault`
- **Default server port:** `8888`
- **Default vault port:** `8200`
- **Default vault engine kv:** `2`

### Init HashiCorp Vault and RabbitMQ services

If you are inside `ManagementSystemConfigServer` use `cd ..` to navigate to the root of the repository

```bash
cd vault/
docker compose -p management-system up -d
```

## Initialize & Unseal Vault (ONE TIME)

### **Variant 1: Use the CLI to unseal the vault (Recommend):**

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

Add new secrets for `local` testing (Optional):

```bash
vault kv put "secret/application" \
spring.rabbitmq.host="localhost" \
spring.rabbitmq.port="5672" \
spring.rabbitmq.username="user" \
spring.rabbitmq.password="password"
```

Get the secrets:

```bash
vault kv get secret/application
```

### **Variant 2: Use the UI via browser and unseal the vault:**

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
### Create Vault's engine and store the secrets

Login to the Vault and create a new secret engine:

- engine: kv
- version: 2
- url: management-system

## Update the applications properties

Add value for `ROOT_TOKEN` required for connection with Vault. (This could be IDE configuration file or environment variable)

**Add secrets in to Vault**

> [!NOTE]
> Vault should be unsealed before you start!

> [!IMPORTANT]
> Check the [Users microservice guide](../ManagementSystemUsers/README.md) in order to create public and private keys.

1. Enter in to Vault's cli via Docker container (`management-system-vault` is the name of the container):

    ```bash
    docker exec -it management-system-vault /bin/sh
    ```

2. Login to the Vault via `ROOT_TOKEN`. Replace the `<..>` with your `ROOT_TOKEN`

    ```bash
    vault login <YOUR_ROOT_TOKEN>
    ```

3. Add new secrets for `local` testing:

    ```bash
    vault kv put "secret/application" \
    spring.rabbitmq.host="localhost" \
    spring.rabbitmq.port="5672" \
    spring.rabbitmq.username="user" \
    spring.rabbitmq.password="password"
    ```

4. Put secrets for `API Gateway` (public key location). If you need to add configuration only for `prod` profile use `"secret/management-system-api-gateway,prod"` where `,` is separator.

    ```bash
    vault kv put "secret/management-system-api-gateway" \
    token.public-key-location="classpath:public.pem"
    ```

5. Put secrets for `Users microservice` (keystore credentials):

    ```bash
    vault kv put "secret/management-system-users" \
    encrypt.key-store.location="classpath:management-system.p12" \
    encrypt.key-store.password="<YOUR_KEY_STORE_PASS>" \
    encrypt.key-store.alias="management-system-key" \
    encrypt.key-store.secret="<YOUR_KEY_STORE_SECRET>"
    ```

6. Test the credentials from new terminal:

    ```bash
    vault kv get -mount="secret" "management-system-users"

    vault kv get -mount="secret" "management-system-api-gateway"
    ```

### Start the application

- Via terminal:
    ```bash
    mvn spring-boot:run
    ```

    Or with parameters (Replace `<YOUR_ROOT_TOKEN>` with your actual Vault token):

    ```bash
    mvn spring-boot:run -Dspring.cloud.config.server.vault.token="<YOUR_ROOT_TOKEN>"
    ```

- Via IDE:
    - Right-click on your application and select `Run 'ManagementSystemConfigServerApplication'`

### Test bus refresh

Test `busrefresh` via curl:

```bash
curl -X POST http://localhost:8888/actuator/busrefresh
```

Check the stored secrets for all applications:

```bash
curl http://localhost:8888/application/default
```