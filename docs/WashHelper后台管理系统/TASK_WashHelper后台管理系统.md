# TASK - WashHelper后台管理系统

## 已完成任务

### T1 基础工程
- 建立 Spring Boot 工程与 Maven 构建
- 接入 JPA、Redis、MariaDB、Validation、Lombok
- 结果：可本地构建与启动

### T2 业务模块实现
- 订单、客户、库存、AI Mock 控制器/服务/仓储实现
- 结果：核心接口齐全

### T3 安全与配置
- 接入 API Key 拦截器与 Web 配置
- 生效范围 `/api/**`
- 结果：未带Key请求返回401

### T4 缓存与数据库
- 订单列表接入 Redis 缓存
- 启动时自动补齐数据库表/字段注释
- 结果：缓存可用，注释计数可校验

### T5 文档能力
- 接入 springdoc-openapi
- 补充 Controller 的 Swagger 注解
- 结果：Swagger UI 与 OpenAPI JSON 可访问

### T6 规范治理
- 建立项目规则：实体注释统一使用 `@Schema`
- 替换实体中的 Hibernate `@Comment`
- 结果：实体注释规范统一

## 待办任务

### P1 高优先级
- 增加全局异常处理（统一错误码与错误结构）
- 增加参数校验注解并补充请求示例
- 增加最小化单元测试（Service层）

### P2 中优先级
- 为 DTO（ApiResponse/PageResponse）补齐 Swagger 示例
- 为鉴权失败与业务异常定义标准响应模型
- 增加启动脚本和环境变量模板

### P3 低优先级
- AI接口扩展为真实模型调用
- 增加接口性能压测与缓存命中率监控
