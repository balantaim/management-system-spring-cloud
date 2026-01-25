-- Users table
CREATE TABLE users
(
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    full_name     VARCHAR(255) NOT NULL,
    password      VARCHAR(255) NOT NULL,
    user_id       VARCHAR(36)  NOT NULL UNIQUE,
    enabled       BOOLEAN      NOT NULL DEFAULT FALSE,
    created_date  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Authorities table
CREATE TABLE authorities
(
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(20) NOT NULL UNIQUE,
    created_date  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Roles table
CREATE TABLE roles
(
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(20) NOT NULL UNIQUE,
    created_date  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Users-Roles junction table
CREATE TABLE users_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

-- Roles-Authorities junction table
CREATE TABLE roles_authorities
(
    role_id      BIGINT NOT NULL,
    authority_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, authority_id),
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    FOREIGN KEY (authority_id) REFERENCES authorities (id) ON DELETE CASCADE
);

-- Indexes for performance
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_user_id ON users (user_id);
CREATE INDEX idx_users_roles_user_id ON users_roles (user_id);
CREATE INDEX idx_users_roles_role_id ON users_roles (role_id);
CREATE INDEX idx_roles_authorities_role_id ON roles_authorities (role_id);
CREATE INDEX idx_roles_authorities_authority_id ON roles_authorities (authority_id);