@echo off
chcp 65001 >nul
title 视光管理系统

echo ========================================
echo         视光管理系统启动中...
echo ========================================
echo.

:: 检查 MySQL 服务
echo [1/3] 检查 MySQL 服务...
sc query MySQL80 >nul 2>&1
if errorlevel 1 (
    sc query MySQL >nul 2>&1
    if errorlevel 1 (
        echo 提示：MySQL 服务未运行，请手动启动
    ) else (
        net start MySQL >nul 2>&1
        echo MySQL 服务已启动
    )
) else (
    net start MySQL80 >nul 2>&1
    echo MySQL 服务已启动
)

echo.
echo [2/3] 正在启动视光管理系统...
echo 访问地址: http://localhost:8080/vision
echo 登录账号: admin / 123456
echo.

:: 进入项目目录
cd /d "%~dp0"

:: 启动 Spring Boot
echo [3/3] Spring Boot 启动中...
echo ========================================
echo.
call mvn spring-boot:run

pause