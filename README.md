# WashHelper - 洗衣店后台管理系统

WashHelper 是一套面向中小型洗衣店的后台管理系统，提供订单管理、客户管理、员工管理、库存管理、会员钱包、小票打印等核心功能，帮助洗衣门店实现数字化运营管理。

## 技术栈

- **语言**: Java 17
- **框架**: Spring Boot 3.2.0
- **ORM**: Spring Data JPA (Hibernate)
- **数据库**: MariaDB (MySQL 兼容)
- **缓存**: Redis (Lettuce)
- **API 文档**: SpringDoc OpenAPI (Swagger UI)
- **构建工具**: Maven
- **其他**: Lombok

## 项目结构

`
backend/
├── pom.xml
└── src/main/
    ├── java/com/washhelper/
    │   ├── WashHelperApplication.java          # 启动类
    │   ├── config/                              # 配置
    │   │   ├── ApiKeyInterceptor.java           # API Key 鉴权拦截器
    │   │   ├── RedisConfig.java                 # Redis 配置
    │   │   └── WebConfig.java                   # Web/CORS 配置
    │   ├── controller/                          # 控制器层
    │   │   ├── OrderController.java             # 订单管理
    │   │   ├── CustomerController.java          # 客户管理
    │   │   ├── EmployeeController.java          # 员工管理
    │   │   ├── InventoryController.java         # 库存管理
    │   │   ├── ServiceItemController.java       # 服务项目管理
    │   │   ├── ShopController.java              # 门店管理
    │   │   ├── WalletController.java            # 会员钱包
    │   │   ├── ReceiptTemplateController.java   # 小票模板
    │   │   ├── FileUploadController.java        # 文件上传
    │   │   ├── AIController.java                # AI 服务
    │   │   └── GlobalExceptionHandler.java      # 全局异常处理
    │   ├── dto/                                 # 数据传输对象
    │   ├── entity/                              # 实体类
    │   ├── repository/                          # 数据访问层 (JPA Repository)
    │   ├── service/                             # 业务逻辑层
    │   └── util/                                # 工具类
    └── resources/
        └── application.yml                      # 应用配置
`

## 环境要求

- JDK 17+
- Maven 3.6+
- MariaDB 10.x / MySQL 8.x
- Redis 6.x+

## 快速开始

### 1. 准备数据库

确保 MariaDB/MySQL 服务已启动，创建数据库：

`sql
CREATE DATABASE washhelper DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
`

> 也可在配置中设置 createDatabaseIfNotExist=true，应用启动时会自动创建。

### 2. 配置应用

编辑 ackend/src/main/resources/application.yml，修改数据库和 Redis 连接信息：

`yaml
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/washhelper
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
`

### 3. 构建并运行

`ash
cd backend
mvn clean package -DskipTests
java -jar target/washhelper-backend-1.0.0.jar
`

或者直接使用 Maven 插件运行：

`ash
cd backend
mvn spring-boot:run
`

### 4. 访问

- 服务地址: http://localhost:8099
- Swagger UI: http://localhost:8099/swagger-ui.html
- API 文档: http://localhost:8099/v3/api-docs

## 核心模块

| 模块 | 说明 |
|------|------|
| **订单管理** | 订单创建、状态流转（待处理→清洗中→待取件→已完成）、订单打印 |
| **客户管理** | 客户信息维护、会员类型管理、客户联系 |
| **员工管理** | 员工信息、权限分配 |
| **库存管理** | 耗材库存跟踪、低库存预警 |
| **服务项目** | 洗衣服务项目配置（干洗、水洗、皮具护理等） |
| **会员钱包** | 会员充值、消费、余额查询 |
| **门店管理** | 多门店支持、营业时间配置 |
| **小票模板** | 可定制的小票打印模板 |
| **AI 服务** | 智能推荐与辅助功能 |

## API 文档

详细的接口文档请参考 docs/API_DOC.md，包含所有前端已对接接口的请求参数和响应格式说明。

## 多租户支持

系统支持多门店（多租户）架构，通过 shop_id 字段隔离各门店数据，相关迁移 SQL 见 docs/tenant-shop-migration.sql。

## 许可证

本项目为私有项目，未经授权不得分发。
