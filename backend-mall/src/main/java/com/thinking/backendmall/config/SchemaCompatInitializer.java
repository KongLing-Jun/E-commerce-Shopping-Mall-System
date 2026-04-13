package com.thinking.backendmall.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SchemaCompatInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SchemaCompatInitializer.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        ensureBannerColumns();
        ensureOrderTrackingEventTable();
        ensureMerchantNoticeTable();
    }

    private void ensureBannerColumns() {
        addColumnIfMissing("banner", "title", "varchar(255) null");
        addColumnIfMissing("banner", "subtitle", "varchar(255) null");
        addColumnIfMissing("banner", "button_text", "varchar(255) null");
    }

    private void ensureOrderTrackingEventTable() {
        if (hasTable("order_tracking_event")) {
            return;
        }
        jdbcTemplate.execute("""
                CREATE TABLE order_tracking_event (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    order_id BIGINT NOT NULL,
                    status INT NULL,
                    title VARCHAR(80) NOT NULL,
                    description VARCHAR(255) NULL,
                    location VARCHAR(255) NULL,
                    event_time DATETIME NOT NULL,
                    created_at DATETIME NULL,
                    INDEX idx_order_tracking_event_order_id (order_id),
                    INDEX idx_order_tracking_event_event_time (event_time),
                    CONSTRAINT fk_order_tracking_event_order_id
                        FOREIGN KEY (order_id) REFERENCES `order`(id)
                        ON DELETE CASCADE
                )
                """);
        log.info("Schema compatibility: created table order_tracking_event");
    }

    private void ensureMerchantNoticeTable() {
        if (hasTable("merchant_notice")) {
            return;
        }
        jdbcTemplate.execute("""
                CREATE TABLE merchant_notice (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    notice_type VARCHAR(64) NOT NULL,
                    order_no VARCHAR(64) NULL,
                    content VARCHAR(500) NULL,
                    status INT NOT NULL DEFAULT 0,
                    created_at DATETIME NULL,
                    INDEX idx_merchant_notice_status (status),
                    INDEX idx_merchant_notice_created_at (created_at)
                )
                """);
        log.info("Schema compatibility: created table merchant_notice");
    }

    private void addColumnIfMissing(String tableName, String columnName, String ddl) {
        if (hasColumn(tableName, columnName)) {
            return;
        }
        jdbcTemplate.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + ddl);
        log.info("Schema compatibility: added column {}.{} {}", tableName, columnName, ddl);
    }

    private boolean hasTable(String tableName) {
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM information_schema.TABLES
                        WHERE TABLE_SCHEMA = DATABASE()
                          AND TABLE_NAME = ?
                        """,
                Integer.class,
                tableName
        );
        return count != null && count > 0;
    }

    private boolean hasColumn(String tableName, String columnName) {
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*)
                        FROM information_schema.COLUMNS
                        WHERE TABLE_SCHEMA = DATABASE()
                          AND TABLE_NAME = ?
                          AND COLUMN_NAME = ?
                        """,
                Integer.class,
                tableName,
                columnName
        );
        return count != null && count > 0;
    }
}
