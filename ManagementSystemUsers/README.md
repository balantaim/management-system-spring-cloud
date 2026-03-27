# Users microservice

## Description

The Users microservice is responsible for all functionalities related to users, as well as for generating a valid JWT token used to authenticate with the API Gateway.

### Test the application's endpoints

```bash
curl http://localhost:5000/management-system-users/info
```

-------------

Connect to redis and check `KEYS "*"`

```bash
docker exec -it management-system-redis redis-cli
```