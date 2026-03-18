CREATE TABLE app_user (
                          id            BIGSERIAL PRIMARY KEY,
                          username      VARCHAR(100) NOT NULL,
                          email         VARCHAR(255) NOT NULL UNIQUE,
                          password_hash VARCHAR(255) NOT NULL,
                          role          VARCHAR(50)  NOT NULL
);

INSERT INTO app_user (id, username, email, password_hash, role)
VALUES (1, 'Dev User', 'dev@local', 'dev-password-hash', 'USER');

ALTER TABLE task
    ADD COLUMN owner_id BIGINT NOT NULL DEFAULT 1;

ALTER TABLE task
    ALTER COLUMN owner_id DROP DEFAULT;

ALTER TABLE task
    ADD CONSTRAINT fk_task_owner
        FOREIGN KEY (owner_id) REFERENCES app_user(id);

CREATE INDEX idx_task_owner_id ON task(owner_id);
