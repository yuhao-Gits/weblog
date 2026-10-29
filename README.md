# Weblog 前后端分离博客系统

> 基于 **Spring Boot 2.7 + Vue 3** 的前后端分离个人博客系统，涵盖博客前台、管理后台、站内搜索、评论、知识库、聊天室、滑块验证码等完整功能。

## ✨ 功能特性

### 1.0 版本
- **登录鉴权**：Spring Security + JWT，支持管理员（admin）与游客（test）两种角色
- **管理后台**：文章、分类、标签、评论、知识库、博客设置、仪表盘全模块
- **博客前台**：首页、文章详情、归档、分类列表、标签列表、404 页面
- **仪表盘**：文章/分类/标签/访问量统计、发布热点柱状图、PV/UV 折线图
- **文章发布**：Markdown 编辑器、分类标签关联、文章置顶、上下篇导航

### 2.0 版本
- **站内搜索**：Lucene + SmartChineseAnalyzer 中文分词检索，关键词高亮
- **评论模块**：树形评论、QQ 头像、敏感词检测、审核开关、邮件通知
- **知识库**：目录树 + 文章，前后台管理，上下篇导航
- **聊天室**：WebSocket 双向通信，历史消息游标分页，在线用户列表
- **滑块验证码**：纯 Java AWT 自实现，防重放机制
- **主题切换**：白天/黑夜主题一键切换
- **UI 重设计**：现代极简 + 玻璃拟态质感，前后台风格统一

## 🛠 技术栈

| 端 | 技术 |
|----|------|
| 后端 | Spring Boot 2.7.18 · MyBatis-Plus 3.5.5 · Spring Security · JWT · MySQL 8 · Lucene 8.11 · WebSocket |
| 前端 | Vue 3 · Vite 5 · Element Plus · Pinia · Tailwind CSS · ECharts · md-editor-v3 |
| 部署 | Docker · Nginx · Jenkins CI/CD |

## 📂 项目结构

```
weblog/
├── weblog-server/          # 后端（Spring Boot）
│   └── src/main/java/com/yuhao/weblog/
│       ├── config/         # 配置类（Jackson/MyBatis/线程池等）
│       ├── framework/      # 安全框架（JWT/Security）
│       ├── common/         # 通用工具（Result/异常/Lucene/Markdown）
│       ├── module/         # 业务模块（admin/article/comment/...）
│       └── startup/        # 启动初始化
├── weblog-web/             # 前端（Vue 3 + Vite）
│   └── src/
│       ├── views/          # 页面（front 前台 / admin 后台）
│       ├── components/     # 组件
│       ├── api/            # 接口封装
│       ├── stores/         # Pinia 状态
│       └── router/         # 路由
├── docs/                   # 项目文档
├── test-1.0.py             # 回归测试脚本（46 项）
└── *.sh / *.bat            # 启动/构建脚本
```

## 🚀 快速开始

### 环境要求
- JDK 1.8+
- Maven 3.6+
- Node.js 16+
- MySQL 8（本机 root/root，库 `weblog`）

### 1. 初始化数据库
后端启动时会自动执行 `schema.sql`（建表）与 `data.sql`（种子数据），无需手动建表。确保本机 MySQL 存在 `weblog` 数据库即可：

```sql
CREATE DATABASE IF NOT EXISTS weblog DEFAULT CHARSET utf8mb4;
```

### 2. 启动后端
```bash
# Linux / macOS
bash start-backend.sh

# Windows
start-backend.bat
```
后端默认运行于 `http://localhost:8080`，Swagger 接口文档：`http://localhost:8080/swagger-ui.html`

### 3. 启动前端
```bash
# Linux / macOS
bash start-frontend.sh

# Windows
start-frontend.bat
```
前端默认运行于 `http://localhost:5173`，开发环境已配置代理转发到后端。

### 4. 登录账号
| 账号 | 密码 | 角色 | 权限 |
|------|------|------|------|
| admin | 123456 | 管理员 | 全部读写 |
| test | test | 游客 | 仅查询 |

## 📖 文档导航

| 文档 | 说明 |
|------|------|
| [需求文档](docs/01-需求文档.md) | 功能需求清单与角色权限 |
| [设计文档](docs/02-设计文档.md) | 系统架构、数据库设计、关键技术方案 |
| [开发文档](docs/03-开发文档.md) | 环境搭建、开发规范、接口清单 |
| [运维部署文档](docs/04-运维部署文档.md) | 生产部署、Docker、CI/CD |

## 🧪 测试

```bash
python test-1.0.py   # 全量接口回归测试（46 项）
```

## 📄 许可证

本项目仅用于学习与演示。
