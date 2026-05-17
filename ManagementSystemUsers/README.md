# Users microservice

## Description

The Users microservice is responsible for all functionalities related to users, as well as for generating a valid JWT token used to authenticate with the API Gateway.

## Set up the application:

**Generate Public and Private keys via keytool:**

1. Generate .p12 file via keytool in the `resources` directory
    ```bash
    keytool -genkeypair \
      -alias management-system-key \
      -keyalg RSA \
      -keysize 2048 \
      -validity 3650 \
      -storetype PKCS12 \
      -keystore management-system.p12 \
      -storepass <YOUR_STORE_PASSWORD> \
      -keypass <YOUR_KEY_PASSWORD> \
      -dname "CN=management-system, OU=Development, L=Sofia, C=BG"
    ```

2. Extract Public key as file
    ```bash
    keytool -exportcert \
      -alias management-system-key \
      -keystore management-system.p12 \
      -storetype PKCS12 \
      -rfc \
      -file public.pem
    ```

3. Copy `public.pem` to APIGateway's `resources` directory

### Check Redis connection (Optional)

Connect to redis cli via Docker container (`management-system-redis` is the name of the container)

```bash
docker exec -it management-system-redis redis-cli
```

Check all variables in Redis with following command: `KEYS "*"`
