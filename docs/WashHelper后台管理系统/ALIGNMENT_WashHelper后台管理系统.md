# ALIGNMENT - WashHelper后台管理系统

## 项目现状对齐

### 技术栈
- 后端框架：Spring Boot 3.2.0
- 语言与构建：Java 17 + Maven
- 数据库：MariaDB（3306）
- 缓存：Redis（6379）
- ORM：Spring Data JPA + Hibernate
- 接口文档：springdoc-openapi（Swagger UI）

### 运行配置
- 服务端口：8099
- 主配置文件：`backend/src/main/resources/application.yml`
- 鉴权方式：`X-API-Key` 请求头（拦截 `/api/**`）
- Swagger入口：
  - `/swagger-ui/index.html`
  - `/v3/api-docs`

### 业务模块
- 订单：列表、新建、改状态、删除、打印
- 客户：列表、新建、联系
- 库存：列表、新建、更新
- AI：Mock响应

## 代码事实

### 接口前缀
- 统一前缀：`/api`
- 控制器：
  - `OrderController` -> `/api/orders`
  - `CustomerController` -> `/api/customers`
  - `InventoryController` -> `/api/inventory`
  - `AIController` -> `/api/ai`

### 数据模型
- `orders`
- `customers`
- `inventory`

### 注释规范（已落地）
- 实体类注释统一使用 Swagger 注解：
  - 类：`@Schema(description = "...")`
  - 字段：`@Schema(description = "...")`
- 项目规则文件：`.trae/rules/project_rules.md`

## 风险与边界
- 现有 `docs` 历史内容曾包含 Node/Prisma 方案，已与现代码不一致。
- 当前文档以 `backend` 工程为唯一事实来源。
- 仅记录已实现能力，不扩展未实现需求。

## 本阶段结论
- 当前项目已完成从旧方案到 Spring Boot 方案的对齐。
- 后续所有设计、任务、验收文档均以 `backend` 代码与配置为准。
