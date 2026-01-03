# Management System

## Description

## Software and Requirements

Tools/library: Java, Spring (Cloud, Config Server, WebMVC), Maven, Docker, RabbitMQ, git

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

Use the UI via browser and generate (Recommended): http://127.0.0.1:8200

- Root Token (At least 1)
- Unseal Keys (At least 1, but recommended 3 or more for production)


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
 - version: 1
 - url: management-system

Alternatively, you can use the CLI to unseal the vault:

```bash
docker exec -it management-system-vault vault operator init
```



### Service endpoints

- Config Server: http://localhost:8888/
- Vault: http://localhost:8200/
- Users: http://localhost:5000/
- RabbitMQ: http://localhost:5672/
- RabbitMQ UI: http://localhost:15672/