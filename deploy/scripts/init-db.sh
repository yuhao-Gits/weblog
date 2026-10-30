#!/usr/bin/env bash
# ============================================================
# init-db.sh —— 等待 MySQL 健康并校验 weblog 库/表结构（幂等）
# 用途：手动排查或首次部署时确认数据库就绪。
# 用法：./deploy/scripts/init-db.sh
# 说明：后端启动时 Spring 会执行幂等 schema.sql/data.sql，
#       本脚本仅做「连通性 + 关键表存在性」校验，不负责建表。
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
cd "${COMPOSE_DIR}"

# 从 .env 读取密码（若存在），否则用默认值
if [[ -f "${COMPOSE_DIR}/.env" ]]; then
  # shellcheck disable=SC1091
  set -a && source "${COMPOSE_DIR}/.env" && set +a
fi
MYSQL_PASSWORD="${MYSQL_ROOT_PASSWORD:-root}"

# 需校验的关键业务表（与 schema.sql 对齐，选取代表性表即可）
TABLES=(t_user t_article t_category t_tag t_comment t_chat_message)

echo ">> 等待 MySQL 健康（mysqladmin ping）..."
for i in $(seq 1 30); do
  if docker compose exec -T mysql \
      mysqladmin ping -h localhost -uroot -p"${MYSQL_PASSWORD}" --silent >/dev/null 2>&1; then
    echo ">> MySQL 已就绪"
    break
  fi
  echo "   等待中（${i}/30）..."
  sleep 3
  if [[ ${i} -eq 30 ]]; then
    echo "!! MySQL 在 90s 内未就绪" >&2
    exit 1
  fi
done

echo ">> 校验数据库 weblog 与关键表 ..."
MISSING=0
for table in "${TABLES[@]}"; do
  count=$(docker compose exec -T mysql \
    mysql -h localhost -uroot -p"${MYSQL_PASSWORD}" -N -e \
    "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='weblog' AND table_name='${table}';" 2>/dev/null | tr -d '\r')
  if [[ "${count}" == "1" ]]; then
    echo "   [OK] ${table}"
  else
    echo "   [缺失] ${table}"
    MISSING=$((MISSING + 1))
  fi
done

if [[ ${MISSING} -gt 0 ]]; then
  echo "!! 存在 ${MISSING} 张表缺失。若为首次部署，请确认后端 SPRING_SQL_INIT_MODE=always 并已成功启动一次。" >&2
  exit 1
fi

echo ">> 数据库校验通过"
