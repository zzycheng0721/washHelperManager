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
            jdbcTemplate.execute("ALTER TABLE orders COMMENT = '订单表'");
            jdbcTemplate.execute("ALTER TABLE orders MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID'");
            jdbcTemplate.execute("ALTER TABLE orders MODIFY COLUMN order_id VARCHAR(50) COMMENT '订单编号'");
            jdbcTemplate.execute("ALTER TABLE orders MODIFY COLUMN customer_name VARCHAR(100) COMMENT '客户姓名'");
            jdbcTemplate.execute("ALTER TABLE orders MODIFY COLUMN customer_phone VARCHAR(20) COMMENT '客户手机号'");
            jdbcTemplate.execute("ALTER TABLE orders MODIFY COLUMN status VARCHAR(50) COMMENT '订单状态'");
            jdbcTemplate.execute("ALTER TABLE orders MODIFY COLUMN service VARCHAR(100) COMMENT '服务项目'");
            jdbcTemplate.execute("ALTER TABLE orders MODIFY COLUMN service_type VARCHAR(50) COMMENT '服务类型'");
            jdbcTemplate.execute("ALTER TABLE orders MODIFY COLUMN special_reqs TEXT COMMENT '特殊要求'");
            jdbcTemplate.execute("ALTER TABLE orders MODIFY COLUMN quantity INT COMMENT '数量'");
            jdbcTemplate.execute("ALTER TABLE orders MODIFY COLUMN created_at DATETIME(6) COMMENT '创建时间'");
            jdbcTemplate.execute("ALTER TABLE orders MODIFY COLUMN updated_at DATETIME(6) COMMENT '更新时间'");

            jdbcTemplate.execute("ALTER TABLE customers COMMENT = '客户表'");
            jdbcTemplate.execute("ALTER TABLE customers MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID'");
            jdbcTemplate.execute("ALTER TABLE customers MODIFY COLUMN customer_id VARCHAR(50) COMMENT '客户编号'");
            jdbcTemplate.execute("ALTER TABLE customers MODIFY COLUMN name VARCHAR(100) COMMENT '客户姓名'");
            jdbcTemplate.execute("ALTER TABLE customers MODIFY COLUMN phone VARCHAR(20) COMMENT '手机号'");
            jdbcTemplate.execute("ALTER TABLE customers MODIFY COLUMN member_type VARCHAR(50) COMMENT '会员类型'");
            jdbcTemplate.execute("ALTER TABLE customers MODIFY COLUMN notes TEXT COMMENT '备注'");
            jdbcTemplate.execute("ALTER TABLE customers MODIFY COLUMN created_at DATETIME(6) COMMENT '创建时间'");
            jdbcTemplate.execute("ALTER TABLE customers MODIFY COLUMN updated_at DATETIME(6) COMMENT '更新时间'");

            jdbcTemplate.execute("ALTER TABLE inventory COMMENT = '库存表'");
            jdbcTemplate.execute("ALTER TABLE inventory MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID'");
            jdbcTemplate.execute("ALTER TABLE inventory MODIFY COLUMN name VARCHAR(100) COMMENT '物料名称'");
            jdbcTemplate.execute("ALTER TABLE inventory MODIFY COLUMN category VARCHAR(50) COMMENT '物料分类'");
            jdbcTemplate.execute("ALTER TABLE inventory MODIFY COLUMN available INT COMMENT '可用数量'");
            jdbcTemplate.execute("ALTER TABLE inventory MODIFY COLUMN total INT COMMENT '总数量'");
            jdbcTemplate.execute("ALTER TABLE inventory MODIFY COLUMN unit VARCHAR(20) COMMENT '单位'");
            jdbcTemplate.execute("ALTER TABLE inventory MODIFY COLUMN alert_threshold INT COMMENT '预警阈值'");
            jdbcTemplate.execute("ALTER TABLE inventory MODIFY COLUMN notes TEXT COMMENT '备注'");
            jdbcTemplate.execute("ALTER TABLE inventory MODIFY COLUMN created_at DATETIME(6) COMMENT '创建时间'");
            jdbcTemplate.execute("ALTER TABLE inventory MODIFY COLUMN updated_at DATETIME(6) COMMENT '更新时间'");

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
