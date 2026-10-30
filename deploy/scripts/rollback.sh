#!/usr/bin/env bash
# ============================================================
# rollback.sh —— 回滚到上一成功（或指定）镜像 tag
# 用法：
#   ./deploy/scripts/rollback.sh               # 回滚到 .last-good-tag 记录的 tag
#   IMAGE_TAG=build-41 ./deploy/scripts/rollback.sh  # 回滚到指定 tag
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
LAST_GOOD_TAG_FILE="${SCRIPT_DIR}/.last-good-tag"

cd "${COMPOSE_DIR}"

# 目标 tag：优先取 IMAGE_TAG 环境变量，否则回退到上一成功记录，最后兜底 latest
if [[ -n "${IMAGE_TAG:-}" ]]; then
  TARGET_TAG="${IMAGE_TAG}"
elif [[ -f "${LAST_GOOD_TAG_FILE}" ]]; then
  TARGET_TAG="$(cat "${LAST_GOOD_TAG_FILE}")"
else
  TARGET_TAG="latest"
fi

echo ">> 回滚到 tag: ${TARGET_TAG}"
export IMAGE_TAG="${TARGET_TAG}"

# 拉取目标镜像并重建 backend/frontend（不触发源码重编译，--no-deps 不触碰基础设施）
docker compose pull backend frontend
docker compose up -d --no-build --no-deps backend frontend

# 回滚后复检
if bash "${SCRIPT_DIR}/smoke-test.sh"; then
  echo "${TARGET_TAG}" > "${LAST_GOOD_TAG_FILE}"
  echo ">> 回滚成功：${TARGET_TAG}"
else
  echo "!! 回滚后健康检查仍未通过，请人工介入排查" >&2
  exit 1
fi
