# Weblog Docker 化 + Jenkins CI/CD 改造 PRD（简单版）

## 1. 项目信息

- **Language**：中文
- **Project Name**：weblog
- **技术栈**：Spring Boot 2.7.18（Java 8）+ Vue3/Vite5/Element Plus + MySQL 8 + Minio + Nginx + Jenkins
- **部署目标**：腾讯云轻量服务器 `62.234.30.116`（Ubuntu 24.04，2 核 4G）
- **代码仓库**：https://github.com/yuhao-Gits/weblog.git
- **原始需求复述**：将现有「前后端分离博客 Weblog」从传统部署（JDK + MySQL + Caddy）改造为 Docker 一键编排部署，并接入 Jenkins 完成 CI/CD 自动化（构建 → 测试 → 推送镜像 → 部署），同时补齐 Minio 图床、Nginx 反代、HTTPS/SSL、Minio 子域名访问。

> ⚠️ 现状勘察结论（基于仓库实际代码）：
> - 仓库 `deploy/` 已存在部分 Docker 化产物：`docker-compose.yml`（mysql/backend/frontend 三服务）、`backend.Dockerfile`、`frontend.Dockerfile`、`nginx.conf`、`.env.example`。
> - **尚未覆盖**：Minio（compose 无此服务，后端 pom 无 minio/s3 依赖，上传走本地 `weblog.upload.path`）、HTTPS/SSL（Nginx 仅 80，`server_name _`）、WebSocket 代理（nginx.conf 未配置）、Jenkins 流水线（无 Jenkinsfile）。
> - 已有 `application.yml` + `application-prod.yml` 与 `.github/workflows/ci.yml`（GitHub Actions CI）。
> - **关键口径冲突**：团队要求按 JDK17，但 `pom.xml` / `backend.Dockerfile` / `ci.yml` 全部为 **Java 8**，需先确认以哪个为准。

---

## 2. 产品定义

### 2.1 产品目标（3 个正交目标）

1. **一键容器化交付**：后端、前端、MySQL、Minio、Nginx 全部容器化，`docker compose up -d` 一条命令完成拉起与更新，彻底替代手工 JDK/Caddy 部署。
2. **CI/CD 全自动**：Jenkins 流水线实现「代码提交 → 构建 → 测试 → 推送镜像 → 自动部署」，缩短发布周期、消除手工误操作。
3. **生产级安全与可达**：主域名 + Minio 子域名 HTTPS 全覆盖；数据库、JWT、Minio 等凭据环境变量化；数据卷持久化，容器重建不丢数据。

### 2.2 用户故事

1. 作为运维人员，我希望一条 `docker compose up -d` 就能拉起整套博客，不必手工安装 JDK/MySQL/Caddy。
2. 作为开发者，我希望 push 代码后 Jenkins 自动完成构建、测试、镜像推送与部署，无需登录服务器手工操作。
3. 作为博主，我希望博客通过 HTTPS 访问，图片通过 `https://img.<域名>` 子域名加载，兼顾体验与安全。
4. 作为运维人员，我希望 MySQL 数据、上传文件、日志持久化在宿主机卷中，容器重建不丢数据。
5. 作为读者，我希望前端路由刷新不 404、API 与聊天 WebSocket 均能正常访问。

---

## 3. 技术规范

### 3.1 需求池（优先级）

#### P0（Must have，必须）
- **P0-1 容器编排覆盖 5 类服务**：`mysql`、`minio`、`backend`、`frontend`（Vue + 内置 Nginx 作为统一入口）、`nginx`（Minio 子域名独立反代，或与 frontend 复用网关）。基于现状推荐：`frontend` 容器内 Nginx 承载主域名反代 + 静态资源；Minio 子域名用独立 Nginx server 块或独立 nginx 容器。
- **P0-2 后端 prod 配置与 profile 激活**：注入 `SPRING_PROFILES_ACTIVE=prod`；数据源/JWT/上传路径全部环境变量化；明确建表策略（首次 `always` 幂等建表，后续可 `never`，避免误删数据）。
- **P0-3 Jenkins 流水线（Jenkinsfile）**：checkout → 后端 `mvn clean package` + 单测 → 前端 `npm ci` + lint + build → 分别 `docker build` → 推送到镜像仓库 → SSH 到目标服务器 `docker compose pull && up -d`（滚动更新）。
- **P0-4 HTTPS/SSL**：主域名 Nginx 监听 443，挂载证书，80 强制跳转 443。
- **P0-5 数据持久化**：`mysql-data`、`minio-data`、`uploads`、`logs` 卷映射到宿主机（建议 `/data/weblog/...`）。
- **P0-6 凭据安全**：`.env` 注入 `MYSQL_ROOT_PASSWORD`、`WEBLOG_JWT_SECRET`、Minio 账号密码；Jenkins 用 Credentials 存 SSH 私钥与镜像仓库口令，禁止硬编码。

#### P1（Should have，应该）
- **P1-1 Minio 接入后端存储层**：引入 minio SDK，抽象文件存储（上传/删除/访问 URL），图片与附件改走 Minio（需新增代码，当前后端无此依赖）。
- **P1-2 Minio 子域名 + SSL 反代**：`img.<域名>` → `minio:9000`，独立证书/泛域名证书。
- **P1-3 WebSocket 代理**：nginx.conf 增加 `/chat/ws` 升级头代理（`Upgrade`/`Connection: upgrade`）。
- **P1-4 端口收敛**：MySQL 3306、Minio 9000 不对外映射（仅 compose 内网），降低暴露面。
- **P1-5 回滚能力**：镜像带版本 tag（如 `:build-${BUILD_NUMBER}`），保留上一版本，部署失败可一键回滚。

#### P2（Nice to have，可选）
- 部署结果邮件/企微通知；构建失败即时告警。
- 日志集中（docker logs + 宿主机卷 + logrotate 轮转）。
- 定时备份：`mysqldump` + `mc mirror`，保留最近 N 天。
- 2 核 4G 下的内存/CPU 资源限制（`mem_limit`/`cpus`），避免 MySQL 与 Jenkins 争抢。
- 前端静态资源 CDN 加速。

### 3.2 部署架构设计（UI/架构草稿）

```
                    用户浏览器
        ┌──────────────┼──────────────┐
    HTTPS 443                     HTTPS 443
   主域名 blog.<域名>              img.<域名>
        ▼                            ▼
  Nginx（frontend 容器内）      Nginx（Minio 反代）
   ├─ /          → 前端 dist        │
   ├─ /api/*     → backend:8080     ▼
   ├─ /files/*   → backend/Minio   minio:9000
   └─ /chat/ws   → backend (WS)
        │
        ▼
   backend:8080 ──TCP(内网)──► mysql:3306
                ──TCP(内网)──► minio:9000
```

**端口 / 域名规划**

| 服务 | 容器名 | 容器内端口 | 对外映射 | 域名/访问 |
|---|---|---|---|---|
| MySQL | weblog-mysql | 3306 | 不映射（仅内网） | — |
| Minio | weblog-minio | 9000 / 9001 | 不映射（仅内网） | img.<域名> 经 Nginx |
| 后端 | weblog-backend | 8080 | 不直接对外 | /api、/files |
| 前端+反代 | weblog-frontend | 80 / 443 | 80 / 443 | blog.<域名> |
| Minio 反代 | nginx 或复用 server 块 | 443 | 443 | img.<域名> |

**数据卷规划**（宿主机 `/data/weblog/` 下）

| 卷 | 挂载 | 用途 |
|---|---|---|
| mysql-data | /var/lib/mysql | 数据库持久化 |
| minio-data | /data | 对象存储持久化 |
| upload-data | /app/uploads | 本地上传兜底/迁移过渡 |
| logs | /app/logs | 后端日志 |

**Jenkins 流水线阶段**

| 阶段 | 内容 | 产物 |
|---|---|---|
| 1 Checkout | 拉取 main 分支 | 源码 |
| 2 Backend Build/Test | `mvn clean package` + 单测 | jar + 测试报告 |
| 3 Frontend Build/Lint | `npm ci` + lint + build | dist |
| 4 Docker Build | 构建 backend/frontend 两镜像 | `weblog-server:build-N`、`weblog-web:build-N` |
| 5 Push Image | 推送到镜像仓库 | 仓库内可拉取镜像 |
| 6 Deploy | SSH 到服务器，`docker compose pull && up -d` + 健康检查 | 更新后的运行服务 |

### 3.3 生产配置改动清单

1. **后端**：`docker-compose` 注入 `SPRING_PROFILES_ACTIVE=prod`；`application-prod.yml` 中数据源/JWT 走环境变量；上传存储由本地 `weblog.upload.path` 切换为 Minio（需新增存储抽象层与 minio 依赖）。
2. **前端**：`vite.config.js` 已用相对路径 `/api`、`/files`，Nginx 反代即可，生产 baseURL 无需改动（需确认无硬编码 IP）。
3. **Nginx**：`deploy/nginx.conf` 补齐 `server_name`（真实域名）、443 + SSL、80→443 跳转、`/chat/ws` WS 代理、`/files` 指向 Minio。
4. **docker-compose**：在现有 3 服务基础上新增 `minio`、Minio 反代配置、`restart: always/unless-stopped`、健康检查、资源限制。
5. **CI/CD**：新增 `Jenkinsfile`（含镜像 tag 策略、SSH 部署、健康校验、失败回滚）；决定是否保留/停用现有 `.github/workflows/ci.yml`。

---

## 4. 待确认问题（Open Questions）

1. **域名**：主域名、Minio 子域名分别是什么？`62.234.30.116` 是否已绑定域名/备案？
2. **SSL 证书来源**：Let's Encrypt（certbot 自动续期）还是已有证书文件？验证方式 HTTP-01 还是 DNS-01？
3. **镜像仓库**：Docker Hub / 阿里云 ACR / 腾讯云 TCR / 自建 Harbor？是否有私有仓库凭据？
4. **Jenkins 部署位置**：与博客同机（2 核 4G 需评估内存，MySQL+Minio+Jenkins 可能吃紧）还是单独节点？Jenkins 通过什么方式访问目标服务器（SSH 凭据 / 插件）？
5. **JDK 版本**（关键）：团队口径为 JDK17，但代码/Dockerfile/CI 均为 **Java 8**，以哪个为准？
6. **Minio 集成范围**：仅做图片图床，还是所有文件上传（附件/封面）？后端当前无 Minio 依赖，需开发存储抽象层，是否纳入本次改造范围？
7. **WebSocket 聊天室**：`/chat/ws` 端点是否已实现？（当前 src 中未检索到实现，nginx.conf 也未配 WS 代理）是否需补齐？
8. **存量数据迁移**：现传统部署在 `62.234.30.116` 的 MySQL 数据与上传文件，是否迁移进容器新卷？旧 Caddy 是否下线/停用？
9. **MySQL 凭据**：沿用 `root/root` 还是生产换强密码（涉及 `.env` 与 Jenkins 凭据联动）？
10. **构建触发与分支**：Jenkins 触发 main push/PR，还是手动构建？现有 GitHub Actions `ci.yml` 保留还是停用？
