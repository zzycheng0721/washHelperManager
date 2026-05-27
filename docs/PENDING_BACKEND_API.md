# WashHelper 待完成后端接口文档

本文档根据当前小程序项目 `F:\code\washHelperAPP` 的页面调用情况整理，目标是把仍未实现、仍使用本地存储或 Mock 的功能全部后端化。

## 1. 通用约定

### 1.1 基础地址

本机调试：

```text
http://localhost:8099
```

生产环境：

```text
https://api.example.com
```

本文所有路径均为相对路径，例如：

```text
/api/customers/{id}
```

### 1.2 请求头

所有 `/api/**` 接口保持和当前后端一致，走 API Key 鉴权：

```http
Content-Type: application/json
X-API-Key: your-secret-api-key-here
```

文件上传接口使用：

```http
Content-Type: multipart/form-data
X-API-Key: your-secret-api-key-here
```

### 1.3 通用成功响应

建议统一使用：

```json
{
  "ok": true,
  "data": {},
  "message": "success"
}
```

创建类接口可额外返回 `id`：

```json
{
  "ok": true,
  "id": "123",
  "data": {}
}
```

### 1.4 通用失败响应

```json
{
  "ok": false,
  "message": "错误原因"
}
```

### 1.5 分页响应

列表接口建议统一返回：

```json
{
  "items": [],
  "page": 1,
  "pageSize": 20,
  "total": 0
}
```

### 1.6 时间字段

时间字段建议使用 ISO 8601 字符串，例如：

```text
2026-05-26T10:30:00+08:00
```

## 2. 客户模块待完成接口

当前后端已实现：

- `GET /api/customers`
- `POST /api/customers`
- `POST /api/customers/{id}/contact`

以下接口小程序已经调用或后续功能需要补齐。

### 2.1 获取单个客户详情

```http
GET /api/customers/{id}
```

用途：

- 客户详情页进入后获取最新客户资料。
- 充值成功后返回客户详情页时刷新客户信息。

路径参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---:|---:|---|
| id | string/number | 是 | 客户主键 ID 或客户编号 customerId，建议两者都支持 |

响应示例：

```json
{
  "ok": true,
  "data": {
    "id": 1,
    "customerId": "CUST-0001",
    "name": "张三",
    "phone": "13800000000",
    "memberType": "金卡会员",
    "notes": "常用低温洗",
    "balance": 2450.0,
    "avatarUrl": "https://cdn.example.com/avatar/1.png",
    "createdAt": "2026-05-26T10:30:00+08:00",
    "updatedAt": "2026-05-26T10:30:00+08:00"
  }
}
```

### 2.2 更新客户信息

```http
PUT /api/customers/{id}
PATCH /api/customers/{id}
```

用途：

- 客户详情页保存姓名、手机号、会员等级、备注、头像、余额等信息。
- 小程序当前优先调用 `PUT /api/customers/{id}`，新增客户预充值兜底时也会尝试 `PATCH /api/customers/{id}`。

请求体示例：

```json
{
  "name": "张三",
  "phone": "13800000000",
  "memberType": "金卡会员",
  "notes": "常用低温洗",
  "balance": 2450.0,
  "avatar": "https://cdn.example.com/avatar/1.png",
  "avatarUrl": "https://cdn.example.com/avatar/1.png"
}
```

响应示例：

```json
{
  "ok": true,
  "data": {
    "id": 1,
    "customerId": "CUST-0001",
    "name": "张三",
    "phone": "13800000000",
    "memberType": "金卡会员",
    "balance": 2450.0
  }
}
```

### 2.3 删除客户

```http
DELETE /api/customers/{id}
```

用途：

- 客户详情页删除客户。

建议：

- 如果客户已有订单，建议后端做软删除，避免历史订单丢失客户信息。

响应示例：

```json
{
  "ok": true
}
```

### 2.4 更新客户余额

```http
PATCH /api/customers/{id}/balance
```

用途：

- 新增客户时如果填写预充值金额，小程序会尝试更新余额。
- 会员充值成功后也可复用该接口，但更推荐通过“充值接口”统一写余额和流水。

请求体示例：

```json
{
  "balance": 100.0
}
```

响应示例：

```json
{
  "ok": true,
  "data": {
    "customerId": "CUST-0001",
    "balance": 100.0
  }
}
```

## 3. 文件上传接口

### 3.1 上传客户头像

推荐标准接口：

```http
POST /api/customers/avatar/upload
```

当前小程序预留兜底路径：

```text
/api/customers/avatar/upload
/api/customers/upload-avatar
/api/files/upload
/api/upload
```

建议：

- 后端至少实现 `/api/customers/avatar/upload`。
- 如果希望不改前端，也可以把另外三个路径做成别名。

请求类型：

```http
multipart/form-data
```

字段：

| 字段 | 类型 | 必填 | 说明 |
|---|---:|---:|---|
| file | file | 是 | 上传文件 |
| bizType | string | 否 | 当前小程序传 `customer-avatar` |

响应示例：

```json
{
  "ok": true,
  "data": {
    "url": "https://cdn.example.com/customer-avatar/abc.png",
    "avatarUrl": "https://cdn.example.com/customer-avatar/abc.png",
    "fileUrl": "https://cdn.example.com/customer-avatar/abc.png",
    "path": "/customer-avatar/abc.png"
  }
}
```

## 4. 会员充值与钱包接口

当前小程序中“会员充值”页面还没有真正调用后端；新增客户页如果填写预充值金额，会尝试调用钱包相关预留接口。

### 4.1 获取充值套餐

```http
GET /api/recharge/packages
```

用途：

- 会员充值页展示充值套餐。
- 当前前端写死了 `100/500/1000/2000` 四档，后端化后可由接口返回。

响应示例：

```json
{
  "items": [
    {
      "id": "pkg-100",
      "amount": 100,
      "gift": 10,
      "recommended": true,
      "enabled": true
    },
    {
      "id": "pkg-500",
      "amount": 500,
      "gift": 60,
      "recommended": false,
      "enabled": true
    }
  ]
}
```

### 4.2 会员充值

推荐标准接口：

```http
POST /api/customers/{id}/wallet/recharge
```

当前小程序预留兜底路径：

```text
/api/customers/{id}/wallet/recharge
/api/customers/{id}/recharge
/api/member-wallet/recharge
/api/wallet/recharge
```

用途：

- 会员充值页确认充值。
- 新增客户时填写预充值金额后同步充值。
- 后端应同时完成余额更新和交易流水写入，避免前端分别调多个接口造成数据不一致。

请求体示例：

```json
{
  "customerId": "CUST-0001",
  "amount": 100,
  "giftAmount": 10,
  "type": "recharge",
  "source": "miniapp",
  "channel": "wechat",
  "remark": "会员充值",
  "operator": "miniapp"
}
```

响应示例：

```json
{
  "ok": true,
  "data": {
    "transactionId": "TX-202605260001",
    "customerId": "CUST-0001",
    "rechargeAmount": 100,
    "giftAmount": 10,
    "changeAmount": 110,
    "balance": 2560,
    "createdAt": "2026-05-26T10:30:00+08:00"
  }
}
```

### 4.3 新增钱包流水

推荐标准接口：

```http
POST /api/customers/{id}/wallet/transactions
```

当前小程序预留兜底路径：

```text
/api/customers/{id}/wallet/transactions
/api/customers/{id}/transactions
/api/wallet/transactions
/api/transactions
```

用途：

- 新增客户预充值时，小程序当前会尝试手动写交易流水。
- 如果 `POST /api/customers/{id}/wallet/recharge` 已经自动写流水，该接口可用于后台补录、余额调整、订单消费等场景。

请求体示例：

```json
{
  "customerId": "CUST-0001",
  "customerName": "张三",
  "amount": 100,
  "direction": "in",
  "type": "recharge",
  "channel": "wallet",
  "remark": "新增客户预充值",
  "relatedOrderId": null,
  "operator": "miniapp"
}
```

响应示例：

```json
{
  "ok": true,
  "data": {
    "id": "TX-202605260001",
    "customerId": "CUST-0001",
    "amount": 100,
    "direction": "in",
    "balance": 100,
    "type": "recharge",
    "createdAt": "2026-05-26T10:30:00+08:00"
  }
}
```

## 5. 交易明细接口

当前交易明细页使用前端 Mock 数据，需要后端化。

### 5.1 查询客户交易明细

```http
GET /api/customers/{id}/wallet/transactions
```

查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---:|---:|---|
| type | string | 否 | `all`、`recharge`、`spend`、`adjust` |
| page | number | 否 | 默认 1 |
| pageSize | number | 否 | 默认 20 |
| startDate | string | 否 | 开始日期 |
| endDate | string | 否 | 结束日期 |

响应示例：

```json
{
  "items": [
    {
      "id": "TX-202605260001",
      "customerId": "CUST-0001",
      "title": "会员充值",
      "amount": 100,
      "direction": "in",
      "balance": 2560,
      "type": "recharge",
      "channel": "wechat",
      "payType": "微信支付",
      "relatedOrderId": null,
      "remark": "会员充值",
      "createdAt": "2026-05-26T10:30:00+08:00"
    }
  ],
  "page": 1,
  "pageSize": 20,
  "total": 1
}
```

### 5.2 查询全局交易流水

```http
GET /api/transactions
```

用途：

- 后台财务统计、全店流水查询。
- 小程序当前未直接使用，但前端已预留 `/api/transactions` 作为兜底路径。

查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---:|---:|---|
| customerId | string | 否 | 客户 ID |
| type | string | 否 | `all`、`recharge`、`spend`、`adjust` |
| page | number | 否 | 默认 1 |
| pageSize | number | 否 | 默认 20 |

## 6. 店铺信息接口

当前店铺信息使用本地存储 `washhelper:taro:shop-info`，需要后端化。

### 6.1 获取店铺信息

```http
GET /api/shop/info
```

响应示例：

```json
{
  "ok": true,
  "data": {
    "shopName": "赛维干洗",
    "phone": "13800000000",
    "address": "江苏省南京市雨花台区玉兰路99号",
    "logoUrl": "https://cdn.example.com/shop/logo.png",
    "latitude": 31.987654,
    "longitude": 118.765432,
    "updatedAt": "2026-05-26T10:30:00+08:00"
  }
}
```

### 6.2 保存店铺信息

```http
PUT /api/shop/info
```

请求体示例：

```json
{
  "shopName": "赛维干洗",
  "phone": "13800000000",
  "address": "江苏省南京市雨花台区玉兰路99号",
  "logoUrl": "https://cdn.example.com/shop/logo.png",
  "latitude": 31.987654,
  "longitude": 118.765432
}
```

响应示例：

```json
{
  "ok": true,
  "data": {
    "shopName": "赛维干洗",
    "phone": "13800000000",
    "address": "江苏省南京市雨花台区玉兰路99号",
    "logoUrl": "https://cdn.example.com/shop/logo.png"
  }
}
```

### 6.3 上传店铺 Logo

```http
POST /api/shop/logo/upload
```

请求类型：

```http
multipart/form-data
```

字段：

| 字段 | 类型 | 必填 | 说明 |
|---|---:|---:|---|
| file | file | 是 | Logo 图片 |

响应示例：

```json
{
  "ok": true,
  "data": {
    "logoUrl": "https://cdn.example.com/shop/logo.png",
    "url": "https://cdn.example.com/shop/logo.png"
  }
}
```

## 7. 营业时间接口

当前营业时间使用本地存储 `washhelper:taro:operating-hours`，需要后端化。

### 7.1 获取营业时间

```http
GET /api/shop/operating-hours
```

响应示例：

```json
{
  "ok": true,
  "data": {
    "paused": false,
    "week": [
      { "day": "周一", "start": "09:00", "end": "21:00", "enabled": true },
      { "day": "周二", "start": "09:00", "end": "21:00", "enabled": true },
      { "day": "周三", "start": "09:00", "end": "21:00", "enabled": true },
      { "day": "周四", "start": "09:00", "end": "21:00", "enabled": true },
      { "day": "周五", "start": "09:00", "end": "21:00", "enabled": true },
      { "day": "周六", "start": "10:00", "end": "22:00", "enabled": true },
      { "day": "周日", "start": "10:00", "end": "22:00", "enabled": true }
    ]
  }
}
```

### 7.2 保存营业时间

```http
PUT /api/shop/operating-hours
```

请求体示例：

```json
{
  "paused": false,
  "week": [
    { "day": "周一", "start": "09:00", "end": "21:00", "enabled": true },
    { "day": "周二", "start": "09:00", "end": "21:00", "enabled": true }
  ]
}
```

响应示例：

```json
{
  "ok": true
}
```

## 8. 服务价目表接口

当前服务价目表和新增服务项都使用本地存储 `washhelper:taro:service-items`，需要后端化。

### 8.1 查询服务价目表

```http
GET /api/services
```

查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---:|---:|---|
| category | string | 否 | 服务分类 |
| keyword | string | 否 | 服务名称关键字 |
| enabled | boolean | 否 | 是否启用 |
| page | number | 否 | 默认 1 |
| pageSize | number | 否 | 默认 100 |

响应示例：

```json
{
  "items": [
    {
      "id": "svc-1",
      "name": "衬衫干洗",
      "category": "干洗",
      "price": 25,
      "duration": "2天",
      "notes": "普通衬衫",
      "enabled": true,
      "createdAt": "2026-05-26T10:30:00+08:00",
      "updatedAt": "2026-05-26T10:30:00+08:00"
    }
  ],
  "page": 1,
  "pageSize": 100,
  "total": 1
}
```

### 8.2 获取单个服务项

```http
GET /api/services/{id}
```

响应示例：

```json
{
  "ok": true,
  "data": {
    "id": "svc-1",
    "name": "衬衫干洗",
    "category": "干洗",
    "price": 25,
    "duration": "2天",
    "notes": "普通衬衫",
    "enabled": true
  }
}
```

### 8.3 新增服务项

```http
POST /api/services
```

请求体示例：

```json
{
  "name": "羽绒服清洗",
  "category": "水洗",
  "price": 65,
  "duration": "3天",
  "notes": "按件计价",
  "enabled": true
}
```

响应示例：

```json
{
  "ok": true,
  "id": "svc-1001",
  "data": {
    "id": "svc-1001",
    "name": "羽绒服清洗",
    "category": "水洗",
    "price": 65
  }
}
```

### 8.4 更新服务项

```http
PUT /api/services/{id}
PATCH /api/services/{id}
```

请求体示例：

```json
{
  "name": "羽绒服清洗",
  "category": "水洗",
  "price": 68,
  "duration": "3天",
  "notes": "按件计价",
  "enabled": true
}
```

响应示例：

```json
{
  "ok": true
}
```

### 8.5 删除服务项

```http
DELETE /api/services/{id}
```

建议：

- 如果服务项已被订单引用，建议软删除或设置 `enabled=false`。

响应示例：

```json
{
  "ok": true
}
```

## 9. 小票模板接口

当前小票模板使用本地存储 `washhelper:taro:receipt-settings`，需要后端化。

### 9.1 获取小票模板设置

```http
GET /api/receipt-template
```

响应示例：

```json
{
  "ok": true,
  "data": {
    "showLogo": true,
    "showCustomerName": true,
    "showWashInstructions": false,
    "footerText": "感谢您的光临，祝您生活愉快！",
    "printWidth": "58mm",
    "updatedAt": "2026-05-26T10:30:00+08:00"
  }
}
```

### 9.2 保存小票模板设置

```http
PUT /api/receipt-template
```

请求体示例：

```json
{
  "showLogo": true,
  "showCustomerName": true,
  "showWashInstructions": false,
  "footerText": "感谢您的光临，祝您生活愉快！",
  "printWidth": "58mm"
}
```

响应示例：

```json
{
  "ok": true
}
```

## 10. 员工管理接口

当前员工列表和新增员工使用本地存储 `washhelper:taro:employees`，需要后端化。

### 10.1 查询员工列表

```http
GET /api/employees
```

查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---:|---:|---|
| keyword | string | 否 | 姓名、手机号、角色关键字 |
| role | string | 否 | 角色：店长、前台、洗护师 |
| status | string | 否 | 状态：在线、离线、禁用 |
| page | number | 否 | 默认 1 |
| pageSize | number | 否 | 默认 20 |

响应示例：

```json
{
  "items": [
    {
      "id": "emp-1",
      "name": "张三",
      "role": "店长",
      "phone": "13800000001",
      "status": "在线",
      "permissions": ["orders", "inventory", "finance"],
      "createdAt": "2026-05-26T10:30:00+08:00",
      "updatedAt": "2026-05-26T10:30:00+08:00"
    }
  ],
  "page": 1,
  "pageSize": 20,
  "total": 1
}
```

### 10.2 获取单个员工

```http
GET /api/employees/{id}
```

响应示例：

```json
{
  "ok": true,
  "data": {
    "id": "emp-1",
    "name": "张三",
    "role": "店长",
    "phone": "13800000001",
    "status": "在线",
    "permissions": ["orders", "inventory", "finance"]
  }
}
```

### 10.3 新增员工

```http
POST /api/employees
```

请求体示例：

```json
{
  "name": "李四",
  "phone": "13800000002",
  "role": "前台",
  "password": "123456",
  "permissions": ["orders", "inventory"],
  "status": "在线"
}
```

安全要求：

- 后端必须保存密码哈希，不可明文保存。
- 员工列表和详情接口不可返回密码或密码哈希。

响应示例：

```json
{
  "ok": true,
  "id": "emp-2",
  "data": {
    "id": "emp-2",
    "name": "李四",
    "role": "前台",
    "phone": "13800000002",
    "permissions": ["orders", "inventory"],
    "status": "在线"
  }
}
```

### 10.4 更新员工

```http
PUT /api/employees/{id}
PATCH /api/employees/{id}
```

请求体示例：

```json
{
  "name": "李四",
  "phone": "13800000002",
  "role": "洗护师",
  "permissions": ["orders"],
  "status": "在线"
}
```

响应示例：

```json
{
  "ok": true
}
```

### 10.5 删除员工

```http
DELETE /api/employees/{id}
```

建议：

- 优先软删除或设置 `status=禁用`，避免审计数据丢失。

响应示例：

```json
{
  "ok": true
}
```

### 10.6 重置员工密码

```http
PATCH /api/employees/{id}/password
```

请求体示例：

```json
{
  "password": "newPassword123"
}
```

响应示例：

```json
{
  "ok": true
}
```

## 11. 建议的数据表

后端可按实际技术栈调整，建议至少补充以下表：

| 表名 | 用途 |
|---|---|
| `customer_wallet_transactions` | 会员充值、消费、余额调整流水 |
| `recharge_packages` | 充值套餐 |
| `shop_info` | 店铺信息 |
| `operating_hours` | 营业时间 |
| `service_items` | 服务价目表 |
| `receipt_template_settings` | 小票模板配置 |
| `employees` | 员工 |
| `employee_permissions` | 员工权限，可选，也可 JSON 存储 |
| `uploaded_files` | 头像、Logo 等文件记录，可选 |

## 12. 后端实现优先级建议

### P0：小程序当前会直接报错或功能不可用

1. `GET /api/customers/{id}`
2. `PUT /api/customers/{id}`
3. `DELETE /api/customers/{id}`
4. `POST /api/customers/avatar/upload`
5. `POST /api/customers/{id}/wallet/recharge`
6. `GET /api/customers/{id}/wallet/transactions`

### P1：需要替换本地存储的核心配置

1. `GET/PUT /api/shop/info`
2. `GET/PUT /api/shop/operating-hours`
3. `GET/POST/PUT/DELETE /api/services`
4. `GET/PUT /api/receipt-template`

### P2：员工与管理能力

1. `GET/POST/PUT/DELETE /api/employees`
2. `PATCH /api/employees/{id}/password`
3. `GET /api/recharge/packages`

