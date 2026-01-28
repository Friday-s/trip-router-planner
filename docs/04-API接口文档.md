# 旅游线路规划平台设计与实现 - API接口文档

> 版本：v1.0
> 更新日期：2026-01-28
> Base URL：`/api`

---

## 一、接口规范

### 1.1 通用规范

| 项目 | 规范 |
|-----|------|
| 协议 | HTTPS |
| 数据格式 | JSON |
| 字符编码 | UTF-8 |
| 时间格式 | ISO 8601 (`yyyy-MM-dd'T'HH:mm:ss`) |
| 分页参数 | `page` (从1开始), `size` (默认10) |

### 1.2 请求头

| Header | 说明 | 必填 |
|--------|------|------|
| Content-Type | `application/json` | 是 |
| Authorization | `Bearer <token>` | 需登录的接口 |

### 1.3 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

### 1.4 响应码定义

| code | 含义 | 说明 |
|------|------|------|
| 200 | 成功 | 请求成功 |
| 400 | 参数错误 | 请求参数不合法 |
| 401 | 未认证 | 未登录或token过期 |
| 403 | 无权限 | 没有操作权限 |
| 404 | 资源不存在 | 请求的资源不存在 |
| 500 | 服务器错误 | 服务器内部错误 |

### 1.5 分页响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [ ... ],
    "total": 100,
    "page": 1,
    "size": 10,
    "pages": 10
  }
}
```

---

## 二、认证模块

### 2.1 用户注册

**POST** `/auth/register`

注册新用户账号。

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| username | string | 是 | 用户名，3-50字符 |
| email | string | 是 | 邮箱 |
| password | string | 是 | 密码，6-20字符 |

**请求示例**

```json
{
  "username": "traveler",
  "email": "traveler@example.com",
  "password": "123456"
}
```

**响应示例**

```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": 1,
    "username": "traveler",
    "email": "traveler@example.com"
  }
}
```

**错误码**

| code | message |
|------|---------|
| 400 | 用户名已存在 |
| 400 | 邮箱已被注册 |
| 400 | 密码长度不符合要求 |

---

### 2.2 用户登录

**POST** `/auth/login`

用户登录，获取JWT Token。

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| email | string | 是 | 邮箱 |
| password | string | 是 | 密码 |

**请求示例**

```json
{
  "email": "traveler@example.com",
  "password": "123456"
}
```

**响应示例**

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 86400,
    "user": {
      "id": 1,
      "username": "traveler",
      "email": "traveler@example.com",
      "avatar": null,
      "role": "user"
    }
  }
}
```

**错误码**

| code | message |
|------|---------|
| 400 | 邮箱或密码错误 |
| 400 | 账号已被禁用 |

---

### 2.3 获取当前用户信息

**GET** `/auth/me`

获取当前登录用户的信息。

**请求头**

```
Authorization: Bearer <token>
```

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "traveler",
    "email": "traveler@example.com",
    "avatar": null,
    "role": "user",
    "createdAt": "2026-01-01T10:00:00"
  }
}
```

**错误码**

| code | message |
|------|---------|
| 401 | 未登录或token已过期 |

---

## 三、POI模块

### 3.1 获取POI列表

**GET** `/pois`

获取景点列表，支持筛选和分页。

**请求参数 (Query)**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| city | string | 否 | 城市，默认"大理" |
| area | string | 否 | 区域筛选 |
| tag | string | 否 | 标签筛选 |
| keyword | string | 否 | 关键词搜索(名称) |
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页数量，默认10 |

**请求示例**

```
GET /api/pois?city=大理&area=古城区&page=1&size=10
```

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "name": "大理古城",
        "lng": 100.1653,
        "lat": 25.6969,
        "address": "云南省大理白族自治州大理市",
        "city": "大理",
        "area": "古城区",
        "tags": ["古镇", "摄影", "民族文化"],
        "brief": "大理古城位于云南西部...",
        "coverImage": "https://...",
        "visitDuration": 180,
        "openTime": "全天开放",
        "ticketPrice": 0
      }
    ],
    "total": 50,
    "page": 1,
    "size": 10,
    "pages": 5
  }
}
```

---

### 3.2 获取POI详情

**GET** `/pois/{id}`

获取单个景点的详细信息。

**路径参数**

| 参数 | 类型 | 说明 |
|-----|------|------|
| id | long | POI ID |

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "大理古城",
    "lng": 100.1653,
    "lat": 25.6969,
    "address": "云南省大理白族自治州大理市",
    "city": "大理",
    "area": "古城区",
    "tags": ["古镇", "摄影", "民族文化"],
    "brief": "大理古城位于云南西部，是中国首批24个国家历史文化名城之一",
    "coverImage": "https://...",
    "visitDuration": 180,
    "openTime": "全天开放",
    "ticketPrice": 0,
    "createdAt": "2026-01-01T10:00:00",
    "updatedAt": "2026-01-01T10:00:00"
  }
}
```

**错误码**

| code | message |
|------|---------|
| 404 | 景点不存在 |

---

### 3.3 获取区域列表

**GET** `/pois/areas`

获取指定城市的所有区域列表。

**请求参数 (Query)**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| city | string | 否 | 城市，默认"大理" |

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    "古城区",
    "洱海周边",
    "苍山",
    "双廊",
    "喜洲",
    "剑川"
  ]
}
```

---

### 3.4 获取标签列表

**GET** `/pois/tags`

获取所有可用的POI标签。

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    "古镇",
    "自然风光",
    "民族文化",
    "美食",
    "摄影",
    "徒步",
    "亲子",
    "小众"
  ]
}
```

---

## 四、行程模块

### 4.1 生成行程

**POST** `/itineraries/generate`

根据参数自动生成行程。

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| city | string | 否 | 城市，默认"大理" |
| days | int | 是 | 天数，1-7 |
| theme | string | 否 | 主题标签 |
| pace | string | 否 | 节奏：relaxed(轻松)/moderate(适中)/intensive(紧凑)，默认moderate |
| travelers | string | 否 | 出行人群：solo/couple/family/friends |
| startLng | number | 否 | 起点经度 |
| startLat | number | 否 | 起点纬度 |

**请求示例**

```json
{
  "city": "大理",
  "days": 3,
  "theme": "古镇",
  "pace": "relaxed",
  "travelers": "couple"
}
```

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "itineraryId": 123,
    "city": "大理",
    "days": 3,
    "theme": "古镇",
    "title": "大理3日浪漫古镇之旅",
    "summary": "漫步白族古镇，感受风花雪月的浪漫",
    "dayList": [
      {
        "dayIndex": 1,
        "theme": "大理古城慢时光",
        "notes": "今日以古城为主，不赶时间，慢慢感受",
        "items": [
          {
            "orderIndex": 1,
            "startTime": "10:00",
            "poi": {
              "id": 1,
              "name": "大理古城",
              "lng": 100.1653,
              "lat": 25.6969,
              "brief": "大理古城位于云南西部...",
              "coverImage": "https://...",
              "visitDuration": 180
            },
            "tips": "从南门进入，沿复兴路漫步，感受白族建筑风情",
            "routeToNext": {
              "distanceMeter": 1500,
              "walkDurationSec": 1200,
              "driveDurationSec": 300,
              "transitDurationSec": 600,
              "recommendMode": "walk"
            }
          },
          {
            "orderIndex": 2,
            "startTime": "15:00",
            "poi": {
              "id": 6,
              "name": "崇圣寺三塔",
              "lng": 100.1428,
              "lat": 25.7144,
              "brief": "崇圣寺三塔是大理的标志性建筑...",
              "coverImage": "https://...",
              "visitDuration": 120
            },
            "tips": "傍晚光线最佳，可拍摄三塔倒影",
            "routeToNext": null
          }
        ]
      },
      {
        "dayIndex": 2,
        "theme": "双廊洱海风光",
        "notes": "建议在双廊住一晚，体验洱海日落日出",
        "items": [ ... ]
      },
      {
        "dayIndex": 3,
        "theme": "喜洲田园风光",
        "notes": "今日较为轻松，可慢慢拍照",
        "items": [ ... ]
      }
    ],
    "tips": ["大理紫外线强，注意防晒", "早晚温差大，带件薄外套"],
    "mapData": {
      "center": [100.1653, 25.6969],
      "zoom": 12,
      "markers": [
        {
          "poiId": 1,
          "name": "大理古城",
          "position": [100.1653, 25.6969],
          "dayIndex": 1,
          "orderIndex": 1
        }
      ],
      "polylines": [
        {
          "fromPoiId": 1,
          "toPoiId": 6,
          "path": [[100.1653, 25.6969], [100.1428, 25.7144]],
          "dayIndex": 1
        }
      ]
    }
  }
}
```

**错误码**

| code | message |
|------|---------|
| 400 | 天数必须在1-7之间 |
| 400 | 该城市暂无景点数据 |
| 500 | 行程生成失败，请稍后重试 |

---

### 4.2 获取行程详情

**GET** `/itineraries/{id}`

获取行程的完整详情。

**路径参数**

| 参数 | 类型 | 说明 |
|-----|------|------|
| id | long | 行程ID |

**响应示例**

与生成行程的响应格式相同。

**错误码**

| code | message |
|------|---------|
| 404 | 行程不存在 |

---

### 4.3 保存行程

**POST** `/itineraries/{id}/save`

将行程保存到用户账户（需登录）。

**请求头**

```
Authorization: Bearer <token>
```

**路径参数**

| 参数 | 类型 | 说明 |
|-----|------|------|
| id | long | 行程ID |

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| title | string | 否 | 行程标题 |

**请求示例**

```json
{
  "title": "我的大理三日游"
}
```

**响应示例**

```json
{
  "code": 200,
  "message": "保存成功",
  "data": {
    "itineraryId": 123
  }
}
```

**错误码**

| code | message |
|------|---------|
| 401 | 请先登录 |
| 404 | 行程不存在 |

---

### 4.4 获取我的行程列表

**GET** `/itineraries/my`

获取当前用户保存的行程列表（需登录）。

**请求头**

```
Authorization: Bearer <token>
```

**请求参数 (Query)**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页数量，默认10 |

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 123,
        "title": "我的大理三日游",
        "city": "大理",
        "days": 3,
        "theme": "古镇",
        "createdAt": "2026-01-15T10:00:00"
      }
    ],
    "total": 5,
    "page": 1,
    "size": 10,
    "pages": 1
  }
}
```

---

### 4.5 删除行程

**DELETE** `/itineraries/{id}`

删除用户保存的行程（需登录）。

**请求头**

```
Authorization: Bearer <token>
```

**路径参数**

| 参数 | 类型 | 说明 |
|-----|------|------|
| id | long | 行程ID |

**响应示例**

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

**错误码**

| code | message |
|------|---------|
| 401 | 请先登录 |
| 403 | 无权删除该行程 |
| 404 | 行程不存在 |

---

## 五、社区模块

### 5.1 获取帖子列表

**GET** `/posts`

获取社区帖子列表。

**请求参数 (Query)**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| sort | string | 否 | 排序方式: latest(默认)/hot |
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页数量，默认10 |

**请求示例**

```
GET /api/posts?sort=hot&page=1&size=10
```

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "title": "超详细的大理三日游攻略",
        "summary": "分享我的大理之行，古城、洱海、苍山全都有...",
        "author": {
          "id": 1,
          "username": "traveler",
          "avatar": null
        },
        "itinerary": {
          "id": 123,
          "city": "大理",
          "days": 3,
          "theme": "古镇"
        },
        "likeCount": 128,
        "favoriteCount": 56,
        "liked": false,
        "favorited": false,
        "createdAt": "2026-01-10T14:30:00"
      }
    ],
    "total": 100,
    "page": 1,
    "size": 10,
    "pages": 10
  }
}
```

**说明**

- `liked` 和 `favorited` 字段：若用户已登录，返回当前用户是否点赞/收藏；未登录返回 `false`

---

### 5.2 获取帖子详情

**GET** `/posts/{id}`

获取帖子详情，包含关联的完整行程。

**路径参数**

| 参数 | 类型 | 说明 |
|-----|------|------|
| id | long | 帖子ID |

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "超详细的大理三日游攻略",
    "summary": "分享我的大理之行，古城、洱海、苍山全都有...",
    "author": {
      "id": 1,
      "username": "traveler",
      "avatar": null
    },
    "itinerary": {
      "itineraryId": 123,
      "city": "大理",
      "days": 3,
      "theme": "古镇",
      "dayList": [ ... ],
      "mapData": { ... }
    },
    "likeCount": 128,
    "favoriteCount": 56,
    "liked": false,
    "favorited": false,
    "createdAt": "2026-01-10T14:30:00"
  }
}
```

**错误码**

| code | message |
|------|---------|
| 404 | 帖子不存在 |

---

### 5.3 发布帖子

**POST** `/posts`

将行程发布到社区（需登录）。

**请求头**

```
Authorization: Bearer <token>
```

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| itineraryId | long | 是 | 行程ID |
| title | string | 是 | 标题，2-100字符 |
| summary | string | 否 | 摘要，最多500字符 |

**请求示例**

```json
{
  "itineraryId": 123,
  "title": "超详细的大理三日游攻略",
  "summary": "分享我的大理之行，古城、洱海、苍山全都有..."
}
```

**响应示例**

```json
{
  "code": 200,
  "message": "发布成功",
  "data": {
    "postId": 1
  }
}
```

**错误码**

| code | message |
|------|---------|
| 400 | 标题不能为空 |
| 400 | 该行程已发布过帖子 |
| 401 | 请先登录 |
| 403 | 无权发布该行程 |
| 404 | 行程不存在 |

---

### 5.4 点赞帖子

**PUT** `/posts/{id}/like`

点赞帖子（需登录）。

**请求头**

```
Authorization: Bearer <token>
```

**路径参数**

| 参数 | 类型 | 说明 |
|-----|------|------|
| id | long | 帖子ID |

**响应示例**

```json
{
  "code": 200,
  "message": "点赞成功",
  "data": {
    "liked": true,
    "likeCount": 129
  }
}
```

---

### 5.5 取消点赞

**DELETE** `/posts/{id}/like`

取消点赞（需登录）。

**请求头**

```
Authorization: Bearer <token>
```

**路径参数**

| 参数 | 类型 | 说明 |
|-----|------|------|
| id | long | 帖子ID |

**响应示例**

```json
{
  "code": 200,
  "message": "取消点赞成功",
  "data": {
    "liked": false,
    "likeCount": 128
  }
}
```

---

### 5.6 收藏帖子

**PUT** `/posts/{id}/favorite`

收藏帖子（需登录）。

**请求头**

```
Authorization: Bearer <token>
```

**路径参数**

| 参数 | 类型 | 说明 |
|-----|------|------|
| id | long | 帖子ID |

**响应示例**

```json
{
  "code": 200,
  "message": "收藏成功",
  "data": {
    "favorited": true,
    "favoriteCount": 57
  }
}
```

---

### 5.7 取消收藏

**DELETE** `/posts/{id}/favorite`

取消收藏（需登录）。

**请求头**

```
Authorization: Bearer <token>
```

**路径参数**

| 参数 | 类型 | 说明 |
|-----|------|------|
| id | long | 帖子ID |

**响应示例**

```json
{
  "code": 200,
  "message": "取消收藏成功",
  "data": {
    "favorited": false,
    "favoriteCount": 56
  }
}
```

---

### 5.8 获取我发布的帖子

**GET** `/posts/my`

获取当前用户发布的帖子列表（需登录）。

**请求头**

```
Authorization: Bearer <token>
```

**请求参数 (Query)**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页数量，默认10 |

**响应格式**

同 5.1 获取帖子列表。

---

### 5.9 获取我收藏的帖子

**GET** `/posts/favorites`

获取当前用户收藏的帖子列表（需登录）。

**请求头**

```
Authorization: Bearer <token>
```

**请求参数 (Query)**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页数量，默认10 |

**响应格式**

同 5.1 获取帖子列表。

---

## 六、管理模块

> 以下接口需要管理员权限 (role = admin)

### 6.1 POI管理 - 新增

**POST** `/admin/pois`

新增景点。

**请求头**

```
Authorization: Bearer <token>
```

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| name | string | 是 | 景点名称 |
| lng | number | 是 | 经度 |
| lat | number | 是 | 纬度 |
| address | string | 否 | 详细地址 |
| city | string | 是 | 城市 |
| area | string | 否 | 区域 |
| tags | string[] | 否 | 标签数组 |
| brief | string | 否 | 简介 |
| coverImage | string | 否 | 封面图URL |
| visitDuration | int | 否 | 预计游玩时长(分钟) |
| openTime | string | 否 | 营业时间描述 |
| ticketPrice | number | 否 | 门票价格 |
| status | int | 否 | 状态: 0下架 1上架，默认1 |

**请求示例**

```json
{
  "name": "新景点",
  "lng": 100.1234,
  "lat": 25.5678,
  "city": "大理",
  "area": "古城区",
  "tags": ["古镇", "摄影"],
  "brief": "这是一个新的景点...",
  "visitDuration": 120
}
```

**响应示例**

```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 11
  }
}
```

---

### 6.2 POI管理 - 编辑

**PUT** `/admin/pois/{id}`

编辑景点信息。

**请求头**

```
Authorization: Bearer <token>
```

**路径参数**

| 参数 | 类型 | 说明 |
|-----|------|------|
| id | long | POI ID |

**请求参数**

同新增接口，所有字段均为可选，只传需要修改的字段。

**请求示例**

```json
{
  "brief": "更新后的简介...",
  "visitDuration": 150
}
```

**响应示例**

```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

---

### 6.3 POI管理 - 删除/下架

**DELETE** `/admin/pois/{id}`

删除或下架景点。

**请求头**

```
Authorization: Bearer <token>
```

**路径参数**

| 参数 | 类型 | 说明 |
|-----|------|------|
| id | long | POI ID |

**请求参数 (Query)**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| hard | boolean | 否 | 是否硬删除，默认false(下架) |

**响应示例**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

---

### 6.4 帖子管理 - 列表

**GET** `/admin/posts`

获取所有帖子列表（包含已下架）。

**请求头**

```
Authorization: Bearer <token>
```

**请求参数 (Query)**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| status | int | 否 | 状态筛选: 0下架 1正常 |
| keyword | string | 否 | 标题关键词搜索 |
| page | int | 否 | 页码，默认1 |
| size | int | 否 | 每页数量，默认10 |

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "title": "超详细的大理三日游攻略",
        "author": {
          "id": 1,
          "username": "traveler"
        },
        "likeCount": 128,
        "favoriteCount": 56,
        "status": 1,
        "createdAt": "2026-01-10T14:30:00"
      }
    ],
    "total": 100,
    "page": 1,
    "size": 10,
    "pages": 10
  }
}
```

---

### 6.5 帖子管理 - 修改状态

**PUT** `/admin/posts/{id}/status`

修改帖子状态（上架/下架）。

**请求头**

```
Authorization: Bearer <token>
```

**路径参数**

| 参数 | 类型 | 说明 |
|-----|------|------|
| id | long | 帖子ID |

**请求参数**

| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| status | int | 是 | 状态: 0下架 1上架 |

**请求示例**

```json
{
  "status": 0
}
```

**响应示例**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

---

### 6.6 帖子管理 - 删除

**DELETE** `/admin/posts/{id}`

永久删除帖子。

**请求头**

```
Authorization: Bearer <token>
```

**路径参数**

| 参数 | 类型 | 说明 |
|-----|------|------|
| id | long | 帖子ID |

**响应示例**

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

### 6.7 数据统计

**GET** `/admin/stats`

获取平台数据统计。

**请求头**

```
Authorization: Bearer <token>
```

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userCount": 1000,
    "poiCount": 50,
    "itineraryCount": 500,
    "postCount": 200,
    "todayItineraryCount": 20,
    "todayPostCount": 5
  }
}
```

---

## 七、错误响应示例

### 7.1 参数错误 (400)

```json
{
  "code": 400,
  "message": "天数必须在1-7之间",
  "data": null
}
```

### 7.2 未认证 (401)

```json
{
  "code": 401,
  "message": "请先登录",
  "data": null
}
```

### 7.3 无权限 (403)

```json
{
  "code": 403,
  "message": "无权访问该资源",
  "data": null
}
```

### 7.4 资源不存在 (404)

```json
{
  "code": 404,
  "message": "帖子不存在",
  "data": null
}
```

### 7.5 服务器错误 (500)

```json
{
  "code": 500,
  "message": "系统繁忙，请稍后重试",
  "data": null
}
```
