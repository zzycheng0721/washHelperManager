@echo off
setlocal
REM One-click start for WashHelper full stack (MariaDB + Redis + Backend).
REM Pass --dev to also bring up the frontend webpack-dev-server.

cd /d "%~dp0"

if not exist .env (
    if exist .env.example (
        copy /Y .env.example .env > nul
        echo [washhelper] .env created from .env.example
    )
)

if "%1"=="--dev" (
    docker compose --profile dev up -d --build
) else (
    docker compose up -d --build
)

echo.
echo Backend  : http://localhost:8099/admin/        (Vue SPA served by Spring Boot)
echo Swagger  : http://localhost:8099/swagger-ui.html
if "%1"=="--dev" (
    echo Frontend : http://localhost:9528/admin/        (webpack-dev-server, hot reload)
)
echo.
echo Tail logs : docker compose logs -f backend
echo Stop      : docker compose down
endlocal