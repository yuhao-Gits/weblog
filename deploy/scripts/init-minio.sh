#!/usr/bin/env bash
# ============================================================
# init-minio.sh —— 手动初始化 Minio（建桶 + 匿名下载策略）
# 等价于 docker-compose 中的一次性服务 minio-init。
# 用法：./deploy/scripts/init-minio.sh
# 依赖：宿主机已安装 Docker Compose v2，且 minio 服务已在 compose 中启动。
# ============================================================
set -euo pipefail

# 脚本目录 = deploy/scripts/，compose 目录 = deploy/
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

cd "${COMPOSE_DIR}"

echo ">> 复用 compose 中的 minio-init 服务进行初始化（幂等）"
echo ">> compose 目录：${COMPOSE_DIR}"

# --exit-code-from：等待 minio-init 运行结束并以其退出码作为本脚本结果
# 前置依赖 minio（service_started）会由 compose 自动拉起
docker compose up --exit-code-from minio-init minio-init

echo ">> Minio 初始化完成：桶 ${MINIO_BUCKET:-weblog} 已就绪，并设置匿名只读下载策略"
