# Users microservice

## Description

The Users microservice is responsible for all functionalities related to users, as well as for generating a valid JWT token used to authenticate with the API Gateway.

## Generate Public and Private keys via keytool

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

### Test the application's endpoints

```bash
curl http://localhost:5000/management-system-users/info
```

-------------

Connect to redis and check `KEYS "*"`

```bash
docker exec -it management-system-redis redis-cli
```

