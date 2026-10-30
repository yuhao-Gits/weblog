#!/usr/bin/env bash
# ============================================================
# backup.sh —— 数据备份（MySQL mysqldump + Minio mc mirror）
# 用途：定时（cron）或手动备份数据库与对象存储。
# 用法：./deploy/scripts/backup.sh [保留天数]
#   - 参数1：备份保留天数，默认 7
# 输出：deploy/backups/<时间戳>/weblog.sql 与 minio/ 目录
# 建议 cron：0 2 * * * /opt/weblog/deploy/scripts/backup.sh 7 >> /var/log/weblog-backup.log 2>&1
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
cd "${COMPOSE_DIR}"

KEEP_DAYS="${1:-7}"
STAMP="$(date +%Y%m%d_%H%M%S)"
BACKUP_ROOT="${COMPOSE_DIR}/backups/${STAMP}"
SQL_FILE="${BACKUP_ROOT}/weblog.sql"
MINIO_DIR="${BACKUP_ROOT}/minio"

# 读取 .env（若存在）
if [[ -f "${COMPOSE_DIR}/.env" ]]; then
  # shellcheck disable=SC1091
  set -a && source "${COMPOSE_DIR}/.env" && set +a
fi
MYSQL_PASSWORD="${MYSQL_ROOT_PASSWORD:-root}"
MINIO_USER="${MINIO_ROOT_USER:-minioadmin}"
MINIO_PASSWORD="${MINIO_ROOT_PASSWORD:-minioadmin}"
MINIO_BUCKET="${MINIO_BUCKET:-weblog}"

mkdir -p "${BACKUP_ROOT}"

echo ">> 备份 MySQL -> ${SQL_FILE}"
# 导出兼容 mysqldump 的完整数据（含建表与数据，锁表保证一致性）
docker compose exec -T mysql \
  mysqldump -h localhost -uroot -p"${MYSQL_PASSWORD}" \
  --single-transaction --routines --triggers weblog > "${SQL_FILE}"

echo ">> 备份 Minio bucket=${MINIO_BUCKET} -> ${MINIO_DIR}"
# 使用 minio/mc 容器执行 mirror（宿主机无需预装 mc）；
# 网络直接复用 compose 定义的 weblog-net（已在 docker-compose.yml 中显式命名）
docker run --rm \
  --network weblog-net \
  -v "${MINIO_DIR}:/backup" \
  minio/mc:latest sh -c "
    mc alias set local http://minio:9000 '${MINIO_USER}' '${MINIO_PASSWORD}' &&
    mc mirror --overwrite 'local/${MINIO_BUCKET}' /backup
  "

echo ">> 清理 ${KEEP_DAYS} 天前的旧备份"
find "${COMPOSE_DIR}/backups" -maxdepth 1 -mindepth 1 -type d -mtime "+${KEEP_DAYS}" -exec rm -rf {} +

echo ">> 备份完成：${BACKUP_ROOT}"
