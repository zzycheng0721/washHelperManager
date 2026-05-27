package com.washhelper;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
@EnableCaching
public class WashHelperApplication {
    public static void main(String[] args) {
        SpringApplication.run(WashHelperApplication.class, args);
    }

    @Bean
    public CommandLineRunner applyTableComments(JdbcTemplate jdbcTemplate) {
        return args -> {
            String[] statements = {
                    "ALTER TABLE orders COMMENT = 'Orders'",
                    "ALTER TABLE orders MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary key'",
                    "ALTER TABLE orders MODIFY COLUMN shop_id BIGINT NOT NULL DEFAULT 1 COMMENT 'Shop/tenant ID'",
                    "ALTER TABLE orders MODIFY COLUMN order_id VARCHAR(50) COMMENT 'Order number'",
                    "ALTER TABLE orders MODIFY COLUMN customer_id BIGINT NULL DEFAULT NULL COMMENT 'Customer ID, references customers.id'",
                    "ALTER TABLE orders MODIFY COLUMN customer_name VARCHAR(100) COMMENT 'Customer name snapshot'",
                    "ALTER TABLE orders MODIFY COLUMN customer_phone VARCHAR(20) COMMENT 'Customer phone snapshot'",
                    "ALTER TABLE orders MODIFY COLUMN status VARCHAR(50) COMMENT 'Order status'",
                    "ALTER TABLE orders MODIFY COLUMN service VARCHAR(100) COMMENT 'Service item'",
                    "ALTER TABLE orders MODIFY COLUMN service_type VARCHAR(50) COMMENT 'Service type'",
                    "ALTER TABLE orders MODIFY COLUMN special_reqs TEXT COMMENT 'Special requirements'",
                    "ALTER TABLE orders MODIFY COLUMN quantity INT COMMENT 'Quantity'",
                    "ALTER TABLE orders MODIFY COLUMN created_at DATETIME(6) COMMENT 'Created time'",
                    "ALTER TABLE orders MODIFY COLUMN updated_at DATETIME(6) COMMENT 'Updated time'",

                    "ALTER TABLE customers COMMENT = 'Customers'",
                    "ALTER TABLE customers MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary key'",
                    "ALTER TABLE customers MODIFY COLUMN shop_id BIGINT NOT NULL DEFAULT 1 COMMENT 'Shop/tenant ID'",
                    "ALTER TABLE customers MODIFY COLUMN customer_id VARCHAR(50) COMMENT 'Customer number'",
                    "ALTER TABLE customers MODIFY COLUMN name VARCHAR(100) COMMENT 'Customer name'",
                    "ALTER TABLE customers MODIFY COLUMN phone VARCHAR(20) COMMENT 'Phone number'",
                    "ALTER TABLE customers MODIFY COLUMN member_type VARCHAR(50) COMMENT 'Member type'",
                    "ALTER TABLE customers MODIFY COLUMN notes TEXT COMMENT 'Notes'",
                    "ALTER TABLE customers MODIFY COLUMN deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Soft delete flag'",
                    "ALTER TABLE customers MODIFY COLUMN created_at DATETIME(6) COMMENT 'Created time'",
                    "ALTER TABLE customers MODIFY COLUMN updated_at DATETIME(6) COMMENT 'Updated time'",

                    "ALTER TABLE inventory COMMENT = 'Inventory'",
                    "ALTER TABLE inventory MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary key'",
                    "ALTER TABLE inventory MODIFY COLUMN shop_id BIGINT NOT NULL DEFAULT 1 COMMENT 'Shop/tenant ID'",
                    "ALTER TABLE inventory MODIFY COLUMN name VARCHAR(100) COMMENT 'Material name'",
                    "ALTER TABLE inventory MODIFY COLUMN category VARCHAR(50) COMMENT 'Material category'",
                    "ALTER TABLE inventory MODIFY COLUMN available INT COMMENT 'Available quantity'",
                    "ALTER TABLE inventory MODIFY COLUMN total INT COMMENT 'Total quantity'",
                    "ALTER TABLE inventory MODIFY COLUMN unit VARCHAR(20) COMMENT 'Unit'",
                    "ALTER TABLE inventory MODIFY COLUMN alert_threshold INT COMMENT 'Alert threshold'",
                    "ALTER TABLE inventory MODIFY COLUMN notes TEXT COMMENT 'Notes'",
                    "ALTER TABLE inventory MODIFY COLUMN created_at DATETIME(6) COMMENT 'Created time'",
                    "ALTER TABLE inventory MODIFY COLUMN updated_at DATETIME(6) COMMENT 'Updated time'"
            };

            for (String statement : statements) {
                jdbcTemplate.execute(statement);
            }

            Integer tableCommentCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME IN ('orders','customers','inventory') AND TABLE_COMMENT <> ''",
                    Integer.class
            );
            Integer columnCommentCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME IN ('orders','customers','inventory') AND COLUMN_COMMENT <> ''",
                    Integer.class
            );
            System.out.println("TABLE_COMMENT_COUNT=" + tableCommentCount);
            System.out.println("COLUMN_COMMENT_COUNT=" + columnCommentCount);
        };
    }
}
