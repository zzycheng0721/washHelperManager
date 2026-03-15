# WashHelper 后端接口文档（前端占位接口汇总）

本文档汇总了当前前端代码中已预留的后端接口，供后端实现时参考。所有路径均以 `/api` 为前缀。

## 通用约定
- 所有接口返回 JSON。
- 时间字段建议使用 ISO 8601 字符串。
- 分页建议使用 `page`（从 1 开始）。
- 列表接口建议返回：`items`、`page`、`pageSize`、`total`。

## 订单 Orders

### 获取订单列表
- 方法：`GET`
- 路径：`/api/orders`
- 查询参数：
  - `status`：订单状态（例如：`待处理`、`清洗中`、`待取件`、`已完成`、`全部`）
  - `search`：关键字（订单号/客户名/手机号）
  - `page`：页码
- 期望响应（示例）：
```json
{
  "items": [
    {
      "id": "ORD-8829",
      "customer": "张三",
      "status": "待处理",
      "service": "干洗(3件套)",
      "time": "2024-10-24 14:30"
    }
  ],
  "page": 1,
  "pageSize": 20,
  "total": 58
}
```

### 新建订单
- 方法：`POST`
- 路径：`/api/orders`
- 请求体（示例）：
```json
{
  "serviceType": "wash",
  "specialReqs": ["no_bleach", "gentle"],
  "quantity": 2
}
```
- 期望响应：
```json
{ "ok": true, "id": "ORD-8830" }
```

### 更新订单状态
- 方法：`PUT`
- 路径：`/api/orders/:id/status`
- 请求体（示例）：
```json
{ "status": "清洗中" }
```
- 期望响应：
```json
{ "ok": true }
```

### 删除订单
- 方法：`DELETE`
- 路径：`/api/orders/:id`
- 期望响应：
```json
{ "ok": true }
```

### 打印订单标签
- 方法：`POST`
- 路径：`/api/orders/:id/print`
- 期望响应：
```json
{ "ok": true, "message": "print queued" }
```

## 客户 Customers

### 获取客户列表
- 方法：`GET`
- 路径：`/api/customers`
- 查询参数：
  - `type`：会员类型（例如：`普通会员`、`银卡会员`、`金卡会员`、`全部`）
  - `search`：关键字（姓名/手机号）
  - `page`：页码
- 期望响应（示例）：
```json
{
  "items": [
    {
      "id": "CUST-001",
      "name": "张三",
      "phone": "13800000000",
      "type": "金卡会员"
    }
  ],
  "page": 1,
  "pageSize": 20,
  "total": 12
}
```

### 新建客户
- 方法：`POST`
- 路径：`/api/customers`
- 请求体（示例）：
```json
{
  "name": "李四",
  "phone": "13900000000",
  "memberType": "银卡会员",
  "notes": "常用低温洗"
}
```
- 期望响应：
```json
{ "ok": true, "id": "CUST-010" }
```

### 联系客户
- 方法：`POST`
- 路径：`/api/customers/:id/contact`
- 期望响应：
```json
{ "ok": true }
```

## 库存 Inventory

### 获取库存列表
- 方法：`GET`
- 路径：`/api/inventory`
- 期望响应（示例）：
```json
{
  "items": [
    {
      "id": "1",
      "name": "浓缩洗衣液",
      "category": "detergent",
      "available": 15,
      "total": 100,
      "unit": "L",
      "alertThreshold": 20,
      "notes": "日常使用"
    }
  ]
}
```

### 新建库存条目
- 方法：`POST`
- 路径：`/api/inventory`
- 请求体（示例）：
```json
{
  "name": "塑料袋（大号）",
  "category": "packaging",
  "available": 40,
  "total": 500,
  "unit": "pcs",
  "alertThreshold": 10,
  "notes": "大件衣物包装"
}
```
- 期望响应：
```json
{ "ok": true, "id": "6" }
```

### 更新库存条目
- 方法：`PUT`
- 路径：`/api/inventory/:id`
- 请求体（示例）：
```json
{
  "name": "塑料袋（大号）",
  "category": "packaging",
  "available": 30,
  "total": 500,
  "unit": "pcs",
  "alertThreshold": 10,
  "notes": "补货中"
}
```
- 期望响应：
```json
{ "ok": true }
```

## AI（本地 Mock）

### 获取 AI Mock 响应
- 方法：`GET`
- 路径：`/api/ai`
- 说明：目前仅在本地开发服务器提供，后端实现后可替换。
- 期望响应（示例）：
```json
{
  "ok": true,
  "data": {
    "summary": "这是一个本地 Mock 的 AI 响应，用于前端联调。",
    "suggestion": "请在后端就绪后替换为真实模型输出。",
    "timestamp": "2026-03-15T12:34:56.000Z"
  }
}
```
