# FINAL - WashHelper后台管理系统

## 项目最终状态（以当前代码为准）

### 技术栈
- Spring Boot 3.2.0
- Java 17 + Maven
- Spring Data JPA + MariaDB
- Spring Data Redis
- Swagger/OpenAPI（springdoc）

### 目录基线
```text
backend/
├── src/main/java/com/washhelper/
│   ├── controller
│   ├── service
│   ├── repository
│   ├── entity
│   ├── dto
│   ├── config
│   └── WashHelperApplication.java
├── src/main/resources/application.yml
└── pom.xml
```

### 已实现能力
- 订单、客户、库存、AI Mock 接口完整
- API Key 鉴权（`/api/**`）
- CORS 配置
- 订单列表缓存与缓存失效
- Swagger UI 与 OpenAPI JSON
- 实体 Swagger 注释规范
- 启动时数据库表/字段注释自动补齐

### 实际可访问地址
- 业务接口：`http://localhost:8099/api/...`
- Swagger UI：`http://localhost:8099/swagger-ui/index.html`
- OpenAPI：`http://localhost:8099/v3/api-docs`

## 与历史文档差异修复
- 已移除旧 Node/Prisma/`api` 目录描述
- 已统一为 Spring Boot `backend` 事实
- 已重写 6A 文档以匹配当前代码

## 质量结论
- 当前版本可稳定构建、启动、联调。
- 文档与代码已重新对齐，可作为后续迭代基础。
