# peach-auth-service

Peach 体系中的**认证服务**：提供自定义登录 API（如用户名密码、API Key 等）并签发 JWT，与 `peach-gateway`、业务微服务解耦。

## 工程说明

独立 Spring Boot 应用，依赖 **`peach-common-start`** 获得统一 MVC、MyBatis、`ApiResult`、全局异常等能力；自身增加 **JJWT**、**jBCrypt**、**Nacos Discovery**、**PostgreSQL** 驱动。

## 功能说明

- 用户名密码登录（滑块、RSA 解密口令、BCrypt 验密等以 `UserServiceImpl` 为准）。
- API Key 登录：实现并注册 **`PeachApiKeyAuthenticator`** Bean。
- 签发 **HS256 JWT**：`JwtUtil` 将用户 VO 序列化为 **`sub`** 载荷；与网关 **`TokenGlobalFilter`** 解析约定一致。

## 技术栈

- JDK 21
- Spring Boot 4.0.x（与 `peach-dependencies` BOM 对齐）
- **jBCrypt**（`org.mindrot:jbcrypt`）：口令验密，与库内既有 BCrypt 摘要兼容；业务代码不依赖 `spring-boot-starter-security` / `AuthenticationManager`
- **JJWT**（`io.jsonwebtoken:jjwt-*`）：JWT 签发与 refresh 解析（HS256，与网关验签密钥一致；无 OAuth2 依赖）
- `peach-common-start`：统一 MVC / MyBatis / `ApiResult` 等

## 工程坐标

| 项 | 值 |
| --- | --- |
| `groupId` | `org.peach.auth` |
| `artifactId` | `peach-auth-service` |
| `spring.application.name` | `peach-auth-service` |
| 启动类 | `org.peach.auth.AuthApp` |

## 主要接口

- `POST /auth/login/password`：用户名密码登录
- `POST /auth/login/api-key`：API Key 登录（需实现 `PeachApiKeyAuthenticator` Bean）

## 配置说明

见 `src/main/resources/application.yml`：

- **数据源**：PostgreSQL，连接业务库（用户表等）；可用环境变量 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD` 覆盖。
- **`peach.jwt.expires-in`**：访问令牌有效期（秒），默认 `3600`；可用环境变量 **`PEACH_JWT_EXPIRES_IN`** 覆盖。
- **Nacos**：`NACOS_SERVER_ADDR`、`NACOS_USERNAME`、`NACOS_PASSWORD`、`NACOS_NAMESPACE`、`NACOS_GROUP` 等与 sibling 服务一致。
- **`spring.application.module-code`**：当前为 **`AUTH`**（与 `ApiResult` 业务码前缀相关）。

### JWT 秘钥（与网关一致）

当前 **`JwtUtil.DEFAULT_HMAC_SECRET`** 与网关 **`TokenGlobalFilter`** 内常量**同源默认值**（便于本地联调）。生产环境应抽取为**统一配置或 KMS**，并同时更新网关验签逻辑。

## 开发约定

- 认证逻辑避免强依赖 **Spring Security OAuth2**；口令使用 **jBCrypt**，与 `BCryptUtil` 工具类风格保持一致。
- 新增对外路径时，确认已被网关 **`TokenGlobalFilter`** 匿名白名单覆盖（登录、滑块、Swagger 等）。

## 本地运行

前置：本地已 `mvn install` 构建 `peach-dependencies`、`peach-common-start`。

```bash
mvn -f peach-auth-service/pom.xml spring-boot:run
```

默认端口：`8084`。

## 构建与测试

```bash
mvn -f peach-auth-service/pom.xml clean verify
```

## 远程仓库与双推送

已配置 `origin` 同时推送 **GitHub** 与 **Gitee**，一次推送即可同步：

```bash
git push -u origin main
```

（首次若远程已有提交，请先 `git pull origin main --allow-unrelated-histories` 再推送，或按空仓库流程只推送本地。）

## 作者

leiyangjun
