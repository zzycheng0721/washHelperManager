# CONSENSUS - WashHelper后台管理系统

## 已确认共识

### 基础技术共识
- 使用 Spring Boot 后端，不再维护旧 `api` 目录方案。
- 本地依赖：
  - MariaDB：`localhost:3306`
  - Redis：`localhost:6379`
- 服务端口：`8099`
- 鉴权：基础 API Key（请求头 `X-API-Key`）
- AI：保持 Mock 返回

### 文档与注释共识
- Swagger 已接入并可访问。
- 实体注释必须使用 `@Schema`，不再使用 Hibernate `@Comment`。
- 数据库表/字段注释由启动时 SQL 自动补齐。

## 功能范围共识

### 在范围内
- 订单管理：
  - `GET /api/orders`
  - `POST /api/orders`
  - `PUT /api/orders/{id}/status`
  - `DELETE /api/orders/{id}`
  - `POST /api/orders/{id}/print`
- 客户管理：
  - `GET /api/customers`
  - `POST /api/customers`
  - `POST /api/customers/{id}/contact`
- 库存管理：
  - `GET /api/inventory`
  - `POST /api/inventory`
  - `PUT /api/inventory/{id}`
- AI Mock：
  - `GET /api/ai`

### 不在范围内
- 用户登录注册与多角色权限
- 支付、报表、消息推送
- AI真实模型接入

## 验收标准共识
- 服务可启动并监听 `8099`
- API Key 拦截有效（未带Key返回401）
- Swagger 文档页可访问
- MariaDB/Redis 可连通
- 核心接口可用并返回约定结构

## 版本基线
- 工程目录：`backend`
- 配置入口：`backend/src/main/resources/application.yml`
- 构建命令：`mvn clean package -DskipTests`
