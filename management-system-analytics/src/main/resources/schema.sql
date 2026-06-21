-- Analytics table
CREATE TABLE IF NOT EXISTS analytics
(
    id               BIGSERIAL PRIMARY KEY,
    email            VARCHAR(255) NOT NULL UNIQUE,
    event_identifier VARCHAR(50)  NOT NULL,
    created_date     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
