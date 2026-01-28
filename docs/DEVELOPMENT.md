# 开发指南

> 本文档为开发人员提供项目开发规范和快速上手指南。

---

## 环境准备

### 必需环境

| 软件 | 版本要求 | 安装验证命令 |
|------|----------|--------------|
| JDK | 17+ | `java -version` |
| Maven | 3.8+ | `mvn -v` |
| Node.js | 18+ | `node -v` |
| MySQL | 8.0+ | `mysql --version` |
| Redis | 7.0+ | `redis-cli ping` |

### 开发工具（推荐）

- **IDE**: IntelliJ IDEA / VS Code
- **数据库**: Navicat / DataGrip / DBeaver
- **API测试**: Postman / Apifox

---

## 快速启动

### 1. 克隆项目

```bash
git clone <repository-url>
cd trip-route-planner
```

### 2. 数据库初始化

```bash
# 创建数据库
mysql -u root -e "CREATE DATABASE IF NOT EXISTS trip_planner CHARACTER SET utf8mb4;"

# 导入表结构和数据
mysql -u root trip_planner < database/schema.sql
mysql -u root trip_planner < database/init-data.sql
```

### 3. 启动后端

```bash
cd backend

# 方式1: Maven命令
mvn spring-boot:run

# 方式2: IDE运行
# 运行 TripPlannerApplication.java 主类
```

后端启动后访问：http://localhost:8080/api

### 4. 启动前端

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端启动后访问：http://localhost:3000

---

## 项目结构说明

### 后端结构 (backend/)

```
src/main/java/com/dali/trip/
├── TripPlannerApplication.java   # 启动类
├── controller/                    # 控制器层
│   ├── AuthController.java        # 认证接口
│   ├── PoiController.java         # 景点接口
│   ├── ItineraryController.java   # 行程接口
│   └── PostController.java        # 帖子接口
├── service/                       # 服务层
│   ├── UserService.java
│   ├── PoiService.java
│   ├── ItineraryService.java
│   ├── PostService.java
│   └── RecommendationEngine.java  # 推荐引擎接口
├── service/impl/                  # 服务实现
│   ├── AIRecommendationEngine.java # AI推荐引擎
│   └── ...
├── mapper/                        # MyBatis Mapper
├── entity/                        # 实体类
├── dto/                           # 请求参数对象
├── vo/                            # 响应数据对象
├── config/                        # 配置类
│   ├── WebConfig.java             # Web配置
│   ├── RedisConfig.java           # Redis配置
│   ├── MyBatisPlusConfig.java     # MyBatis配置
│   └── JwtInterceptor.java        # JWT拦截器
└── common/                        # 公共组件
    ├── Result.java                # 统一响应
    ├── BusinessException.java     # 业务异常
    └── GlobalExceptionHandler.java # 全局异常处理
```

### 前端结构 (frontend/)

```
src/
├── main.js                # 入口文件
├── App.vue                # 根组件
├── api/                   # API接口封装
│   ├── auth.js            # 认证API
│   ├── poi.js             # 景点API
│   ├── itinerary.js       # 行程API
│   └── post.js            # 帖子API
├── components/            # 公共组件
│   ├── AppHeader.vue      # 页头
│   ├── AppFooter.vue      # 页脚
│   └── LoadingOverlay.vue # 加载遮罩
├── views/                 # 页面视图
│   ├── GeneratePage.vue   # 行程生成页（首页）
│   ├── ItineraryPage.vue  # 行程详情页
│   ├── CommunityPage.vue  # 社区广场
│   ├── PostPage.vue       # 帖子详情
│   ├── LoginPage.vue      # 登录页
│   ├── RegisterPage.vue   # 注册页
│   ├── ProfilePage.vue    # 个人中心
│   └── admin/             # 管理后台
│       ├── PoiManage.vue
│       ├── PostManage.vue
│       └── StatsPage.vue
├── stores/                # Pinia状态
│   └── user.js            # 用户状态
├── router/                # 路由配置
│   └── index.js
└── assets/                # 静态资源
```

---

## 开发规范

### Git 提交规范

使用约定式提交信息格式：

```
<type>(<scope>): <subject>

<body>
```

**type 类型**：
- `feat`: 新功能
- `fix`: Bug修复
- `docs`: 文档更新
- `style`: 代码格式（不影响功能）
- `refactor`: 重构
- `test`: 测试相关
- `chore`: 构建/工具变动

**示例**：
```bash
git commit -m "feat(itinerary): 添加行程编辑功能"
git commit -m "fix(auth): 修复登录token过期问题"
git commit -m "docs: 更新API文档"
```

### 分支管理

| 分支 | 用途 |
|------|------|
| main | 主分支，稳定版本 |
| develop | 开发分支 |
| feature/* | 功能分支 |
| fix/* | 修复分支 |
| release/* | 发布分支 |

### 代码规范

#### Java 后端
- 类名：大驼峰 `UserService`
- 方法名：小驼峰 `getUserById`
- 常量：全大写下划线 `MAX_PAGE_SIZE`
- 包名：全小写 `com.dali.trip.service`

#### JavaScript 前端
- 组件名：大驼峰 `AppHeader.vue`
- 函数名：小驼峰 `handleSubmit`
- 常量：全大写下划线 `API_BASE_URL`
- CSS类名：短横线 `nav-menu`

---

## API 接口约定

### 请求格式

```
POST /api/itineraries/generate
Content-Type: application/json
Authorization: Bearer <token>  (需要认证的接口)

{
  "city": "大理",
  "days": 3
}
```

### 响应格式

**成功响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

**错误响应**：
```json
{
  "code": 400,
  "message": "参数错误：天数必须在1-7天之间"
}
```

### 状态码

| 代码 | 含义 |
|------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

---

## 配置文件

### 后端配置 (application.yml)

关键配置项：

```yaml
# 数据库（可通过环境变量覆盖）
spring:
  datasource:
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:}

# DeepSeek API
deepseek:
  api-key: ${DEEPSEEK_API_KEY:your-key}

# JWT
jwt:
  secret: ${JWT_SECRET:your-secret}
  expiration: 86400000  # 24小时
```

### 前端配置 (vite.config.js)

```javascript
export default {
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
}
```

---

## 常见问题与解决方案

### 数据库相关

#### Q1: 后端启动报 `Public Key Retrieval is not allowed`
**原因**：MySQL 8.0+ 使用 `caching_sha2_password` 认证插件，需要额外配置。

**解决方案**：在 JDBC URL 中添加参数：
```
jdbc:mysql://localhost:3306/trip_planner?allowPublicKeyRetrieval=true&useSSL=false
```

#### Q2: 后端启动报 `Access denied for user 'root'@'localhost'`
**原因**：配置文件中的密码与实际 MySQL 密码不匹配。

**解决方案**：检查 `application.yml` 中的密码配置：
```yaml
spring:
  datasource:
    password: ${DB_PASSWORD:}  # 默认空密码，或设置为你的MySQL密码
```

#### Q3: POI查询报 `Parameter index out of range`
**原因**：MyBatis-Plus 的 `JSON_CONTAINS` 函数参数绑定语法错误。

**错误写法**：
```java
wrapper.apply("JSON_CONTAINS(tags, '\"{0}\"')", tag);
```

**正确写法**：
```java
wrapper.apply("JSON_CONTAINS(tags, {0})", "\"" + tag + "\"");
```

---

### 认证相关

#### Q4: 管理员账号无法登录
**原因**：`init-data.sql` 中的 BCrypt 哈希值与密码不匹配。

**解决方案**：
1. 通过注册接口创建新用户获取正确的哈希值
2. 或使用在线 BCrypt 工具生成哈希值
3. 更新数据库中的密码字段

**注意**：BCrypt 每次加密结果不同，但都能正确验证。确保使用正确的明文密码生成哈希。

#### Q5: 管理员登录后未跳转到后台
**原因**：登录成功后未根据角色判断跳转路径。

**解决方案**：在登录成功回调中添加角色判断：
```javascript
if (userStore.user?.role === 'admin') {
  router.push('/admin')
} else {
  router.push(route.query.redirect || '/')
}
```

---

### 地图相关

#### Q6: 地图只显示标记点，底图不渲染
**原因**：后端返回的坐标是 `BigDecimal` 类型，序列化为 JSON 后变成字符串，高德地图要求数字类型。

**解决方案**：在前端使用 `parseFloat()` 转换坐标：
```javascript
const center = [
  parseFloat(mapData.center[0]),
  parseFloat(mapData.center[1])
]
```

#### Q7: 高德地图 API 调用失败
**原因**：2021年12月后申请的 Key 必须配合安全密钥使用。

**解决方案**：
1. 在高德开放平台申请 JS API Key 和安全密钥
2. 在 `index.html` 中配置（注意顺序）：

```html
<!-- 安全密钥配置（必须在 loader.js 之前） -->
<script type="text/javascript">
  window._AMapSecurityConfig = {
    securityJsCode: '你的安全密钥'
  }
</script>
<!-- 高德地图 JS API Loader -->
<script src="https://webapi.amap.com/loader.js"></script>
```

3. 在 Vue 组件中使用 AMapLoader 加载：
```javascript
window.AMapLoader.load({
  key: '你的JS API Key',
  version: '2.0',
  plugins: []
}).then((AMap) => {
  // 初始化地图
})
```

#### Q8: 高德地图密钥体系说明

| 密钥类型 | 用途 | 配置位置 |
|----------|------|----------|
| JS API Key | 前端地图展示 | AMapLoader.load() 的 key 参数 |
| 安全密钥 | 配合 JS API Key 使用 | window._AMapSecurityConfig |
| Web 服务 Key | 后端调用路径规划等 API | 后端环境变量 |

---

### 前端相关

#### Q9: 前端启动报端口占用
**解决方案**：修改 `vite.config.js` 中的端口：
```javascript
server: {
  port: 3001  // 改为其他端口
}
```

#### Q10: 行程生成超时
**可能原因**：
1. DeepSeek API Key 未配置或无效
2. 网络无法访问 AI API
3. POI 数据不足

**排查步骤**：
1. 检查后端日志中的 API 调用错误
2. 确认 `DEEPSEEK_API_KEY` 环境变量已设置
3. 确认数据库中有足够的 POI 数据

---

## 版本日志

所有版本更新记录请查看 `docs/changelog/` 目录：

| 版本 | 日期 | 主要内容 |
|------|------|----------|
| v1.0.0 | 2026-01-28 | MVP 版本发布 |
| v1.0.1 | 2026-01-28 | 修复管理员登录、数据库连接等问题 |
| v1.0.2 | 2026-01-28 | 修复地图坐标类型问题 |
| v1.0.3 | 2026-01-28 | 改用 AMapLoader 并配置安全密钥 |

---

## 联系方式

如有问题，请通过以下方式反馈：
- 提交 GitHub Issue
- 发送邮件至项目负责人
