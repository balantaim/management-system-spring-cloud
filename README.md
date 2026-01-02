# Management System

### Init HashiCorp Vault

```bash
cd vault/
docker compose up -d
```

### Initialize & Unseal Vault (ONE TIME)

```bash
docker exec -it vault vault operator init
```

Use the UI via browser and generate 

- Root Token
- Unseal Keys

UI Link: http://127.0.0.1:8200

Example:

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