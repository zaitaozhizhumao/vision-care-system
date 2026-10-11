@echo off
chcp 65001 >nul
title 视光管理系统 - Java 25 启动器

setlocal enabledelayedexpansion

cd /d "%~dp0"

echo ========================================
echo       视光管理系统启动中...
echo ========================================
echo.

:: 1. 检查并配置 Java 25
echo [1/4] 检查 Java 25 环境...
set "JAVA_HOME="
for %%D in (
    "C:\Users\57761\.jdk\jdk-25.0.2"
    "C:\Program Files\Java\jdk-25"
    "C:\Program Files\Java\jdk-25.0.2"
    "C:\Program Files\Java\jdk-1.8"
) do (
    if exist "%%~D\bin\java.exe" (
        set "JAVA_HOME=%%~D"
        goto :java_found
    )
)

where java >nul 2>nul
if not errorlevel 1 (
    for /f "delims=" %%I in ('where java 2^>nul') do (
        set "JAVA_EXE=%%I"
    )
    if defined JAVA_EXE (
        for %%P in ("!JAVA_EXE!") do set "JAVA_HOME=%%~dpP"
        set "JAVA_HOME=!JAVA_HOME:~0,-1!"
    )
)

:java_found
if defined JAVA_HOME (
    echo 已检测到 JDK: !JAVA_HOME!
    set "PATH=!JAVA_HOME!\bin;%PATH%"
) else (
    echo 错误：未检测到 JDK 25。请先安装 JDK 25，或将 JAVA_HOME 配置到系统环境变量。
    echo 参考路径：C:\Users\57761\.jdk\jdk-25.0.2
    echo.
    pause
    exit /b 1
)

:: 2. 检查 MySQL 服务
echo.
echo [2/4] 检查 MySQL 服务...
sc query MySQL80 >nul 2>&1
if errorlevel 1 (
    sc query MySQL >nul 2>&1
    if errorlevel 1 (
        echo 提示：MySQL 服务未运行，程序可能无法启动。请手动启动 MySQL 后再运行。
    ) else (
        net start MySQL >nul 2>&1
        echo MySQL 服务已启动
    )
) else (
    net start MySQL80 >nul 2>&1
    echo MySQL 服务已启动
)

:: 3. 检查 Maven Wrapper
echo.
echo [3/4] 检查构建工具...
if exist "%~dp0mvnw.cmd" (
    set "MVN_CMD=%~dp0mvnw.cmd"
    echo 使用 Maven Wrapper: %MVN_CMD%
) else if exist "%~dp0mvnw" (
    set "MVN_CMD=%~dp0mvnw"
    echo 使用 Maven Wrapper: %MVN_CMD%
) else (
    where mvn >nul 2>nul
    if errorlevel 1 (
        echo 错误：未找到 Maven 或 Maven Wrapper，请确保 Maven 已安装或项目包含 mvnw。 
        pause
        exit /b 1
    )
    for /f "delims=" %%M in ('where mvn 2^>nul') do set "MVN_CMD=%%M"
    echo 使用 Maven: %MVN_CMD%
)

:: 4. 启动应用
echo.
echo [4/4] 启动 Spring Boot 应用...
echo 访问地址: http://localhost:8080/vision
echo 登录账号: admin / 123456
echo.
echo ========================================
echo.
call "%MVN_CMD%" spring-boot:run

if errorlevel 1 (
    echo.
    echo 启动失败，请查看上面的错误信息。
    echo 若为 Java 版本问题，请确认 JDK 25 已正确安装且 JAVA_HOME 已设置。
    echo.
    pause
    exit /b 1
)

pause