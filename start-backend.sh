#!/usr/bin/env bash
# ============================================================
# Weblog 后端启动脚本（开发环境）
# 用法：bash start-backend.sh [端口]  默认 8080
# ============================================================
set -e

PORT="${1:-8080}"
JAR="weblog-server/target/weblog-server-1.0.0.jar"

# 若 jar 不存在，先尝试构建
if [ ! -f "$JAR" ]; then
  echo "[INFO] 未找到 $JAR，开始构建..."
  bash build-backend.sh
fi

echo "[INFO] 启动后端服务，端口：$PORT"
java -jar "$JAR" --server.port="$PORT"
