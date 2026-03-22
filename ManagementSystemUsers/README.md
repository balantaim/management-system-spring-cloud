


```bash
curl http://localhost:5000/management-system-users/info
```

Connect to redis and check `KEYS "*"`

```bash
docker exec -it management-system-redis redis-cli
```