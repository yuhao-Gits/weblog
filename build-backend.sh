#!/usr/bin/env bash
# ============================================================
# Weblog 后端构建脚本
# 依赖：Maven 3.6+、JDK 1.8
# 用法：bash build-backend.sh [-o 离线模式]
# ============================================================
set -e

MVN="${MVN_HOME:-mvn}"

echo "[INFO] 开始构建后端..."
if [ "$1" = "-o" ]; then
  echo "[INFO] 使用离线模式（-o）"
  (cd weblog-server && "$MVN" -o clean package -DskipTests)
else
  (cd weblog-server && "$MVN" clean package -DskipTests)
fi

echo "[OK] 后端构建完成，产物：weblog-server/target/weblog-server-1.0.0.jar"
