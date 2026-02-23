CREATE TABLE IF NOT EXISTS merchant_notice (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  notice_type VARCHAR(64) NOT NULL,
  order_no VARCHAR(64) NOT NULL,
  content VARCHAR(512) NOT NULL,
  status INT NOT NULL DEFAULT 0,
  created_at DATETIME,
  INDEX idx_merchant_notice_created_at (created_at),
  INDEX idx_merchant_notice_status (status)
);
