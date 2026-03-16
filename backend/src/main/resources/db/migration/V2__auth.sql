CREATE TABLE app_users (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(320) NOT NULL UNIQUE,
    password_hash VARCHAR(255),
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE user_oauth_accounts (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    provider VARCHAR(32) NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_user_oauth_accounts_user FOREIGN KEY (user_id) REFERENCES app_users(id),
    CONSTRAINT uk_provider_identity UNIQUE (provider, provider_user_id)
);

CREATE TABLE app_sessions (
    token VARCHAR(72) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_app_sessions_user FOREIGN KEY (user_id) REFERENCES app_users(id)
);
