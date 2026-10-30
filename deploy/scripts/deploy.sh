#!/usr/bin/env bash
# ============================================================
# deploy.sh —— 服务端滚动部署（拉镜像 → 更新 → 健康检查 → 失败回滚）
# 用法：
#   IMAGE_TAG=build-42 ./deploy/scripts/deploy.sh   # 部署指定 tag
#   ./deploy/scripts/deploy.sh                      # 部署 latest
# 流程：
#   1) 记录当前成功 tag 作为回滚基线（.last-good-tag）
#   2) docker compose pull backend frontend
#   3) docker compose up -d --no-build backend frontend
#   4) smoke-test.sh 健康检查
#   5) 失败 -> rollback.sh 回滚并退出非 0；成功 -> 记录新 tag
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
LAST_GOOD_TAG_FILE="${SCRIPT_DIR}/.last-good-tag"
SMOKE_TEST="${SCRIPT_DIR}/smoke-test.sh"
ROLLBACK="${SCRIPT_DIR}/rollback.sh"

cd "${COMPOSE_DIR}"

IMAGE_TAG="${IMAGE_TAG:-latest}"

# 回滚基线：当前记录的成功 tag，无记录则 latest
if [[ -f "${LAST_GOOD_TAG_FILE}" ]]; then
  LAST_GOOD_TAG="$(cat "${LAST_GOOD_TAG_FILE}")"
else
  LAST_GOOD_TAG="latest"
fi

echo ">> 开始部署 tag=${IMAGE_TAG}（回滚基线=${LAST_GOOD_TAG}）"
export IMAGE_TAG

echo ">> 拉取镜像 ..."
docker compose pull backend frontend

echo ">> 滚动更新 backend/frontend（不重新构建，--no-deps 不触碰 mysql/minio/jenkins）..."
docker compose up -d --no-build --no-deps backend frontend

echo ">> 等待服务稳定后执行健康检查 ..."
# 给容器启动留出缓冲（后端可能需数秒加载 Spring 上下文）
sleep 5

if bash "${SMOKE_TEST}"; then
  echo "${IMAGE_TAG}" > "${LAST_GOOD_TAG_FILE}"
  echo ">> 部署成功并已记录：${IMAGE_TAG}"
  exit 0
else
  echo "!! 健康检查失败，触发回滚到 ${LAST_GOOD_TAG} ..."
  if IMAGE_TAG="${LAST_GOOD_TAG}" bash "${ROLLBACK}"; then
    echo "!! 已回滚到 ${LAST_GOOD_TAG}，本次部署失败"
  else
    echo "!! 回滚失败，请立即人工介入" >&2
  fi
  exit 1
fi
