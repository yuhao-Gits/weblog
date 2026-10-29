@echo off
REM ============================================================
REM Weblog 前端构建脚本（Windows 生产打包）
REM 依赖：Node.js 16+、npm
REM ============================================================
chcp 65001 >nul

echo [INFO] 开始构建前端...
cd weblog-web
call npm install
call npm run build
cd ..

echo [OK] 前端构建完成，产物：weblog-web\dist\
