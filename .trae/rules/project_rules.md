# 项目规则

- 实体类必须使用 Swagger 注解描述模型：类上使用 `@Schema(description = "...")`，字段上使用 `@Schema(description = "...")`，禁止再使用 Hibernate `@Comment` 作为实体注释标准。
