# Management System

## Description

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

Example result:

```text
Unseal Key 1: xxxxxx
Unseal Key 2: xxxxxx
Unseal Key 3: xxxxxx
Unseal Key 4: xxxxxx
Unseal Key 5: xxxxxx

Initial Root Token: xxxxxx
```

!IMPORTANT Save your keys in file!

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
spring.rabbitmq.host=localhost \
spring.rabbitmq.port=5672 \
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

