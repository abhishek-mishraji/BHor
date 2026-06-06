-- Hands Of Retail — full schema


-- ─────────────────────────────────────────────
-- Users
-- ─────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS admin_users (
  admin_id      BIGSERIAL    PRIMARY KEY,
  full_name     VARCHAR(255) NOT NULL,
  email         VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role          VARCHAR(20)  NOT NULL,
  created_at    TIMESTAMP    NOT NULL,
  updated_at    TIMESTAMP
);

CREATE TABLE IF NOT EXISTS client_users (
  client_id     BIGSERIAL    PRIMARY KEY,
  full_name     VARCHAR(255) NOT NULL,
  email         VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  phone_number  VARCHAR(50),
  address       TEXT,
  status        VARCHAR(20)  NOT NULL,
  role          VARCHAR(20)  NOT NULL,
  created_at    TIMESTAMP    NOT NULL,
  updated_at    TIMESTAMP
);

-- ─────────────────────────────────────────────
-- Auth
-- ─────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS refresh_tokens (
  id         BIGSERIAL    PRIMARY KEY,
  token_hash VARCHAR(64)  NOT NULL UNIQUE,
  user_email VARCHAR(255) NOT NULL,
  user_role  VARCHAR(20)  NOT NULL,
  expires_at TIMESTAMP    NOT NULL,
  revoked    BOOLEAN      NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_rt_token_hash ON refresh_tokens(token_hash);
CREATE INDEX IF NOT EXISTS idx_rt_user_email  ON refresh_tokens(user_email);

-- ─────────────────────────────────────────────
-- Stores
-- ─────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS stores (
  store_id       BIGSERIAL    PRIMARY KEY,
  store_name     VARCHAR(255) NOT NULL,
  store_code     VARCHAR(100) NOT NULL UNIQUE,
  address        TEXT,
  contact_number VARCHAR(50),
  status         VARCHAR(20)  NOT NULL,
  created_at     TIMESTAMP    NOT NULL,
  updated_at     TIMESTAMP
);

-- M:M client ↔ store membership
-- Composite PK (client_id, store_id); partial unique index enforces one OWNER per store.
CREATE TABLE IF NOT EXISTS client_store_mapping (
  client_id  BIGINT      NOT NULL,
  store_id   BIGINT      NOT NULL,
  role       VARCHAR(20) NOT NULL,
  created_at TIMESTAMP   NOT NULL,
  updated_at TIMESTAMP,
  CONSTRAINT pk_csm        PRIMARY KEY (client_id, store_id),
  CONSTRAINT fk_csm_client FOREIGN KEY (client_id) REFERENCES client_users(client_id) ON DELETE CASCADE,
  CONSTRAINT fk_csm_store  FOREIGN KEY (store_id)  REFERENCES stores(store_id)       ON DELETE CASCADE
);

-- One-owner-per-store is enforced at the application layer.
-- On PostgreSQL in production use: CREATE UNIQUE INDEX uk_store_owner ON client_store_mapping(store_id) WHERE role = 'OWNER';
CREATE INDEX idx_csm_client ON client_store_mapping(client_id);
CREATE INDEX idx_csm_store  ON client_store_mapping(store_id);

-- ─────────────────────────────────────────────
-- Reports
-- ─────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS daily_reports (
  daily_report_id BIGSERIAL    PRIMARY KEY,
  store_id        BIGINT       NOT NULL,
  report_date     DATE         NOT NULL,
  grocery_total   NUMERIC(15,2),
  volume          NUMERIC(15,2),
  cash_deposit    NUMERIC(15,2),
  check_deposit   NUMERIC(15,2),
  over_short      NUMERIC(15,2),
  no_sale         NUMERIC(15,2),
  line_void       NUMERIC(15,2),
  void_amount     NUMERIC(15,2),
  refunds         NUMERIC(15,2),
  created_at      TIMESTAMP    NOT NULL,
  updated_at      TIMESTAMP,
  CONSTRAINT fk_daily_store FOREIGN KEY (store_id) REFERENCES stores(store_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS monthly_reports (
  monthly_report_id BIGSERIAL    PRIMARY KEY,
  store_id          BIGINT       NOT NULL,
  report_month      INTEGER      NOT NULL,
  report_year       INTEGER      NOT NULL,
  department_id     INTEGER,
  department_name   VARCHAR(255),
  gross             NUMERIC(15,2),
  discount          NUMERIC(15,2),
  promotion         NUMERIC(15,2),
  refund            NUMERIC(15,2),
  void_amount       NUMERIC(15,2),
  net_sales         NUMERIC(15,2),
  created_at        TIMESTAMP    NOT NULL,
  updated_at        TIMESTAMP,
  CONSTRAINT fk_monthly_store FOREIGN KEY (store_id) REFERENCES stores(store_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS yearly_reports (
  yearly_report_id BIGSERIAL PRIMARY KEY,
  store_id         BIGINT    NOT NULL,
  report_year      INTEGER   NOT NULL,
  annual_summary   TEXT,
  created_at       TIMESTAMP NOT NULL,
  updated_at       TIMESTAMP,
  CONSTRAINT fk_yearly_store FOREIGN KEY (store_id) REFERENCES stores(store_id) ON DELETE CASCADE
);
