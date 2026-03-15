# ACCEPTANCE - WashHelper后台管理系统

## 验收时间
- 日期：2026-03-16
- 基线代码：`backend` 当前工作区版本

## 验收项结果

### 1. 可构建性
- 结果：通过
- 命令：`mvn clean package -DskipTests`

### 2. 服务启动
- 结果：通过
- 端口：`8099`

### 3. 鉴权能力
- 结果：通过
- 规则：`/api/**` 必须带 `X-API-Key`

### 4. Swagger 文档
- 结果：通过
- `/swagger-ui/index.html` -> 200
- `/v3/api-docs` -> 200

### 5. 核心业务接口
- 结果：通过
- 订单、客户、库存、AI Mock 接口可访问

### 6. 数据库与缓存
- 结果：通过
- MariaDB 连接正常（`washhelper`）
- Redis 连接正常（支持 `REDIS_PASSWORD`）
- 订单列表缓存可用（`@Cacheable/@CacheEvict`）

### 7. 注释规范
- 结果：通过
- 实体注释：`@Schema`
- 数据库注释：启动自动补齐并打印计数

## 未完成项
- 系统化单元测试与集成测试尚未补齐
- 全局异常模型尚未标准化

## 结论
- 当前版本可用于前端联调与内部测试。
- 与当前代码事实一致，可作为后续迭代基线。
