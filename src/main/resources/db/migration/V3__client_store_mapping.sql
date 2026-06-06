-- Introduce M:M relationship between client_users and stores
-- Every store has exactly one OWNER; zero or more PARTNER clients.
-- Existing stores: their current client_id is migrated as the OWNER mapping.

-- 1. Create the mapping table
CREATE TABLE IF NOT EXISTS client_store_mapping (
  client_id  BIGINT      NOT NULL,
  store_id   BIGINT      NOT NULL,
  role       VARCHAR(20) NOT NULL,
  created_at TIMESTAMP   NOT NULL,
  updated_at TIMESTAMP,
  CONSTRAINT pk_csm PRIMARY KEY (client_id, store_id),
  CONSTRAINT fk_csm_client FOREIGN KEY (client_id)
    REFERENCES client_users(client_id) ON DELETE CASCADE,
  CONSTRAINT fk_csm_store  FOREIGN KEY (store_id)
    REFERENCES stores(store_id) ON DELETE CASCADE
);

-- 2. Partial unique index: each store has at most one OWNER
CREATE UNIQUE INDEX uk_store_owner
  ON client_store_mapping(store_id)
  WHERE role = 'OWNER';

-- 3. Performance indexes for common lookup patterns
CREATE INDEX idx_csm_client ON client_store_mapping(client_id);
CREATE INDEX idx_csm_store  ON client_store_mapping(store_id);

-- 4. Migrate existing ownership data
--    Every store currently has a direct client_id; promote each to OWNER mapping.
INSERT INTO client_store_mapping (client_id, store_id, role, created_at, updated_at)
SELECT client_id, store_id, 'OWNER', NOW(), NOW()
FROM stores
WHERE client_id IS NOT NULL;

-- 5. Remove the now-redundant direct ownership column from stores
ALTER TABLE stores DROP CONSTRAINT fk_store_client;
ALTER TABLE stores DROP COLUMN client_id;
