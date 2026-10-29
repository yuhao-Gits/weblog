@echo off
REM ============================================================
REM Weblog 后端启动脚本（Windows 开发环境）
REM 用法：start-backend.bat [端口]  默认 8080
REM ============================================================
chcp 65001 >nul
setlocal

set PORT=%1
if "%PORT%"=="" set PORT=8080

set JAR=weblog-server\target\weblog-server-1.0.0.jar

if not exist "%JAR%" (
    echo [INFO] 未找到 %JAR%，开始构建...
    call build-backend.bat
)

echo [INFO] 启动后端服务，端口：%PORT%
java -jar "%JAR%" --server.port=%PORT%

endlocal
