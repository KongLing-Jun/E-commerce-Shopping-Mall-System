-- Migration: product gallery + order delivery + merchant notice
-- Date: 2026-02-23

CREATE TABLE IF NOT EXISTS product_image
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT       NOT NULL,
    url        VARCHAR(255) NOT NULL,
    sort       INT DEFAULT 0,
    INDEX idx_product_image_product_id (product_id)
) CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS order_delivery
(
    order_id        BIGINT PRIMARY KEY,
    express_no      VARCHAR(64),
    express_company VARCHAR(64)
) CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS merchant_notice
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    notice_type VARCHAR(64)  NOT NULL,
    order_no    VARCHAR(64)  NOT NULL,
    content     VARCHAR(512) NOT NULL,
    status      INT          NOT NULL DEFAULT 0,
    created_at  DATETIME,
    INDEX idx_merchant_notice_status (status),
    INDEX idx_merchant_notice_created_at (created_at)
) CHARSET = utf8mb4;
