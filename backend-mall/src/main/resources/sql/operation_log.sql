CREATE TABLE IF NOT EXISTS operation_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  admin_id BIGINT,
  admin_username VARCHAR(64),
  action VARCHAR(64) NOT NULL,
  target VARCHAR(128),
  detail VARCHAR(512),
  ip VARCHAR(64),
  created_at DATETIME,
  INDEX idx_admin_id (admin_id),
  INDEX idx_action (action),
  INDEX idx_created_at (created_at)
);
