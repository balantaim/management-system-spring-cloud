-- CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Insert authorities
INSERT INTO authorities (id, name)
VALUES (1, 'READ'),
       (2, 'WRITE'),
       (3, 'ADMIN_READ'),
       (4, 'ADMIN_WRITE');

-- Insert roles
INSERT INTO roles (id, name)
VALUES (1, 'CUSTOMER'),
       (2, 'ADMIN');

-- Role CUSTOMER set Authorities: READ, WRITE
INSERT INTO roles_authorities (role_id, authority_id)
VALUES (1, 1),
       (1, 2);

-- Role ADMIN set Authorities: ADMIN_READ, ADMIN_WRITE
INSERT INTO roles_authorities (role_id, authority_id)
VALUES (2, 3),
       (2, 4);

-- Create base Customer and Admin role
INSERT INTO users (email, full_name, password, user_id, account_non_expired, account_non_locked, credentials_non_expired, enabled)
VALUES ('abv@abv.bg', 'Customer', '$2a$10$D8vmo9/vGXaTIA0vOHGRlu0UDSCYl54PLoRuI3U7J0A5ay86/5CEy',
        gen_random_uuid(), TRUE, TRUE, TRUE, TRUE),
       ('admin@abv.bg', 'Admin', '$2a$10$D8vmo9/vGXaTIA0vOHGRlu0UDSCYl54PLoRuI3U7J0A5ay86/5CEy',
        gen_random_uuid(), TRUE, TRUE, TRUE, TRUE);

-- Assign roles to the users (1: CUSTOMER, 2: ADMIN)
INSERT INTO users_roles (user_id, role_id)
VALUES (1, 1), -- customer add role CUSTOMER
       (2, 1), -- admin add role CUSTOMER
       (2, 2); -- admin add role ADMIN
