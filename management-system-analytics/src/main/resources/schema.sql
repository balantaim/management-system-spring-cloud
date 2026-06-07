-- Analytics table
CREATE TABLE analytics
(
    id                      BIGSERIAL PRIMARY KEY,
    user_id                 VARCHAR(36)  NOT NULL UNIQUE
);
