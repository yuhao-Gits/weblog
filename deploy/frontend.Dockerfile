# ============================================================
# Weblog 前端镜像（多阶段构建）
# 构建：docker build -f deploy/frontend.Dockerfile -t weblog-web .
# 说明：阶段一用 Node 构建静态产物，阶段二用 Nginx 托管并反代 API。
#       lint 由 Jenkinsfile 单独执行，镜像构建只做依赖安装 + 打包。
# ============================================================

# ---------- 阶段一：构建 ----------
FROM node:18-alpine AS builder
WORKDIR /build

# 先拷贝依赖清单，利用层缓存加速（npm ci 要求存在 package-lock.json）
COPY weblog-web/package.json weblog-web/package-lock.json* ./
RUN npm ci --registry=https://registry.npmmirror.com || npm ci

# 拷贝源码并构建（lint 默认关闭；Jenkinsfile 的“前端构建”阶段通过 RUN_LINT=true 开启）
COPY weblog-web/ ./
ARG RUN_LINT=false
RUN if [ "${RUN_LINT}" = "true" ]; then npm run lint; fi \
    && npm run build

# ---------- 阶段二：运行 ----------
FROM nginx:1.25-alpine
LABEL maintainer="weblog" \
      description="Weblog frontend (Vue 3 + Nginx)"

# 覆盖 Nginx 默认配置（含 /api、/api/chat/ws、/files、/minio 反代）
COPY deploy/nginx.conf /etc/nginx/conf.d/default.conf

# 拷贝构建产物到 Nginx 静态目录
COPY --from=builder /build/dist /usr/share/nginx/html

EXPOSE 80

# Nginx 前台运行
CMD ["nginx", "-g", "daemon off;"]
