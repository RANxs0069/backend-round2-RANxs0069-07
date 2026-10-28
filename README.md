# 第 7 题：真正的登录注册

用 Spring Boot + MySQL + MyBatis + JWT 实现注册、登录、密码散列、多算法切换和角色权限控制。

## 技术栈

- Java 17
- Spring Boot 4.1.2
- MySQL 8.4
- MyBatis 4.1.0
- JJWT 0.13.0
- spring-security-crypto 7.1.2（只做密码编码，不引入 Security 过滤器链）

## 功能

- 用户注册：用户名唯一、密码加密、默认角色 `USER`
- 用户登录：校验密码后签发 JWT
- 密码多算法：`bcrypt` / `pbkdf2`，另保留 `sha256` / `noop` 仅用于兼容演示
- JWT 鉴权：`Authorization: Bearer <token>`
- 角色权限：`/user/admin/**` 仅 `ADMIN` 可访问
- 普通用户只能查询自己的信息
- 滑动续期：剩余有效期小于 30 分钟时，响应头返回 `X-New-Token`
- 全局异常处理：参数校验、重复用户名、404/405、500 统一返回
- MyBatis `#{}` 参数绑定，防止 SQL 注入

## 目录结构

```text
src/main/java/org/example/backend2_07
├── Backend207Application.java
├── common          # 通用响应 Result
├── config          # SecurityConfig、WebConfig
├── controller      # UserController、request DTO
├── entity          # User 实体
├── exception       # GlobalExceptionHandler
├── mapper          # MyBatis UserMapper
├── security        # JwtUtil、JwtInterceptor
├── service         # UserService、impl、dto
└── vo              # UserVO
```

## 快速开始

### 1. 建库

```bash
mysql -h 127.0.0.1 -P 3307 -u <用户名> -p < sql/schema.sql
```

脚本会创建 `backend2_07` 库、`users` 表，并 seed 一个本地验收管理员：

- 用户名：`testAdmin`
- 密码：`admin123`

### 2. 配置环境变量

```bash
cp .env.example .env
```

填写 `.env`：

```properties
DB_USER=你的数据库用户名
DB_PASSWORD=你的数据库密码
JWT_SECRET=至少32字节的随机字符串
```

`.env` 已被 `.gitignore` 忽略，不会提交。

### 3. 启动

```bash
./mvnw spring-boot:run
```

默认端口 `8080`。

## 接口

| 方法 | 路径 | 权限 | 说明 |
|:--|:--|:--|:--|
| POST | `/user/register` | 公开 | 注册，返回 token |
| POST | `/user/login` | 公开 | 登录，返回 token |
| GET | `/user/{id}` | 登录用户 | 只能查自己 |
| GET | `/user/admin/all` | ADMIN | 查全部用户 |
| GET | `/user/admin/id/{id}` | ADMIN | 按 id 查用户 |
| GET | `/user/admin/name/{username}` | ADMIN | 按用户名查用户 |

请求示例：

```bash
curl -X POST http://localhost:8080/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testAdmin","password":"admin123"}'
```

受保护接口：

```bash
curl http://localhost:8080/user/admin/all \
  -H "Authorization: Bearer <token>"
```

## 密码算法切换

通过环境变量或配置切换新密码算法：

```bash
PASSWORD_ALGORITHM=pbkdf2 ./mvnw spring-boot:run
```

库里存的是 `{算法}散列`，切换后老用户仍然可以登录。

## 状态码

| 状态码 | 场景 |
|:--|:--|
| 200 | 成功 |
| 400 | 参数错误 / 用户名密码错误 |
| 401 | 未携带 token / token 无效 |
| 403 | 普通用户访问 admin / 查别人 |
| 404 | 用户或接口不存在 |
| 405 | 请求方法不支持 |
| 409 | 用户名重复（并发兜底） |
| 500 | 服务器内部错误 |

## 说明

- 没有单独的 `/user/refresh`，刷新在 `JwtInterceptor` 中按请求自动完成；客户端需要读取 `X-New-Token` 响应头。
- 接前端跨域时，需要放行 `OPTIONS` 预检，并暴露 `X-New-Token` 响应头。
- 单 token 只能在未过期时续期；超过有效期需要重新登录。
- 建表脚本里的 `testAdmin/admin123` 仅用于本地验收，真实环境不要保留默认密码。
