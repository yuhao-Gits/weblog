@echo off
REM ============================================================
REM Weblog 前端启动脚本（Windows 开发环境）
REM 依赖：Node.js 16+、npm
REM ============================================================
chcp 65001 >nul

echo [INFO] 启动前端开发服务器（http://localhost:5173）
cd weblog-web
call npm install
call npm run dev
cd ..
