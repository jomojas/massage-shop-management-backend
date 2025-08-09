# 会员管理模块

## 会员查询（默认查询未删除会员）

- url: <mark>GET /api/members</mark>
- 描述：按条件分页查询会员（包括模糊查询、余额、时间段等）
- 请求参数：
  | 参数名 | 类型 | 是否必填 | 描述 |
  | ---------- | ------- | ---- | ----------------------------------------- |
  | keyword | string | 否 | 姓名或电话模糊 |
  | minBalance | decimal | 否 | 最小余额 |
  | maxBalance | decimal | 否 | 最大余额 |
  | startDate | string | 否 | 注册时间起始（格式：yyyy-MM-dd） |
  | endDate | string | 否 | 注册时间结束（格式：yyyy-MM-dd） |
  | page | int | 否 | 当前页码（默认 1） |
  | size | int | 否 | 每页条数（默认 10） |
  | sortBy | string | 否 | 排序字段，例如：`name`、`balance`、`created_time` 等,默认`name` |
  | order | string | 否 | 排序方式：`asc`（升序）或 `desc`（降序），默认`asc` |

- 响应实例

```json
{
  "totalMembers": 35,
  "totalPages": 4,
  "currentPage": 1,
  "list": [
    {
      "id": 1,
      "name": "李四",
      "phone": "138****0000",
      "balance": 88.0,
      "description": "老客户",
      "created_time": "2025-08-01"
    }
  ]
}
```

## 查询已删除会员

- url: <mark>GET /api/members/deleted
- 请求参数
  | 参数名 | 类型 | 是否必填 | 描述 |
  | ---------- | ------- | ---- | ----------------------------------------- |
  | keyword | string | 否 | 姓名或电话模糊 |
  | minBalance | decimal | 否 | 最小余额 |
  | maxBalance | decimal | 否 | 最大余额 |
  | startDate | string | 否 | 注册时间起始（格式：yyyy-MM-dd） |
  | endDate | string | 否 | 注册时间结束（格式：yyyy-MM-dd） |
  | page | int | 否 | 当前页码（默认 1） |
  | size | int | 否 | 每页条数（默认 10） |
  | sortBy | string | 否 | 排序字段，例如：`name`、`balance`、`created_time` 等, 默认`name` |
  | order | string | 否 | 排序方式：`asc`（升序）或 `desc`（降序），默认`asc` |

- 响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalMembers": 35,
    "totalPages": 4,
    "currentPage": 1,
    "list": [
      {
        "id": 12,
        "name": "张三",
        "phone": "138****0000",
        "balance": 0.0,
        "description": "多次未到店，已停用",
        "created_time": "2025-05-10"
      },
      {
        "id": 17,
        "name": "刘七",
        "phone": "137****8888",
        "balance": 25.0,
        "description": "转介绍客户",
        "created_time": "2025-06-20"
      }
    ]
  }
}
```

#### 逻辑删除会员

- url: <mark>DELETE /api/members/{id}
- 描述：逻辑删除会员（标记为已删除）
- 响应：200 OK

#### 逻辑恢复会员

- url: <mark>PUT /api/members/{id}/restore
- 描述：恢复被删除的会员
- 响应：200 OK

## 新增会员（首次充值，自动增加充值记录）

- 请求实例：

<mark>POST /api/members

```json
{
  "name": "张三",
  "phone": "13812345678",
  "balance": 100.0,
  "description": "第一次充值成为会员"
}
```

- 响应实例：

```json
{
  "id": 123,
  "name": "张三",
  "phone": "13812345678",
  "balance": 100.0,
  "description": "第一次充值成为会员",
  "created_time": "2025-08-04"
}
```

## 会员充值（已有会员，自动增加充值记录）

- url: <mark>POST /api/members/{id}/recharges
- 请求体：

```json
{
  "amount": 100.0,
  "remark": "微信充值"
}
```

- 响应：200 OK

## 修改会员信息（需谨慎）

- url: <mark>PUT /api/members/{id}
- 请求体:

```json
{
  "name": "赵六",
  "phone": "13911112222",
  "balance": 200,
  "description": "更新备注"
}
```

## 查询会员充值记录

- url: <mark>GET /api/members/recharges
- 请求参数:
  | 参数名 | 类型 | 描述 |
  | --------- | ------- | -------------------------------- |
  | keyword | string | 会员姓名或电话模糊 |
  | minAmount | decimal | 最小充值金额 |
  | maxAmount | decimal | 最大充值金额 |
  | startDate | string | 充值时间起始（格式：yyyy-MM-dd） |
  | endDate | string | 充值时间结束（格式：yyyy-MM-dd） |
  | page | int | 当前页 |
  | size | int | 每页数量 |
  | sortBy | string | 排序字段，例如：`amount`、`recharge_time`, `name`, 默认`recharge_time` |
  | order | string | 排序顺序：`asc`（升序）或 `desc`（降序），默认`desc`（最近到最远） |

- 响应实例:

```json
{
  "total": 5,
  "list": [
    {
      "record_id": 1001,
      "member_id": 1,
      "member_name": "张三",
      "member_phone": "13800000000",
      "amount": 88,
      "recharge_time": "2025-08-03 14:55:23",
      "remark": "微信充值"
    }
  ]
}
```

## 修改充值记录（需谨慎，会造成和会员余额不吻合现象）

- url: <mark>PUT /api/members/recharges{recordId}
- 请求体:

```json
{
  "record_id": 1001,
  "amount": 100,
  "remark": "改为现金充值"
}
```

- 响应： 200 OK

# 消费管理模块

## 消费记录查询

- url: <mark>GET /api/consumptions/
- 请求参数:
  | 参数名 | 类型 | 是否必传 | 说明 |
  | ----------- | ------- | ---- | ------------------------------------------------------ |
  | `keyword` | string | 否 | 模糊匹配客户姓名、电话、员工姓名、项目名称、消费描述 |
  | `startDate` | string | 否 | 消费起始时间，格式：`yyyy-MM-dd` |
  | `endDate` | string | 否 | 消费结束时间 |
  | `minAmount` | decimal | 否 | 最小消费金额 |
  | `maxAmount` | decimal | 否 | 最大消费金额 |
  | `sortBy` | string | 否 | 排序字段，可选值：`name`（客户姓名）、`total_price`（消费金额）、`date`（消费时间） ,默认`date`|
  | `order` | string | 否 | 排序方式，`asc` 升序，`desc` 降序，默认：`desc`(最近到最远) |
  | `page` | int | 否 | 当前页（默认：1） |
  | `pageSize` | int | 否 | 每页条数（默认：10） |

- 请求实例：

```json
GET /api/consumptions?keyword=张三&startDate=2025-08-01&minAmount=100&page=1&pageSize=10
```

```json
GET /api/consumptions?keyword=张三&startDate=2025-08-01&minAmount=100&page=1&pageSize=10
```

- 响应实例：

```json
{
  "code": 200,
  "message": "success",
  "total": 120,
  "list": [
    {
      "record_id": 1001,
      "name": "李四", // 会员名（普通顾客为 null）
      "phone": "13912345678", // 电话（普通顾客为 null）
      "description": "路人顾客（未登记）", // 普通顾客有，会员为 null
      "total_price": 180.0,
      "consume_time": "2025-08-01",
      "projects": [
        {
          "name": "足疗",
          "price": 100,
          "employees": ["小张"]
        },
        {
          "name": "刮痧",
          "price": 80,
          "employees": ["小张", "小李"]
        }
      ],
      "record_detail": "足疗（小张）100元，刮痧（小张、小李）80元，共计180元" // 拼接说明
    }
  ]
}
```

## 修改消费记录（需谨慎，会造成和其他信息不吻合现象）

- url: <mark>PUT /api/consumptions/{id}
- 请求实例：

```json
{
  "name": "李四", // 可选：会员姓名（普通客户为 null）
  "phone": "13912345678", // 可选：会员电话（普通客户为 null）
  "description": "路人顾客（未登记）", // 可选：普通客户说明
  "total_price": 180.0,
  "consume_time": "2025-08-01",
  "projects": [
    {
      "name": "足疗",
      "price": 100,
      "employees": ["小张"]
    },
    {
      "name": "刮痧",
      "price": 80,
      "employees": ["小张", "小李"]
    }
  ],
  "record_detail": "足疗（小张）100元，刮痧（小张、小李）80元，共计180元"
}
```

- 响应实例:

```json
{
  "code": 200,
  "message": "success"
}
```

## 逻辑删除消费记录(消费记录最好不可删除)

- url: <mark>DELETE /api/consumptions/{id}
- 响应实例:

```json
{
  "code": 200,
  "message": "success"
}
```

## 逻辑恢复消费记录

- url <mark>PATCH /api/consumptions/{id}/restore
- 响应实例:

```json
{
  "code": 200,
  "message": "success"
}
```

## 新增消费记录（consume_service 表中的 service_time 字段由 consume_record 表中的 consume_time 提供）

- url: <mark>POST /api/consumptions
- 请求体说明:
  共用结构：
  | 字段名 | 类型 | 是否必填 | 说明 |
  | --------------- | ----------- | ---- | ----------------------- |
  | `member_id` | Long | 否 | 会员 ID（会员消费时传，普通客户不传） |
  | `customer_desc` | String | 否 | 普通客户备注描述（普通客户消费时传，会员不传） |
  | `items` | List\<Item> | 是 | 消费项目数组 |
  | `total_price` | BigDecimal | 是 | 总金额 |
  | `record_detail` | String | 否 | 消费详细描述 |
  | `consume_time` | Date | 否 | 消费时间 |

子结构：Item（每个项目）
| 字段名 | 类型 | 是否必填 | 说明 |
| -------------- | ------------ | ---- | --------- |
| `project_name` | String | 是 | 项目名称 |
| `price` | BigDecimal | 是 | 项目价格 |
| `staff` | List\<Staff> | 是 | 提供服务的员工列表 |

子结构：staff（每个项目的服务员工）
| 字段名 | 类型 | 是否必填 | 说明 |
| ------------- | ---------- | ---- | ----------- |
| `employee_id` | Long | 是 | 员工 ID |
| `income` | BigDecimal | 是 | 员工从该项目获得的收益 |

- 请求实例 1：会员消费记录

```json
{
  "member_id": 1,
  "items": [
    {
      "project_name": "足疗",
      "price": 80,
      "staff": [
        { "employee_id": 101, "income": 50 },
        { "employee_id": 102, "income": 30 }
      ]
    },
    {
      "project_name": "推拿",
      "price": 120,
      "staff": [{ "employee_id": 103, "income": 120 }]
    }
  ],
  "consume_time": "2025-08-01",
  "total_price": 200,
  "record_detail": "会员当天做了两个项目"
}
```

- 请求实例 2: 普通用户消费记录

```json
{
  "customer_desc": "张三，第一次到店，备注走路不便",
  "items": [
    {
      "project_name": "修脚",
      "price": 60,
      "staff": [{ "employee_id": 104, "income": 60 }]
    }
  ],
  "consume_time": "2025-08-01",
  "total_price": 60,
  "record_detail": "普通客户修脚服务"
}
```

- 成功响应实例:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "consumption_id": 10001
  }
}
```

- 失败响应实例:

```json
{
  "code": 400,
  "message": "提交数据格式错误或缺失必要字段"
}
```

# 项目管理模块

##　查询项目(默认查询全部未删除项目)

- url: <mark>GET /api/projects
- 响应实例：

```json
{
  "code": 200,
  "message": "success",
  "total": 2,
  "list": [
    {
      "id": 1,
      "name": "足疗",
      "category": "按摩",
      "price_guest": 88.0,
      "price_member": 68.0,
      "description": "经典足疗项目",
      "created_time": "2025-08-01 12:00:00",
      "updated_time": "2025-08-02 09:11:00"
    },
    {
      "id": 2,
      "name": "修脚",
      "category": "足部护理",
      "price_guest": 60.0,
      "price_member": 50.0,
      "description": "修剪趾甲，去除死皮",
      "created_time": "2025-08-01 15:10:00",
      "updated_time": "2025-08-01 15:10:00"
    }
  ]
}
```

## 查询已删除项目

- url: <mark>GET /api/projects/deleted
- 响应示例：

```json
{
  "code": 200,
  "message": "success",
  "total": 1,
  "list": [
    {
      "id": 3,
      "name": "拔罐",
      "category": "中医理疗",
      "price_guest": 100.0,
      "price_member": 80.0,
      "description": "火罐疗法排湿祛寒",
      "created_time": "2025-08-01 16:00:00",
      "updated_time": "2025-08-01 16:00:00"
    }
  ]
}
```

## 新增项目

- url: <mark>POST /api/projects
- 请求体：

```json
{
  "name": "拔罐",
  "category": "中医理疗",
  "price_guest": 100.0,
  "price_member": 80.0,
  "description": "火罐疗法排湿祛寒"
}
```

- 响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "project_id": 3
  }
}
```

## 修改项目（覆盖式，不影响已消费记录）

- url: <mark>PUT /api/projects/{id}
- 请求体：

```json
{
  "name": "拔罐疗法",
  "category": "中医理疗",
  "price_guest": 108.0,
  "price_member": 85.0,
  "description": "升级版火罐疗法"
}
```

- 说明：覆盖原字段

## 逻辑删除项目

- url: <mark>DELETE /api/projects/{id}
- 响应：

```json
{
  "code": 200,
  "message": "success"
}
```

## 逻辑恢复已删除项目

- url: <mark>PATCH /api/projects/{id}/recover
- 响应：

```json
{
  "code": 200,
  "message": "success"
}
```

# 员工管理

## 查询员工服务记录

- url: <mark>GET /api/staffs/service-records
- 请求参数:
  | 参数名 | 类型 | 是否必须 | 说明 |
  | -------------- | ------- | ---- | ------------------------------------------------------------------ |
  | `staff_id` | Long | 否 | 员工 ID（可指定某个员工） |
  | `keyword` | String | 否 | 模糊关键字（匹配项目名称、会员姓名、普通客户描述） |
  | `date_start` | String | 否 | 消费开始时间（格式：`YYYY-MM-DD`） |
  | `date_end` | String | 否 | 消费结束时间（格式：`YYYY-MM-DD`） |
  | `earnings_min` | Decimal | 否 | 最低服务收入 |
  | `earnings_max` | Decimal | 否 | 最高服务收入 |
  | `sortBy` | String | 否 | 排序字段，可选：`staff_name`（员工姓名）、`earnings`（收入）、`consumption_date`（消费日期），默认`consumption_date` |
  | `order` | String | 否 | 排序方式，可选：`asc`（升序）、`desc`（降序），默认：`desc`（最近到最远） |
  | `page` | Integer | 否 | 当前页码（默认：`1`） |
  | `page_size` | Integer | 否 | 每页数量（默认：`10`） |

- 请求实例:

```json
  GET /api/staff/service-records?staff_id=3&keyword=张&page=1&page_size=5
  GET /api/staff/service-records?staff_id=3&keyword=张&page=1&page_size=5
```

- 响应实例:

```json
{
  "code": 200,
  "message": "success",
  "total": 2,
  "list": [
    {
      "record_id": 1001,
      "staff_id": 3,
      "staff_name": "李四",
      "project_name": "足疗",
      "earnings": 40.0,
      "customer_name": "张三",
      "service_time": "2025-08-03",
      "commission": 0.2,
      "record_detail": "张三在足疗项目中由李四服务。"
    },
    {
      "record_id": 1002,
      "staff_id": 3,
      "staff_name": "李四",
      "project_name": "肩颈推拿",
      "earnings": 38.0,
      "customer_name": "张晓晓",
      "service_time": "2025-08-04",
      "commission": 0.15,
      "record_detail": "张晓晓消费肩颈推拿项目，由李四服务。"
    }
  ]
}
```

```json
{
  "code": 400,
  "message": "参数不合法"
}
```

## 查询员工列表（默认查询未删除员工）

- url: <mark>GET /api/staffs
- 请求参数:
  | 参数名 | 类型 | 是否必须 | 说明 |
  | ---------------- | ------- | ---- | ------------------------------------ |
  | `keyword` | String | 否 | 模糊搜索（匹配员工姓名和电话） |
  | `commission_min` | Decimal | 否 | 提成比例下限（如：0.05 表示 5%） |
  | `commission_max` | Decimal | 否 | 提成比例上限 |
  | `sortBy` | String | 否 | 排序字段，可选值：`name`（姓名）、`commission`（提成），默认`name`排序 |
  | `order` | String | 否 | 排序方式：`asc`（升序）或 `desc`（降序），默认：`asc` |
  | `page` | Integer | 否 | 当前页码（默认：1） |
  | `page_size` | Integer | 否 | 每页数量（默认：10） |

- 请求实例：

```json
GET /api/staffs?keyword=张&commission_min=10&page=1&page_size=5
```

- 响应实例:

```json
{
  "code": 200,
  "message": "success",
  "total": 2,
  "list": [
    {
      "id": 1,
      "name": "张三",
      "phone": "12345678901",
      "commission": 20.0,
      "create_time": "2025-08-01",
      "update_time": "2025-08-03"
    },
    {
      "id": 2,
      "name": "张晓",
      "phone": "19876543210",
      "commission": 15.0,
      "create_time": "2025-08-02",
      "update_time": "2025-08-04"
    }
  ]
}
```

## 增加员工

- url: <mark>POST /api/staffs
- 请求体：
  | 字段名 | 类型 | 是否必须 | 说明 |
  | ----------- | ------ | ---- | ------------- |
  | name | String | 是 | 员工姓名 |
  | phone | String | 是 | 联系电话 |
  | commission | Double | 是 | 提成比例，范围为 0\~1 |
  | description | String | 否 | 备注描述 |
- 请求实例：

```json
{
  "name": "李华",
  "phone": "18888888888",
  "commission": 0.15,
  "description": "擅长足疗"
}
```

- 响应实例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 10
  }
}
```

## 修改员工信息(覆盖式)

- url: <mark>PUT /api/staffs/{id}
- 请求体：
  | 字段名 | 类型 | 是否必须 | 说明 |
  | ----------- | ------ | ---- | ---- |
  | name | String | 是 | 员工姓名 |
  | phone | String | 是 | 电话 |
  | commission | Double | 是 | 提成比例 |
  | description | String | 否 | 员工描述 |
- 请求实例：

```json
{
  "name": "李华",
  "phone": "18888888888",
  "commission": 0.12,
  "description": "擅长足部按摩"
}
```

- 响应实例：

```json
{
  "code": 200,
  "message": "success"
}
```

## 逻辑删除员工

- url: <mark>DELETE /api/staffs/{id}
- 请求示例：

```json
DELETE /api/staffs/10
```

- 响应示例：

```json
{
  "code": 200,
  "message": "success"
}
```

## 查询已删除员工列表

- url: <mark>GET /api/staffs/deleted
- 请求参数：
  | 参数名 | 类型 | 是否必须 | 说明 |
  | ---------------- | ------- | ---- | ------------------------------------ |
  | `keyword` | String | 否 | 模糊搜索（匹配员工姓名和电话） |
  | `commission_min` | Decimal | 否 | 提成比例下限（如：0.05 表示 5%） |
  | `commission_max` | Decimal | 否 | 提成比例上限 |
  | `sortBy` | String | 否 | 排序字段，可选值：`name`（姓名）、`commission`（提成），默认`name`排序 |
  | `order` | String | 否 | 排序方式：`asc`（升序）或 `desc`（降序），默认：`asc` |
  | `page` | Integer | 否 | 当前页码（默认：1） |
  | `page_size` | Integer | 否 | 每页数量（默认：10） |

- 请求实例：

```json
GET /api/staffs/deleted?keyword=张&commission_min=10&page=1&page_size=5
```

- 响应实例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 2,
    "list": [
      {
        "id": 12,
        "name": "王五",
        "phone": "13888888888",
        "commission": 15,
        "create_time": "2025-07-01 09:00:00",
        "update_time": "2025-08-01 15:45:00"
      },
      {
        "id": 18,
        "name": "赵六",
        "phone": "13999999999",
        "commission": 20,
        "create_time": "2025-07-05 10:30:00",
        "update_time": "2025-08-03 17:20:00"
      }
    ]
  }
}
```

## 逻辑恢复员工

- url: <mark>POST /api/staffs/{id}/restore
- 响应实例：

```json
{
  "code": 200,
  "message": "success"
}
```

# 员工每日状态管理

## 新增员工每日状态

- url: <mark>POST /api/staff-status
- 请求体：

```json
{
  "staff_id": 10,
  "date": "2025-08-05",
  "status": "到岗"
}
```

## 更新员工每日状态

- url: <mark>PUT /api/staff-status
- 请求体:

```json
{
  "staff_id": 10,
  "date": "2025-08-05",
  "status": "未到岗"
}
```

## 获取员工状态字典（用于下拉菜单）

- url: <mark>GET /api/staff-status/categories
- 请求参数: 无
- 响应示例:

```json
{
  "code": 200,
  "message": "success",
  "data": ["到岗", "未到岗"]
}
```

## 查询员工状态(可有高级筛选界面)

- url: <mark>GET /api/staff-status
- 查询参数:
  | 参数名 | 类型 | 是否必传 | 说明 |
  | ------------ | ------ | ---- | ----------------------------------- |
  | `keyword` | string | 否 | 员工姓名或电话模糊查询（合并输入框） |
  | `status` | string | 否 | 状态枚举值（如：`到岗`、`未到岗`） |
  | `start_date` | string | 否 | 起始日期（格式：`YYYY-MM-DD`） |
  | `end_date` | string | 否 | 结束日期（格式：`YYYY-MM-DD`） |
  | `sortBy` | string | 否 | 排序字段，可选值：`staff_name`、`date`， 默认`date` |
  | `order` | string | 否 | 排序方式：`asc`（升序）或 `desc`（降序），默认 `desc` |
  | `page` | int | 否 | 页码（默认：1） |
  | `size` | int | 否 | 每页数量（默认：10） |

- 请求示例:

```json
GET /api/staff-status?keyword=张&status=ON_DUTY&start_date=2025-08-01&end_date=2025-08-05&page=1&size=10
```

- 响应示例:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 3,
    "list": [
      {
        "record_id": 101,
        "staff_id": 1,
        "staff_name": "张三",
        "phone": "12345678901",
        "status": "ON_DUTY",
        "date": "2025-08-01"
      },
      {
        "record_id": 102,
        "staff_id": 1,
        "staff_name": "张三",
        "phone": "12345678901",
        "status": "ON_DUTY",
        "date": "2025-08-02"
      }
    ]
  }
}
```

# 商店支出管理

## 新增支出记录

- url: <mark>POST /api/expenses
- 请求体:

```json
{
  "category": "房租水电",
  "amount": 880.5,
  "spend_date": "2025-08-01",
  "description": "8月房租"
}
```

- 响应

```json
{
  "code": 200,
  "message": "success"
}
```

## 修改支出记录（需谨慎）

- url: <mark>PUT /api/expenses{id}
- 请求体:

```json
{
  "category": "商品进货",
  "amount": 1200.0,
  "spend_date": "2025-08-03",
  "description": "补货：足浴精油"
}
```

- 响应:

```json
{
  "code": 200,
  "message": "success"
}
```

## 查询支出记录(默认未删除)

- url: <mark>GET /api/expenses
- 查询参数:
  | 参数名 | 类型 | 是否可选 | 说明 |
  | ------------ | ------- | ---- | ---------------------------------------------- |
  | `category` | string | 是 | 支出类别（如：房租水电、设备维修、商品进货、其他） |
  | `min_amount` | decimal | 是 | 金额最小值 |
  | `max_amount` | decimal | 是 | 金额最大值 |
  | `start_date` | date | 是 | 支出起始日期（格式：YYYY-MM-DD） |
  | `end_date` | date | 是 | 支出结束日期（格式：YYYY-MM-DD） |
  | `keyword` | string | 是 | 模糊匹配 `description` 字段 |
  | `sortBy` | string | 是 | 排序字段，可选：`spend_date`, `amount`，默认：`spend_date` |
  | `order` | string | 是 | 排序方式，可选：`asc` 或 `desc`，默认：`desc` |
  | `page` | int | 是 | 当前页码（默认：1） |
  | `size` | int | 是 | 每页数量（默认：10） |

- 示例请求:

```json
GET /api/expenses?page=1&size=10&category=商品进货&min_amount=100&max_amount=2000
```

- 响应示例:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 3,
    "list": [
      {
        "id": 101,
        "category": "商品进货",
        "amount": 1688.0,
        "spend_date": "2025-08-01",
        "description": "进货足浴包",
        "create_time": "2025-08-01 10:23:11",
        "update_time": "2025-08-01 10:23:11"
      }
    ]
  }
}
```

## 逻辑删除支出记录

- url: <mark>DELETE /api/expenses
- 响应:

```json
{
  "code": 200,
  "message": "success"
}
```

## 逻辑恢复支出记录

- url: <mark>PUT /api/expenses/recover/{id}
- 响应:

```json
{
  "code": 200,
  "message": "success"
}
```

## 查询已删除支出记录

- url: <mark>GET /api/expenses/deleted
- 查询参数
  | 参数名 | 类型 | 是否可选 | 说明 |
  | ------------ | ------- | ---- | ---------------------------------------------- |
  | `category` | string | 是 | 支出类别（如：房租水电、设备维修、商品进货、其他） |
  | `min_amount` | decimal | 是 | 金额最小值 |
  | `max_amount` | decimal | 是 | 金额最大值 |
  | `start_date` | date | 是 | 支出起始日期（格式：YYYY-MM-DD） |
  | `end_date` | date | 是 | 支出结束日期（格式：YYYY-MM-DD） |
  | `keyword` | string | 是 | 模糊匹配 `description` 字段 |
  | `sortBy` | string | 是 | 排序字段，可选：`spend_date`, `amount`，默认：`spend_date` |
  | `order` | string | 是 | 排序方式，可选：`asc` 或 `desc`，默认：`desc` |
  | `page` | int | 是 | 当前页码（默认：1） |
  | `size` | int | 是 | 每页数量（默认：10） |

- 示例请求:

```json
GET /api/expenses/deleted?category=商品进货&page=1&size=10
```

- 响应示例:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1,
    "list": [
      {
        "id": 108,
        "category": "商品进货",
        "amount": 980.0,
        "spend_date": "2025-08-02",
        "description": "已删除的记录",
        "create_time": "2025-08-02 10:11:00",
        "update_time": "2025-08-04 09:00:00"
      }
    ]
  }
}
```

## 获取支出类别字典（用于下拉菜单）

- url: <mark>GET /api/expenses/categories
- 响应示例:

```json
{
  "code": 200,
  "message": "success",
  "data": ["房租水电", "设备维修", "商品进货", "其他"]
}
```

# 操作日志管理

## 查询操作日志（detail 字段的生成暂时存疑）

- url: <mark>GET /api/logs
- 请求参数:
  | 参数名 | 类型 | 是否必填 | 描述 |
  | ----------- | ------ | ---- | -------------------------------------------- |
  | `module` | string | 否 | 操作模块（如：`MEMBER`、`STAFF`、`EXPENSE` 等） |
  | `operation` | string | 否 | 操作类型（如：`CREATE`、`UPDATE`、`DELETE` 等） |
  | `startTime` | string | 否 | 操作时间起始（格式：`yyyy-MM-dd`） |
  | `endTime` | string | 否 | 操作时间结束（格式：`yyyy-MM-dd`） |
  | `sortBy` | string | 否 | 排序字段，支持：`operation_time`，默认：`operation_time` |
  | `order` | string | 否 | 排序方式：`asc` 或 `desc`，默认：`desc` |
  | `page` | int | 否 | 当前页码，默认：`1` |
  | `size` | int | 否 | 每页条数，默认：`10` |

- 请求示例：

```json
GET /api/logs?module=MEMBER&operation=CREATE&page=1&size=10
```

- 响应示例：

```json
{
  "code": 200,
  "message": "success",
  "total": 3,
  "list": [
    {
      "id": 1,
      "module": "MEMBER",
      "operation": "CREATE",
      "detail": {
        "member_id": 1001,
        "name": "李四",
        "phone": "138****8888"
      },
      "operation_time": "2025-08-05 14:22:33"
    },
    {
      "id": 2,
      "module": "STAFF",
      "operation": "UPDATE",
      "detail": {
        "staff_id": 2003,
        "old_commission": 0.3,
        "new_commission": 0.5
      },
      "operation_time": "2025-08-05 15:10:11"
    }
  ]
}
```

# 数据可视化接口

## 获取商店收入变化折线图数据（week/month:day, year:month, all:year）

- url: <mark>GET /api/stats/income-trend
- 请求参数:
  | 参数名 | 类型 | 是否必传 | 描述 |
  | --------- | ------ | ---- | -------------------------------------- |
  | period | string | 是 | 时间范围，可选：`week`, `month`, `year`, `all` |
- 请求示例：

```json
  GET /api/stats/income-trend?period=year&dimension=day
```

- 响应示例:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "period": "month", // 前端传的 period
    "dimension": "day", // 后端自动确定的 dimension
    "values": [
      {
        "label": "2025-08-01",
        "value": 888.0
      },
      {
        "label": "2025-08-02",
        "value": 998.0
      },
      {
        "label": "2025-08-03",
        "value": 766.0
      }
      // ... 一直到今天（非整月）
    ]
  }
}
```

## 获取商店净收入变化折线图数据（week/month:day, year:month, all:year）

- url: <mark>GET /api/stats/net-income-trend
- 请求参数:
  | 参数名 | 类型 | 是否必填 | 描述 |
  | ------ | ------ | -------- | ------------------------------------------------- |
  | period | string | 是 | 时间周期，可选：`week` / `month` / `year` / `all` |
- 响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "period": "month",
    "dimension": "day",
    "values": [
      {
        "label": "2025-08-01",
        "value": 1280.5
      },
      {
        "label": "2025-08-02",
        "value": 965.0
      },
      {
        "label": "2025-08-03",
        "value": 1420.0
      }
      // ...
    ]
  }
}
```

## 获取每位员工的服务收益变化折线图和对比饼图数据(week/month:day, year:month, all:year)

- url: <mark>GET /api/stats/staff-income-trend
- 请求参数：
  | 参数名 | 类型 | 是否必填 | 描述 |
  | ------ | ------ | ---- | ----------------------------------------- |
  | period | string | 是 | 查询周期，可选：`week` / `month` / `year` / `all` |
- 响应示例:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "source": [
      ["staff_name", "2025-08-01", "2025-08-02", "2025-08-03"],
      ["张三", 120, 130, 140],
      ["李四", 80, 100, 90],
      ["王五", 60, 70, 65]
    ]
  }
}
```

## 获取会员与普通用户消费占比饼图数据（week,month, year, all）

- url: <mark>GET /api/stats/consumption-ratio
- 请求参数:
  | 参数名 | 类型 | 是否必填 | 描述 |
  | ------ | ------ | ---- | ----------------------------------------- |
  | period | string | 是 | 查询周期，可选：`week` / `month` / `year` / `all` |
- 响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    { "type": "会员", "amount": 3100.5 },
    { "type": "普通顾客", "amount": 1450.75 }
  ]
}
```

## 获取每个项目的消费占比饼图数据（week, month, year, all）

- url: <mark>GET /api/stats/project-income-ratio
- 请求参数:
  | 参数名 | 类型 | 是否必填 | 描述 |
  | ------ | ------ | ---- | ----------------------------------------- |
  | period | string | 是 | 查询周期，可选：`week` / `month` / `year` / `all` |
- 响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    { "project": "肩颈推拿", "amount": 5200.0 },
    { "project": "足疗", "amount": 4800.0 },
    { "project": "拔罐", "amount": 3100.0 },
    { "project": "艾灸", "amount": 2900.0 },
    { "project": "全身按摩", "amount": 2500.0 },
    { "project": "修脚", "amount": 2200.0 },
    { "project": "其他", "amount": 4300.0 }
  ]
}
```

## 获取全部总收入， 全部净收入， 全部支出（week, month, year, all）

- url: <mark>GET /api/stats/summary
- 请求参数:
  | 参数名 | 类型 | 是否必填 | 描述 |
  | ------ | ------ | ---- | ----------------------------------------- |
  | period | string | 是 | 查询周期，可选：`week` / `month` / `year` / `all` |
- 响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "period": "month",
    "total_income": 25800.5,
    "total_expense": 7300.0,
    "net_income": 18500.5
  }
}
```

## 获取本月内的某会员的消费记录（timeline 简单版）

- url: <mark>GET /api/stats/member-consumption
- 请求参数:
  | 参数名 | 类型 | 是否必填 | 描述 |
  | ------ | ------ | ---- | ----------------------------------------- |
  | member_id | string | 是 | 会员 ID |
- 响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "consume_date": "2025-08-03",
      "project": "足疗",
      "price": 88.0
    },
    {
      "consume_date": "2025-08-03",
      "project": "拔罐",
      "price": 40.0
    },
    {
      "consume_date": "2025-08-01",
      "project": "修脚",
      "price": 88.0
    }
  ]
}
```

## 获取单个员工本周服务记录（timeline）

- url: <mark>GET /api/stats/staff-service
- 请求参数:
  | 参数名 | 类型 | 是否必填 | 描述 |
  | ------ | ------ | ---- | ----------------------------------------- |
  | staff_id | string | 是 | 员工 ID |
- 响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "service_time": "2025-08-04",
      "project": "修脚",
      "earnings": 28.0
    },
    {
      "service_time": "2025-08-04",
      "project": "拔罐",
      "earnings": 12.0
    },
    {
      "service_time": "2025-08-02",
      "project": "足疗",
      "earnings": 25.0
    }
  ]
}
```

## 获取所有员工全部，本年，本月实际所得薪资

- url: <mark>GET /api/stats/staff-salaries
- 请求参数: 空
- 响应示例:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "staff_id": 1,
      "staff_name": "张三",
      "total_salary": 35200.5,
      "year_salary": 18350.75,
      "month_salary": 2650.0
    },
    {
      "staff_id": 2,
      "staff_name": "李四",
      "total_salary": 29800.0,
      "year_salary": 14250.25,
      "month_salary": 1850.0
    }
  ]
}
```

# 数据范围限制建议

为了保证系统性能和数据管理的有效性，以下是各模块数据查询的默认时间范围限制建议：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "default_time_ranges": {
      "member_consumption": "最近 1 年",
      "staff_service_records": "最近 1 年",
      "operation_logs": "最近 3 个月",
      "recharge_records": "最近 1 年",
      "member_list": "无时间限制",
      "staff_list": "无时间限制"
    }
  }
}
```

| 模块         | 默认时间范围（建议） | 原因                         |
| ------------ | -------------------- | ---------------------------- |
| 会员消费记录 | 最近 1 年            | 近一年消费有价值，太久意义小 |
| 员工服务记录 | 最近 1 年            | 可以分析近期工作表现         |
| 操作日志     | 最近 3 个月          | 管理后台重要变动多在近阶段   |
| 充值记录     | 最近 1 年            | 便于会员管理与统计分析       |
| 会员列表     | 无需限制             | 通常数量不大                 |
| 员工列表     | 无需限制             | 通常数量不大                 |
