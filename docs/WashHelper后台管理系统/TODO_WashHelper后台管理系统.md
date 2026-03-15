# TODO - WashHelper后台管理系统

## 立即待办（高优先级）

### 1. 统一异常处理
- 新增 `@RestControllerAdvice`
- 统一 400/401/404/500 返回结构
- 对齐 `ApiResponse` 输出字段

### 2. 参数校验补齐
- 在请求对象或实体上补 `@NotBlank/@NotNull/@Size`
- 在 Controller 上补 `@Valid`
- 在 Swagger 中补参数示例

### 3. 测试基线
- 增加 Service 层最小单元测试
- 覆盖订单创建、状态更新、缓存失效

## 近期待办（中优先级）

### 4. 文档增强
- 为 DTO 补 `@Schema` 字段示例
- 为 Controller 的 `@Operation` 增加描述与响应说明

### 5. 安全增强
- 将 `app.api-key` 从默认值替换为环境变量注入
- 增加请求日志脱敏策略

### 6. 运维可观测性
- 增加健康检查说明（数据库/Redis）
- 增加缓存命中率观察指标

## 启动与联调清单
- 构建：`D:\env\apache-maven-3.9.9\bin\mvn.cmd clean package -DskipTests`
- 启动：`java -jar target/washhelper-backend-1.0.0.jar`
- 接口：`http://localhost:8099/api/...`
- Swagger：`http://localhost:8099/swagger-ui/index.html`
