# ============================================================
# Weblog 前端镜像（多阶段构建）
# 构建：docker build -f deploy/frontend.Dockerfile -t weblog-web .
# 说明：阶段一用 Node 构建静态产物，阶段二用 Nginx 托管并反代 API
# ============================================================

# ---------- 阶段一：构建 ----------
FROM node:18-alpine AS builder
WORKDIR /build

# 先拷贝依赖清单，利用层缓存加速
COPY weblog-web/package.json weblog-web/package-lock.json* ./
RUN npm install --registry=https://registry.npmmirror.com || npm install

# 拷贝源码并构建
COPY weblog-web/ ./
RUN npm run build

# ---------- 阶段二：运行 ----------
FROM nginx:1.25-alpine
LABEL maintainer="weblog" \
      description="Weblog frontend (Vue 3 + Nginx)"

# 覆盖 Nginx 默认配置（含 /api、/files 反代）
COPY deploy/nginx.conf /etc/nginx/conf.d/default.conf

# 拷贝构建产物到 Nginx 静态目录
COPY --from=builder /build/dist /usr/share/nginx/html

EXPOSE 80

# Nginx 前台运行
CMD ["nginx", "-g", "daemon off;"]
