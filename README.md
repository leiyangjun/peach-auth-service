# peach-auth-service

Peach 体系中的**认证服务**：提供自定义登录 API（如用户名密码、API Key 等）并签发 JWT，与 `peach-gateway`、业务微服务解耦。

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

- `peach.auth.jwt-secret`：HS256 密钥（**至少 32 字节**，生产请用环境变量注入，与网关 `peach.gateway.jwt.secret` 相同）
- 数据源：与业务库 `cmn_user` 一致，用于加载用户验密

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
