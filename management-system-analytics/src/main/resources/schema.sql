-- Analytics table
CREATE TABLE IF NOT EXISTS analytics
(
    id                      BIGSERIAL PRIMARY KEY,
    user_id                 VARCHAR(36)  NOT NULL UNIQUE
);
