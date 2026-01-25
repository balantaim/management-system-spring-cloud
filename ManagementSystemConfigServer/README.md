

Add properties `ROOT_TOKEN` for Vault's root token

Test `busrefresh` via curl:

```bash
curl -X POST http://localhost:8888/actuator/busrefresh
```

Check the stored secrets:

```bash
curl http://localhost:8888/config/management-system
```