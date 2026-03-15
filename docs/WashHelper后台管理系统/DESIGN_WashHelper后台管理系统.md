# DESIGN - WashHelper后台管理系统

## 架构设计

### 分层结构
- `controller`：HTTP接口层（含 Swagger 注解）
- `service`：业务逻辑层
- `repository`：JPA 数据访问层
- `entity`：数据库实体映射层
- `dto`：统一响应对象
- `config`：鉴权、CORS、Redis缓存配置

### 请求链路
1. 客户端请求 `/api/**`
2. `ApiKeyInterceptor` 校验 `X-API-Key`
3. Controller 解析参数并调用 Service
4. Service 调用 Repository 持久化或查询
5. 通过 `ApiResponse`/`PageResponse` 返回结果

## 数据设计

### 表结构
- `orders`
- `customers`
- `inventory`

### 注释策略
- 代码注释：实体使用 Swagger `@Schema`
- 库注释：应用启动时执行 `ALTER TABLE ... COMMENT` 补齐
- 质量检查：启动时打印
  - `TABLE_COMMENT_COUNT`
  - `COLUMN_COMMENT_COUNT`

## 接口契约

### 统一响应
- 成功响应：`ApiResponse.success(...)`
- 分页响应：`PageResponse<T>`

### 鉴权
- Header：`X-API-Key`
- 生效范围：`/api/**`

### Swagger
- UI：`/swagger-ui/index.html`
- OpenAPI：`/v3/api-docs`

## 关键实现点

### 订单缓存
- `OrderService#getOrders` 使用 `@Cacheable`
- 订单变更接口使用 `@CacheEvict(allEntries = true)`
- Redis TTL：30分钟

### CORS
- 允许路径：`/api/**`
- origins/methods/headers：均允许 `*`

## 异常策略
- 目前以运行时异常为主（如资源不存在）
- 鉴权失败返回 401 + JSON
- 后续可扩展全局异常处理器
