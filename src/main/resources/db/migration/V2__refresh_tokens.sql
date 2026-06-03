-- Refresh token storage for rotation-based auth
-- Tokens are stored as SHA-256 hashes (never raw values)

CREATE TABLE IF NOT EXISTS refresh_tokens (
  id            BIGSERIAL PRIMARY KEY,
  token_hash    VARCHAR(64)  NOT NULL UNIQUE,
  user_email    VARCHAR(255) NOT NULL,
  user_role     VARCHAR(20)  NOT NULL,
  expires_at    TIMESTAMP    NOT NULL,
  revoked       BOOLEAN      NOT NULL DEFAULT FALSE,
  created_at    TIMESTAMP    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_rt_token_hash ON refresh_tokens(token_hash);
CREATE INDEX IF NOT EXISTS idx_rt_user_email ON refresh_tokens(user_email);
