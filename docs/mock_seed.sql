SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS=0;
DELETE FROM member_transactions; DELETE FROM member_wallets; DELETE FROM orders;
DELETE FROM employee_permissions; DELETE FROM employees; DELETE FROM customers;
DELETE FROM services; DELETE FROM inventory; DELETE FROM receipt_templates;
DELETE FROM shop_hours; DELETE FROM shops;
ALTER TABLE shops AUTO_INCREMENT=1; ALTER TABLE shop_hours AUTO_INCREMENT=1;
ALTER TABLE employees AUTO_INCREMENT=1; ALTER TABLE employee_permissions AUTO_INCREMENT=1;
ALTER TABLE customers AUTO_INCREMENT=1; ALTER TABLE member_wallets AUTO_INCREMENT=1;
ALTER TABLE member_transactions AUTO_INCREMENT=1; ALTER TABLE services AUTO_INCREMENT=1;
ALTER TABLE inventory AUTO_INCREMENT=1; ALTER TABLE orders AUTO_INCREMENT=1;
ALTER TABLE receipt_templates AUTO_INCREMENT=1;
SET FOREIGN_KEY_CHECKS=1;

-- ===== 门店 =====
INSERT INTO shops (id,name,address,phone,longitude,latitude,is_paused,pause_reason,logo_url,created_at,updated_at) VALUES (1,'WashHelper 总店（中关村）','北京市海淀区中关村大街18号B座1层','010-88886666',116.318610,39.984120,b'0',NULL,NULL,NOW(6),NOW(6));
INSERT INTO shops (id,name,address,phone,longitude,latitude,is_paused,pause_reason,logo_url,created_at,updated_at) VALUES (2,'WashHelper 分店（朝阳大悦城）','北京市朝阳区青年路69号1层','010-58886655',116.510250,39.918760,b'1','设备维护中',NULL,NOW(6),NOW(6));

-- ===== 营业时间（周一=1 ... 周日=7）=====
INSERT INTO shop_hours (shop_id,weekday,open_time,close_time,is_open,created_at,updated_at) VALUES (1,1,'09:00:00','21:00:00',b'1',NOW(6),NOW(6));
INSERT INTO shop_hours (shop_id,weekday,open_time,close_time,is_open,created_at,updated_at) VALUES (1,2,'09:00:00','21:00:00',b'1',NOW(6),NOW(6));
INSERT INTO shop_hours (shop_id,weekday,open_time,close_time,is_open,created_at,updated_at) VALUES (1,3,'09:00:00','21:00:00',b'1',NOW(6),NOW(6));
INSERT INTO shop_hours (shop_id,weekday,open_time,close_time,is_open,created_at,updated_at) VALUES (1,4,'09:00:00','21:00:00',b'1',NOW(6),NOW(6));
INSERT INTO shop_hours (shop_id,weekday,open_time,close_time,is_open,created_at,updated_at) VALUES (1,5,'09:00:00','21:00:00',b'1',NOW(6),NOW(6));
INSERT INTO shop_hours (shop_id,weekday,open_time,close_time,is_open,created_at,updated_at) VALUES (1,6,'10:00:00','22:00:00',b'1',NOW(6),NOW(6));
INSERT INTO shop_hours (shop_id,weekday,open_time,close_time,is_open,created_at,updated_at) VALUES (1,7,'10:00:00','20:00:00',b'0',NOW(6),NOW(6));
INSERT INTO shop_hours (shop_id,weekday,open_time,close_time,is_open,created_at,updated_at) VALUES (2,1,'09:00:00','21:00:00',b'1',NOW(6),NOW(6));
INSERT INTO shop_hours (shop_id,weekday,open_time,close_time,is_open,created_at,updated_at) VALUES (2,2,'09:00:00','21:00:00',b'1',NOW(6),NOW(6));
INSERT INTO shop_hours (shop_id,weekday,open_time,close_time,is_open,created_at,updated_at) VALUES (2,3,'09:00:00','21:00:00',b'1',NOW(6),NOW(6));
INSERT INTO shop_hours (shop_id,weekday,open_time,close_time,is_open,created_at,updated_at) VALUES (2,4,'09:00:00','21:00:00',b'1',NOW(6),NOW(6));
INSERT INTO shop_hours (shop_id,weekday,open_time,close_time,is_open,created_at,updated_at) VALUES (2,5,'09:00:00','21:00:00',b'1',NOW(6),NOW(6));
INSERT INTO shop_hours (shop_id,weekday,open_time,close_time,is_open,created_at,updated_at) VALUES (2,6,'10:00:00','22:00:00',b'1',NOW(6),NOW(6));
INSERT INTO shop_hours (shop_id,weekday,open_time,close_time,is_open,created_at,updated_at) VALUES (2,7,'10:00:00','20:00:00',b'0',NOW(6),NOW(6));

-- ===== 员工（统一密码 wash123）=====
INSERT INTO employees (id,shop_id,name,phone,role,status,password_hash,avatar_url,last_login_at,created_at,updated_at) VALUES (1,1,'张敬之','13800010001','admin','active','sha256$33bdc6b5c7787b9d2984cfd5dc03dc386c1213fdad8478ceae5daf19ecc7996f',NULL,DATE_SUB(NOW(6),INTERVAL 1 DAY),NOW(6),NOW(6));
INSERT INTO employees (id,shop_id,name,phone,role,status,password_hash,avatar_url,last_login_at,created_at,updated_at) VALUES (2,1,'李慧珊','13800010002','cashier','active','sha256$33bdc6b5c7787b9d2984cfd5dc03dc386c1213fdad8478ceae5daf19ecc7996f',NULL,DATE_SUB(NOW(6),INTERVAL 2 DAY),NOW(6),NOW(6));
INSERT INTO employees (id,shop_id,name,phone,role,status,password_hash,avatar_url,last_login_at,created_at,updated_at) VALUES (3,1,'王师傅','13800010003','worker','active','sha256$33bdc6b5c7787b9d2984cfd5dc03dc386c1213fdad8478ceae5daf19ecc7996f',NULL,DATE_SUB(NOW(6),INTERVAL 3 DAY),NOW(6),NOW(6));
INSERT INTO employees (id,shop_id,name,phone,role,status,password_hash,avatar_url,last_login_at,created_at,updated_at) VALUES (4,2,'赵明','13800020001','admin','active','sha256$33bdc6b5c7787b9d2984cfd5dc03dc386c1213fdad8478ceae5daf19ecc7996f',NULL,DATE_SUB(NOW(6),INTERVAL 4 DAY),NOW(6),NOW(6));
INSERT INTO employees (id,shop_id,name,phone,role,status,password_hash,avatar_url,last_login_at,created_at,updated_at) VALUES (5,2,'孙晓雨','13800020002','cashier','inactive','sha256$33bdc6b5c7787b9d2984cfd5dc03dc386c1213fdad8478ceae5daf19ecc7996f',NULL,DATE_SUB(NOW(6),INTERVAL 5 DAY),NOW(6),NOW(6));

-- ===== 员工权限 =====
INSERT INTO employee_permissions (employee_id,shop_id,perm_key,enabled) VALUES (1,1,'order.manage',b'1');
INSERT INTO employee_permissions (employee_id,shop_id,perm_key,enabled) VALUES (1,1,'customer.manage',b'1');
INSERT INTO employee_permissions (employee_id,shop_id,perm_key,enabled) VALUES (1,1,'employee.manage',b'1');
INSERT INTO employee_permissions (employee_id,shop_id,perm_key,enabled) VALUES (1,1,'service.manage',b'1');
INSERT INTO employee_permissions (employee_id,shop_id,perm_key,enabled) VALUES (1,1,'inventory.manage',b'1');
INSERT INTO employee_permissions (employee_id,shop_id,perm_key,enabled) VALUES (1,1,'wallet.manage',b'1');
INSERT INTO employee_permissions (employee_id,shop_id,perm_key,enabled) VALUES (1,1,'shop.manage',b'1');
INSERT INTO employee_permissions (employee_id,shop_id,perm_key,enabled) VALUES (1,1,'report.view',b'1');
INSERT INTO employee_permissions (employee_id,shop_id,perm_key,enabled) VALUES (2,1,'order.manage',b'1');
INSERT INTO employee_permissions (employee_id,shop_id,perm_key,enabled) VALUES (2,1,'customer.manage',b'1');
INSERT INTO employee_permissions (employee_id,shop_id,perm_key,enabled) VALUES (2,1,'wallet.manage',b'1');
INSERT INTO employee_permissions (employee_id,shop_id,perm_key,enabled) VALUES (2,1,'service.view',b'1');
INSERT INTO employee_permissions (employee_id,shop_id,perm_key,enabled) VALUES (3,1,'order.process',b'1');
INSERT INTO employee_permissions (employee_id,shop_id,perm_key,enabled) VALUES (3,1,'inventory.view',b'1');
INSERT INTO employee_permissions (employee_id,shop_id,perm_key,enabled) VALUES (4,2,'order.manage',b'1');
INSERT INTO employee_permissions (employee_id,shop_id,perm_key,enabled) VALUES (4,2,'customer.manage',b'1');
INSERT INTO employee_permissions (employee_id,shop_id,perm_key,enabled) VALUES (4,2,'employee.manage',b'1');
INSERT INTO employee_permissions (employee_id,shop_id,perm_key,enabled) VALUES (4,2,'service.manage',b'1');
INSERT INTO employee_permissions (employee_id,shop_id,perm_key,enabled) VALUES (4,2,'inventory.manage',b'1');
INSERT INTO employee_permissions (employee_id,shop_id,perm_key,enabled) VALUES (4,2,'wallet.manage',b'1');
INSERT INTO employee_permissions (employee_id,shop_id,perm_key,enabled) VALUES (4,2,'shop.manage',b'1');
INSERT INTO employee_permissions (employee_id,shop_id,perm_key,enabled) VALUES (4,2,'report.view',b'1');
INSERT INTO employee_permissions (employee_id,shop_id,perm_key,enabled) VALUES (5,2,'order.manage',b'1');
INSERT INTO employee_permissions (employee_id,shop_id,perm_key,enabled) VALUES (5,2,'customer.manage',b'1');
INSERT INTO employee_permissions (employee_id,shop_id,perm_key,enabled) VALUES (5,2,'wallet.manage',b'1');
INSERT INTO employee_permissions (employee_id,shop_id,perm_key,enabled) VALUES (5,2,'service.view',b'1');

-- ===== 服务项目 =====
INSERT INTO services (id,shop_id,name,category,price,eta_text,notes,is_active,created_at,updated_at) VALUES (1,1,'衬衫水洗','wash',15,'次日取','含熨烫',b'1',NOW(6),NOW(6));
INSERT INTO services (id,shop_id,name,category,price,eta_text,notes,is_active,created_at,updated_at) VALUES (2,1,'毛呢大衣干洗','dryClean',68,'3天','重污渍预处理',b'1',NOW(6),NOW(6));
INSERT INTO services (id,shop_id,name,category,price,eta_text,notes,is_active,created_at,updated_at) VALUES (3,1,'羽绒服干洗','dryClean',88,'3-5天','含防水护理',b'1',NOW(6),NOW(6));
INSERT INTO services (id,shop_id,name,category,price,eta_text,notes,is_active,created_at,updated_at) VALUES (4,1,'真丝衬衣护理','care',58,'2天','手洗+低温熨烫',b'1',NOW(6),NOW(6));
INSERT INTO services (id,shop_id,name,category,price,eta_text,notes,is_active,created_at,updated_at) VALUES (5,1,'运动鞋护理','shoes',45,'2天','清洁+消毒',b'1',NOW(6),NOW(6));
INSERT INTO services (id,shop_id,name,category,price,eta_text,notes,is_active,created_at,updated_at) VALUES (6,1,'皮包深度护理','care',180,'5-7天','补色+上油',b'1',NOW(6),NOW(6));
INSERT INTO services (id,shop_id,name,category,price,eta_text,notes,is_active,created_at,updated_at) VALUES (7,2,'标准水洗（公斤）','wash',12,'次日取','按重量计费',b'1',NOW(6),NOW(6));
INSERT INTO services (id,shop_id,name,category,price,eta_text,notes,is_active,created_at,updated_at) VALUES (8,2,'窗帘清洗','other',25,'3天','按平方计费',b'1',NOW(6),NOW(6));

-- ===== 库存物料 =====
INSERT INTO inventory (id,shop_id,name,category,unit,total,available,alert_threshold,notes,created_at,updated_at) VALUES (1,1,'干洗剂A','洗涤耗材','瓶',50,12,15,'周转较快',NOW(6),NOW(6));
INSERT INTO inventory (id,shop_id,name,category,unit,total,available,alert_threshold,notes,created_at,updated_at) VALUES (2,1,'漂白剂','洗涤耗材','瓶',30,22,8,'',NOW(6),NOW(6));
INSERT INTO inventory (id,shop_id,name,category,unit,total,available,alert_threshold,notes,created_at,updated_at) VALUES (3,1,'衣架','包装耗材','个',500,320,100,'',NOW(6),NOW(6));
INSERT INTO inventory (id,shop_id,name,category,unit,total,available,alert_threshold,notes,created_at,updated_at) VALUES (4,1,'防尘袋','包装耗材','个',400,80,120,'需补货',NOW(6),NOW(6));
INSERT INTO inventory (id,shop_id,name,category,unit,total,available,alert_threshold,notes,created_at,updated_at) VALUES (5,1,'挂签','包装耗材','张',2000,950,300,'',NOW(6),NOW(6));
INSERT INTO inventory (id,shop_id,name,category,unit,total,available,alert_threshold,notes,created_at,updated_at) VALUES (6,1,'烫台蒸汽喷头','配件','个',8,2,3,'损耗较高',NOW(6),NOW(6));
INSERT INTO inventory (id,shop_id,name,category,unit,total,available,alert_threshold,notes,created_at,updated_at) VALUES (7,1,'工业洗衣机','设备','台',4,4,1,'年检合格',NOW(6),NOW(6));
INSERT INTO inventory (id,shop_id,name,category,unit,total,available,alert_threshold,notes,created_at,updated_at) VALUES (8,2,'干洗剂A','洗涤耗材','瓶',40,18,10,'',NOW(6),NOW(6));
INSERT INTO inventory (id,shop_id,name,category,unit,total,available,alert_threshold,notes,created_at,updated_at) VALUES (9,2,'防尘袋','包装耗材','个',300,210,80,'',NOW(6),NOW(6));
INSERT INTO inventory (id,shop_id,name,category,unit,total,available,alert_threshold,notes,created_at,updated_at) VALUES (10,2,'烘干机','设备','台',2,2,1,'',NOW(6),NOW(6));

-- ===== 客户 =====
INSERT INTO customers (id,shop_id,customer_id,name,phone,member_type,notes,avatar_url,deleted,created_at,updated_at) VALUES (1,1,'C-0001','陈一鸣','13911110001','regular','上班族，偏好次日取',NULL,0,NOW(6),NOW(6));
INSERT INTO customers (id,shop_id,customer_id,name,phone,member_type,notes,avatar_url,deleted,created_at,updated_at) VALUES (2,1,'C-0002','周晓彤','13911110002','silver','对羊毛敏感',NULL,0,NOW(6),NOW(6));
INSERT INTO customers (id,shop_id,customer_id,name,phone,member_type,notes,avatar_url,deleted,created_at,updated_at) VALUES (3,1,'C-0003','刘振华','13911110003','gold','高端商务，常洗西装',NULL,0,NOW(6),NOW(6));
INSERT INTO customers (id,shop_id,customer_id,name,phone,member_type,notes,avatar_url,deleted,created_at,updated_at) VALUES (4,1,'C-0004','韩美琪','13911110004','gold','全家会员',NULL,0,NOW(6),NOW(6));
INSERT INTO customers (id,shop_id,customer_id,name,phone,member_type,notes,avatar_url,deleted,created_at,updated_at) VALUES (5,1,'C-0005','郑思齐','13911110005','silver','',NULL,0,NOW(6),NOW(6));
INSERT INTO customers (id,shop_id,customer_id,name,phone,member_type,notes,avatar_url,deleted,created_at,updated_at) VALUES (6,1,'C-0006','钱小满','13911110006','regular','',NULL,0,NOW(6),NOW(6));
INSERT INTO customers (id,shop_id,customer_id,name,phone,member_type,notes,avatar_url,deleted,created_at,updated_at) VALUES (7,1,'C-0007','杨柳','13911110007','regular','偏好微信通知',NULL,0,NOW(6),NOW(6));
INSERT INTO customers (id,shop_id,customer_id,name,phone,member_type,notes,avatar_url,deleted,created_at,updated_at) VALUES (8,1,'C-0008','吴海洋','13911110008','silver','',NULL,0,NOW(6),NOW(6));
INSERT INTO customers (id,shop_id,customer_id,name,phone,member_type,notes,avatar_url,deleted,created_at,updated_at) VALUES (9,2,'C-1001','秦风','13922220001','regular','',NULL,0,NOW(6),NOW(6));
INSERT INTO customers (id,shop_id,customer_id,name,phone,member_type,notes,avatar_url,deleted,created_at,updated_at) VALUES (10,2,'C-1002','宋雨晴','13922220002','silver','',NULL,0,NOW(6),NOW(6));
INSERT INTO customers (id,shop_id,customer_id,name,phone,member_type,notes,avatar_url,deleted,created_at,updated_at) VALUES (11,2,'C-1003','董文博','13922220003','gold','常洗皮包',NULL,0,NOW(6),NOW(6));
INSERT INTO customers (id,shop_id,customer_id,name,phone,member_type,notes,avatar_url,deleted,created_at,updated_at) VALUES (12,2,'C-1004','唐果儿','13922220004','regular','',NULL,0,NOW(6),NOW(6));

-- ===== 会员钱包（仅 silver/gold 拥有）=====
INSERT INTO member_wallets (id,customer_id,shop_id,balance,last_recharge_at,created_at,updated_at) VALUES (1,2,1,320.5,DATE_SUB(NOW(6),INTERVAL 1 DAY),NOW(6),NOW(6));
INSERT INTO member_wallets (id,customer_id,shop_id,balance,last_recharge_at,created_at,updated_at) VALUES (2,3,1,1280,DATE_SUB(NOW(6),INTERVAL 2 DAY),NOW(6),NOW(6));
INSERT INTO member_wallets (id,customer_id,shop_id,balance,last_recharge_at,created_at,updated_at) VALUES (3,4,1,860.4,DATE_SUB(NOW(6),INTERVAL 3 DAY),NOW(6),NOW(6));
INSERT INTO member_wallets (id,customer_id,shop_id,balance,last_recharge_at,created_at,updated_at) VALUES (4,5,1,150,DATE_SUB(NOW(6),INTERVAL 4 DAY),NOW(6),NOW(6));
INSERT INTO member_wallets (id,customer_id,shop_id,balance,last_recharge_at,created_at,updated_at) VALUES (5,8,1,420,DATE_SUB(NOW(6),INTERVAL 5 DAY),NOW(6),NOW(6));
INSERT INTO member_wallets (id,customer_id,shop_id,balance,last_recharge_at,created_at,updated_at) VALUES (6,10,2,200,DATE_SUB(NOW(6),INTERVAL 6 DAY),NOW(6),NOW(6));
INSERT INTO member_wallets (id,customer_id,shop_id,balance,last_recharge_at,created_at,updated_at) VALUES (7,11,2,1740,DATE_SUB(NOW(6),INTERVAL 7 DAY),NOW(6),NOW(6));

-- ===== 订单（覆盖所有状态阶段）=====
INSERT INTO orders (id,shop_id,order_id,customer_id,customer_name,customer_phone,customer_member_type,service,service_type,quantity,unit_price,total_price,status,special_reqs,pending_at,washing_at,pickup_at,completed_at,created_at,updated_at) SELECT 1,1,'ORD-1-00001',c.id,c.name,c.phone,c.member_type,'衬衫水洗 x5','wash',5,15,75,'pending','袖口顽渍',DATE_SUB(NOW(6),INTERVAL 0 DAY),NULL,NULL,NULL,DATE_SUB(NOW(6),INTERVAL 0 DAY),NOW(6) FROM customers c WHERE c.id=1;
INSERT INTO orders (id,shop_id,order_id,customer_id,customer_name,customer_phone,customer_member_type,service,service_type,quantity,unit_price,total_price,status,special_reqs,pending_at,washing_at,pickup_at,completed_at,created_at,updated_at) SELECT 2,1,'ORD-1-00002',c.id,c.name,c.phone,c.member_type,'毛呢大衣干洗','dryClean',1,68,68,'washing','',DATE_SUB(NOW(6),INTERVAL 1 DAY),DATE_SUB(NOW(6),INTERVAL 0 DAY),NULL,NULL,DATE_SUB(NOW(6),INTERVAL 1 DAY),NOW(6) FROM customers c WHERE c.id=2;
INSERT INTO orders (id,shop_id,order_id,customer_id,customer_name,customer_phone,customer_member_type,service,service_type,quantity,unit_price,total_price,status,special_reqs,pending_at,washing_at,pickup_at,completed_at,created_at,updated_at) SELECT 3,1,'ORD-1-00003',c.id,c.name,c.phone,c.member_type,'西装两件套干洗','dryClean',2,120,240,'pickup','烫线挺括',DATE_SUB(NOW(6),INTERVAL 2 DAY),DATE_SUB(NOW(6),INTERVAL 1 DAY),DATE_SUB(NOW(6),INTERVAL 0 DAY),NULL,DATE_SUB(NOW(6),INTERVAL 2 DAY),NOW(6) FROM customers c WHERE c.id=3;
INSERT INTO orders (id,shop_id,order_id,customer_id,customer_name,customer_phone,customer_member_type,service,service_type,quantity,unit_price,total_price,status,special_reqs,pending_at,washing_at,pickup_at,completed_at,created_at,updated_at) SELECT 4,1,'ORD-1-00004',c.id,c.name,c.phone,c.member_type,'羽绒服干洗','dryClean',1,88,88,'completed','',DATE_SUB(NOW(6),INTERVAL 10 DAY),DATE_SUB(NOW(6),INTERVAL 9 DAY),DATE_SUB(NOW(6),INTERVAL 8 DAY),DATE_SUB(NOW(6),INTERVAL 7 DAY),DATE_SUB(NOW(6),INTERVAL 10 DAY),NOW(6) FROM customers c WHERE c.id=3;
INSERT INTO orders (id,shop_id,order_id,customer_id,customer_name,customer_phone,customer_member_type,service,service_type,quantity,unit_price,total_price,status,special_reqs,pending_at,washing_at,pickup_at,completed_at,created_at,updated_at) SELECT 5,1,'ORD-1-00005',c.id,c.name,c.phone,c.member_type,'真丝衬衣护理','care',3,58,174,'completed','手洗+低温',DATE_SUB(NOW(6),INTERVAL 12 DAY),DATE_SUB(NOW(6),INTERVAL 11 DAY),DATE_SUB(NOW(6),INTERVAL 10 DAY),DATE_SUB(NOW(6),INTERVAL 9 DAY),DATE_SUB(NOW(6),INTERVAL 12 DAY),NOW(6) FROM customers c WHERE c.id=4;
INSERT INTO orders (id,shop_id,order_id,customer_id,customer_name,customer_phone,customer_member_type,service,service_type,quantity,unit_price,total_price,status,special_reqs,pending_at,washing_at,pickup_at,completed_at,created_at,updated_at) SELECT 6,1,'ORD-1-00006',c.id,c.name,c.phone,c.member_type,'皮包深度护理','care',1,180,180,'pickup','补色',DATE_SUB(NOW(6),INTERVAL 3 DAY),DATE_SUB(NOW(6),INTERVAL 2 DAY),DATE_SUB(NOW(6),INTERVAL 1 DAY),NULL,DATE_SUB(NOW(6),INTERVAL 3 DAY),NOW(6) FROM customers c WHERE c.id=4;
INSERT INTO orders (id,shop_id,order_id,customer_id,customer_name,customer_phone,customer_member_type,service,service_type,quantity,unit_price,total_price,status,special_reqs,pending_at,washing_at,pickup_at,completed_at,created_at,updated_at) SELECT 7,1,'ORD-1-00007',c.id,c.name,c.phone,c.member_type,'运动鞋护理','shoes',2,45,90,'washing','鞋底深清',DATE_SUB(NOW(6),INTERVAL 1 DAY),DATE_SUB(NOW(6),INTERVAL 0 DAY),NULL,NULL,DATE_SUB(NOW(6),INTERVAL 1 DAY),NOW(6) FROM customers c WHERE c.id=5;
INSERT INTO orders (id,shop_id,order_id,customer_id,customer_name,customer_phone,customer_member_type,service,service_type,quantity,unit_price,total_price,status,special_reqs,pending_at,washing_at,pickup_at,completed_at,created_at,updated_at) SELECT 8,1,'ORD-1-00008',c.id,c.name,c.phone,c.member_type,'毛呢大衣干洗','dryClean',1,68,68,'cancelled','客户改约',DATE_SUB(NOW(6),INTERVAL 4 DAY),NULL,NULL,NULL,DATE_SUB(NOW(6),INTERVAL 4 DAY),NOW(6) FROM customers c WHERE c.id=6;
INSERT INTO orders (id,shop_id,order_id,customer_id,customer_name,customer_phone,customer_member_type,service,service_type,quantity,unit_price,total_price,status,special_reqs,pending_at,washing_at,pickup_at,completed_at,created_at,updated_at) SELECT 9,1,'ORD-1-00009',c.id,c.name,c.phone,c.member_type,'衬衫水洗 x3','wash',3,15,45,'pending','',DATE_SUB(NOW(6),INTERVAL 0 DAY),NULL,NULL,NULL,DATE_SUB(NOW(6),INTERVAL 0 DAY),NOW(6) FROM customers c WHERE c.id=7;
INSERT INTO orders (id,shop_id,order_id,customer_id,customer_name,customer_phone,customer_member_type,service,service_type,quantity,unit_price,total_price,status,special_reqs,pending_at,washing_at,pickup_at,completed_at,created_at,updated_at) SELECT 10,1,'ORD-1-00010',c.id,c.name,c.phone,c.member_type,'窗帘清洗（4平方）','other',4,25,100,'completed','预约上门',DATE_SUB(NOW(6),INTERVAL 20 DAY),DATE_SUB(NOW(6),INTERVAL 19 DAY),DATE_SUB(NOW(6),INTERVAL 18 DAY),DATE_SUB(NOW(6),INTERVAL 17 DAY),DATE_SUB(NOW(6),INTERVAL 20 DAY),NOW(6) FROM customers c WHERE c.id=8;
INSERT INTO orders (id,shop_id,order_id,customer_id,customer_name,customer_phone,customer_member_type,service,service_type,quantity,unit_price,total_price,status,special_reqs,pending_at,washing_at,pickup_at,completed_at,created_at,updated_at) SELECT 11,2,'ORD-2-00011',c.id,c.name,c.phone,c.member_type,'标准水洗 3kg','wash',3,12,36,'pending','',DATE_SUB(NOW(6),INTERVAL 0 DAY),NULL,NULL,NULL,DATE_SUB(NOW(6),INTERVAL 0 DAY),NOW(6) FROM customers c WHERE c.id=9;
INSERT INTO orders (id,shop_id,order_id,customer_id,customer_name,customer_phone,customer_member_type,service,service_type,quantity,unit_price,total_price,status,special_reqs,pending_at,washing_at,pickup_at,completed_at,created_at,updated_at) SELECT 12,2,'ORD-2-00012',c.id,c.name,c.phone,c.member_type,'标准水洗 5kg','wash',5,12,60,'washing','',DATE_SUB(NOW(6),INTERVAL 1 DAY),DATE_SUB(NOW(6),INTERVAL 0 DAY),NULL,NULL,DATE_SUB(NOW(6),INTERVAL 1 DAY),NOW(6) FROM customers c WHERE c.id=10;
INSERT INTO orders (id,shop_id,order_id,customer_id,customer_name,customer_phone,customer_member_type,service,service_type,quantity,unit_price,total_price,status,special_reqs,pending_at,washing_at,pickup_at,completed_at,created_at,updated_at) SELECT 13,2,'ORD-2-00013',c.id,c.name,c.phone,c.member_type,'皮包深度护理','care',1,180,180,'completed','重点清洁',DATE_SUB(NOW(6),INTERVAL 8 DAY),DATE_SUB(NOW(6),INTERVAL 7 DAY),DATE_SUB(NOW(6),INTERVAL 6 DAY),DATE_SUB(NOW(6),INTERVAL 5 DAY),DATE_SUB(NOW(6),INTERVAL 8 DAY),NOW(6) FROM customers c WHERE c.id=11;
INSERT INTO orders (id,shop_id,order_id,customer_id,customer_name,customer_phone,customer_member_type,service,service_type,quantity,unit_price,total_price,status,special_reqs,pending_at,washing_at,pickup_at,completed_at,created_at,updated_at) SELECT 14,2,'ORD-2-00014',c.id,c.name,c.phone,c.member_type,'毛呢大衣干洗','dryClean',2,68,136,'pickup','',DATE_SUB(NOW(6),INTERVAL 3 DAY),DATE_SUB(NOW(6),INTERVAL 2 DAY),DATE_SUB(NOW(6),INTERVAL 1 DAY),NULL,DATE_SUB(NOW(6),INTERVAL 3 DAY),NOW(6) FROM customers c WHERE c.id=11;
INSERT INTO orders (id,shop_id,order_id,customer_id,customer_name,customer_phone,customer_member_type,service,service_type,quantity,unit_price,total_price,status,special_reqs,pending_at,washing_at,pickup_at,completed_at,created_at,updated_at) SELECT 15,2,'ORD-2-00015',c.id,c.name,c.phone,c.member_type,'窗帘清洗','other',6,25,150,'cancelled','临时取消',DATE_SUB(NOW(6),INTERVAL 2 DAY),NULL,NULL,NULL,DATE_SUB(NOW(6),INTERVAL 2 DAY),NOW(6) FROM customers c WHERE c.id=12;

-- ===== 会员钱包流水（recharge / consume）=====
INSERT INTO member_transactions (customer_id,shop_id,type,amount,balance_after,payment_method,order_id,remark,created_at) VALUES (2,1,'recharge',500,500,'wechat',NULL,'首次充值送50',DATE_SUB(NOW(6),INTERVAL 30 DAY));
INSERT INTO member_transactions (customer_id,shop_id,type,amount,balance_after,payment_method,order_id,remark,created_at) VALUES (2,1,'consume',-68,432,'wallet',2,'毛呢大衣干洗',DATE_SUB(NOW(6),INTERVAL 1 DAY));
INSERT INTO member_transactions (customer_id,shop_id,type,amount,balance_after,payment_method,order_id,remark,created_at) VALUES (2,1,'consume',-111.5,320.5,'wallet',NULL,'历史消费',DATE_SUB(NOW(6),INTERVAL 10 DAY));
INSERT INTO member_transactions (customer_id,shop_id,type,amount,balance_after,payment_method,order_id,remark,created_at) VALUES (3,1,'recharge',1000,1000,'alipay',NULL,'金卡续费',DATE_SUB(NOW(6),INTERVAL 45 DAY));
INSERT INTO member_transactions (customer_id,shop_id,type,amount,balance_after,payment_method,order_id,remark,created_at) VALUES (3,1,'recharge',500,1500,'wechat',NULL,'充值送80',DATE_SUB(NOW(6),INTERVAL 15 DAY));
INSERT INTO member_transactions (customer_id,shop_id,type,amount,balance_after,payment_method,order_id,remark,created_at) VALUES (3,1,'consume',-88,1412,'wallet',4,'羽绒服干洗',DATE_SUB(NOW(6),INTERVAL 10 DAY));
INSERT INTO member_transactions (customer_id,shop_id,type,amount,balance_after,payment_method,order_id,remark,created_at) VALUES (3,1,'consume',-132,1280,'wallet',NULL,'历史消费',DATE_SUB(NOW(6),INTERVAL 5 DAY));
INSERT INTO member_transactions (customer_id,shop_id,type,amount,balance_after,payment_method,order_id,remark,created_at) VALUES (4,1,'recharge',1000,1000,'wechat',NULL,'金卡新办',DATE_SUB(NOW(6),INTERVAL 40 DAY));
INSERT INTO member_transactions (customer_id,shop_id,type,amount,balance_after,payment_method,order_id,remark,created_at) VALUES (4,1,'consume',-139.6,860.4,'wallet',5,'真丝衬衣护理 x3',DATE_SUB(NOW(6),INTERVAL 12 DAY));
INSERT INTO member_transactions (customer_id,shop_id,type,amount,balance_after,payment_method,order_id,remark,created_at) VALUES (5,1,'recharge',200,200,'cash',NULL,'银卡充值',DATE_SUB(NOW(6),INTERVAL 20 DAY));
INSERT INTO member_transactions (customer_id,shop_id,type,amount,balance_after,payment_method,order_id,remark,created_at) VALUES (5,1,'consume',-50,150,'wallet',NULL,'零散消费',DATE_SUB(NOW(6),INTERVAL 4 DAY));
INSERT INTO member_transactions (customer_id,shop_id,type,amount,balance_after,payment_method,order_id,remark,created_at) VALUES (8,1,'recharge',500,500,'wechat',NULL,'银卡充值',DATE_SUB(NOW(6),INTERVAL 25 DAY));
INSERT INTO member_transactions (customer_id,shop_id,type,amount,balance_after,payment_method,order_id,remark,created_at) VALUES (8,1,'consume',-80,420,'wallet',10,'窗帘清洗 4平方',DATE_SUB(NOW(6),INTERVAL 20 DAY));
INSERT INTO member_transactions (customer_id,shop_id,type,amount,balance_after,payment_method,order_id,remark,created_at) VALUES (10,2,'recharge',300,300,'alipay',NULL,'银卡充值',DATE_SUB(NOW(6),INTERVAL 18 DAY));
INSERT INTO member_transactions (customer_id,shop_id,type,amount,balance_after,payment_method,order_id,remark,created_at) VALUES (10,2,'consume',-100,200,'wallet',NULL,'历史消费',DATE_SUB(NOW(6),INTERVAL 6 DAY));
INSERT INTO member_transactions (customer_id,shop_id,type,amount,balance_after,payment_method,order_id,remark,created_at) VALUES (11,2,'recharge',2000,2000,'wechat',NULL,'金卡新办送200',DATE_SUB(NOW(6),INTERVAL 35 DAY));
INSERT INTO member_transactions (customer_id,shop_id,type,amount,balance_after,payment_method,order_id,remark,created_at) VALUES (11,2,'consume',-180,1820,'wallet',13,'皮包深度护理',DATE_SUB(NOW(6),INTERVAL 8 DAY));
INSERT INTO member_transactions (customer_id,shop_id,type,amount,balance_after,payment_method,order_id,remark,created_at) VALUES (11,2,'consume',-80,1740,'wallet',NULL,'零散消费',DATE_SUB(NOW(6),INTERVAL 3 DAY));

-- ===== 小票模板 =====
INSERT INTO receipt_templates (shop_id,paper_width,header_text,footer_text,logo_url,show_logo,show_customer_name,show_wash_instructions,updated_at) VALUES (1,'80mm','WashHelper 总店（中关村）','谢谢惠顾，欢迎再次光临！',NULL,b'1',b'1',b'1',NOW(6));
INSERT INTO receipt_templates (shop_id,paper_width,header_text,footer_text,logo_url,show_logo,show_customer_name,show_wash_instructions,updated_at) VALUES (2,'58mm','WashHelper 朝阳店','取衣请凭票，遗失需身份核验',NULL,b'0',b'1',b'1',NOW(6));
