/*
 Multi-tenant table design migration.

 Design:
 - shops is the tenant root table.
 - Tenant-scoped business tables carry shop_id.
 - orders.customer_id is the customer relation. customer_name/customer_phone remain as
   historical display snapshots only.

 Target database: MariaDB 10.11+.
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

ALTER TABLE customers
  ADD COLUMN IF NOT EXISTS shop_id BIGINT NOT NULL DEFAULT 1 COMMENT 'Shop/tenant ID' AFTER id,
  ADD COLUMN IF NOT EXISTS avatar_url VARCHAR(255) NULL DEFAULT NULL COMMENT 'Avatar URL' AFTER notes,
  ADD COLUMN IF NOT EXISTS deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Soft delete flag' AFTER avatar_url;

ALTER TABLE shops
  ADD COLUMN IF NOT EXISTS latitude DECIMAL(10, 6) NULL DEFAULT NULL COMMENT 'Latitude' AFTER logo_url,
  ADD COLUMN IF NOT EXISTS longitude DECIMAL(10, 6) NULL DEFAULT NULL COMMENT 'Longitude' AFTER latitude;

ALTER TABLE employees
  ADD COLUMN IF NOT EXISTS shop_id BIGINT NOT NULL DEFAULT 1 COMMENT 'Shop/tenant ID' AFTER id;

ALTER TABLE employee_permissions
  ADD COLUMN IF NOT EXISTS shop_id BIGINT NOT NULL DEFAULT 1 COMMENT 'Shop/tenant ID' AFTER id;

ALTER TABLE inventory
  ADD COLUMN IF NOT EXISTS shop_id BIGINT NOT NULL DEFAULT 1 COMMENT 'Shop/tenant ID' AFTER id;

ALTER TABLE inventory_items
  ADD COLUMN IF NOT EXISTS shop_id BIGINT NOT NULL DEFAULT 1 COMMENT 'Shop/tenant ID' AFTER id;

ALTER TABLE inventory_transactions
  ADD COLUMN IF NOT EXISTS shop_id BIGINT NOT NULL DEFAULT 1 COMMENT 'Shop/tenant ID' AFTER id;

ALTER TABLE member_wallets
  ADD COLUMN IF NOT EXISTS shop_id BIGINT NOT NULL DEFAULT 1 COMMENT 'Shop/tenant ID' AFTER id;

ALTER TABLE member_transactions
  ADD COLUMN IF NOT EXISTS shop_id BIGINT NOT NULL DEFAULT 1 COMMENT 'Shop/tenant ID' AFTER id;

ALTER TABLE orders
  ADD COLUMN IF NOT EXISTS shop_id BIGINT NOT NULL DEFAULT 1 COMMENT 'Shop/tenant ID' AFTER id,
  MODIFY COLUMN customer_id BIGINT NULL DEFAULT NULL COMMENT 'Customer ID, references customers.id';

ALTER TABLE order_items
  ADD COLUMN IF NOT EXISTS shop_id BIGINT NOT NULL DEFAULT 1 COMMENT 'Shop/tenant ID' AFTER id;

ALTER TABLE order_status_logs
  ADD COLUMN IF NOT EXISTS shop_id BIGINT NOT NULL DEFAULT 1 COMMENT 'Shop/tenant ID' AFTER id;

ALTER TABLE printers
  ADD COLUMN IF NOT EXISTS shop_id BIGINT NOT NULL DEFAULT 1 COMMENT 'Shop/tenant ID' AFTER id;

ALTER TABLE printer_settings
  ADD COLUMN IF NOT EXISTS shop_id BIGINT NOT NULL DEFAULT 1 COMMENT 'Shop/tenant ID' AFTER id;

ALTER TABLE services
  ADD COLUMN IF NOT EXISTS shop_id BIGINT NOT NULL DEFAULT 1 COMMENT 'Shop/tenant ID' AFTER id;

-- Backfill tenant data through parent relationships where possible.
UPDATE employee_permissions p
JOIN employees e ON e.id = p.employee_id
SET p.shop_id = e.shop_id;

UPDATE member_wallets w
JOIN customers c ON c.id = w.customer_id
SET w.shop_id = c.shop_id;

UPDATE member_transactions t
JOIN customers c ON c.id = t.customer_id
SET t.shop_id = c.shop_id;

UPDATE orders o
JOIN customers c ON c.id = o.customer_id
SET o.shop_id = c.shop_id;

UPDATE orders o
JOIN customers c
  ON o.customer_id IS NULL
 AND o.customer_phone IS NOT NULL
 AND o.customer_phone = c.phone
SET o.customer_id = c.id,
    o.shop_id = c.shop_id;

UPDATE order_items oi
JOIN orders o ON o.id = oi.order_id
SET oi.shop_id = o.shop_id;

UPDATE order_status_logs l
JOIN orders o ON o.id = l.order_id
SET l.shop_id = o.shop_id;

UPDATE inventory_transactions t
JOIN inventory_items i ON i.id = t.item_id
SET t.shop_id = i.shop_id;

UPDATE printer_settings s
JOIN printers p ON p.id = s.printer_id
SET s.shop_id = p.shop_id;

-- Replace global business-code uniqueness with tenant-scoped uniqueness.
DROP INDEX IF EXISTS UK_n6axkf7qwn8r7s7ce5gso1xpr ON customers;
CREATE UNIQUE INDEX IF NOT EXISTS uk_customers_shop_customer_id ON customers (shop_id, customer_id);
CREATE INDEX IF NOT EXISTS idx_customers_shop_search ON customers (shop_id, name, phone);

DROP INDEX IF EXISTS UK_hmsk25beh6atojvle1xuymjj0 ON orders;
CREATE UNIQUE INDEX IF NOT EXISTS uk_orders_shop_order_id ON orders (shop_id, order_id);
CREATE INDEX IF NOT EXISTS idx_orders_shop_customer ON orders (shop_id, customer_id);
CREATE INDEX IF NOT EXISTS idx_orders_shop_status ON orders (shop_id, status);

CREATE INDEX IF NOT EXISTS idx_employees_shop_role_status ON employees (shop_id, role, status);
CREATE INDEX IF NOT EXISTS idx_employee_permissions_shop_employee ON employee_permissions (shop_id, employee_id);
CREATE INDEX IF NOT EXISTS idx_inventory_shop_category ON inventory (shop_id, category);
CREATE INDEX IF NOT EXISTS idx_inventory_items_shop_category ON inventory_items (shop_id, category);
CREATE INDEX IF NOT EXISTS idx_inventory_tx_shop_item_created ON inventory_transactions (shop_id, item_id, created_at);

DROP INDEX IF EXISTS uk_wallet_customer ON member_wallets;
CREATE UNIQUE INDEX IF NOT EXISTS uk_wallet_shop_customer ON member_wallets (shop_id, customer_id);
CREATE INDEX IF NOT EXISTS idx_member_tx_shop_customer_created ON member_transactions (shop_id, customer_id, created_at);

CREATE INDEX IF NOT EXISTS idx_order_items_shop_order ON order_items (shop_id, order_id);
CREATE INDEX IF NOT EXISTS idx_order_logs_shop_order_changed ON order_status_logs (shop_id, order_id, changed_at);
CREATE INDEX IF NOT EXISTS idx_printers_shop_default ON printers (shop_id, is_default);
CREATE INDEX IF NOT EXISTS idx_printer_settings_shop_printer ON printer_settings (shop_id, printer_id);
CREATE INDEX IF NOT EXISTS idx_services_shop_category_active ON services (shop_id, category, is_active);
CREATE UNIQUE INDEX IF NOT EXISTS uk_shop_hours_shop_weekday ON shop_hours (shop_id, weekday);
CREATE UNIQUE INDEX IF NOT EXISTS uk_receipt_templates_shop ON receipt_templates (shop_id);

SET FOREIGN_KEY_CHECKS = 1;
