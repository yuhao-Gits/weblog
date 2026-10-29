#!/usr/bin/env bash
# ============================================================
# Weblog 前端启动脚本（开发环境）
# 依赖：Node.js 16+、npm
# 用法：bash start-frontend.sh
# ============================================================
set -e

echo "[INFO] 启动前端开发服务器（http://localhost:5173）..."
(cd weblog-web && npm install && npm run dev)
