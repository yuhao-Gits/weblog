#!/usr/bin/env bash
# ============================================================
# Weblog 前端构建脚本（生产打包）
# 依赖：Node.js 16+、npm
# 用法：bash build-frontend.sh
# ============================================================
set -e

echo "[INFO] 开始构建前端..."
(cd weblog-web && npm install && npm run build)

echo "[OK] 前端构建完成，产物：weblog-web/dist/"
