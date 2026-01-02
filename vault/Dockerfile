FROM hashicorp/vault:1.21.1

RUN mkdir -p /vault/data

COPY vault.hcl /vault/config/vault.hcl

EXPOSE 8200

CMD ["vault", "server", "-config=/vault/config/vault.hcl"]
