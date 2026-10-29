@echo off
REM ============================================================
REM Weblog 后端构建脚本（Windows）
REM 依赖：Maven 3.6+、JDK 1.8
REM ============================================================
chcp 65001 >nul

echo [INFO] 开始构建后端...
cd weblog-server
call mvn clean package -DskipTests
cd ..

echo [OK] 后端构建完成，产物：weblog-server\target\weblog-server-1.0.0.jar
