#!/usr/bin/env bash
set -euo pipefail
# One-click start for WashHelper full stack (MariaDB + Redis + Backend).
# Pass --dev to also bring up the frontend webpack-dev-server.

cd "$(dirname "$0")"

if [[ ! -f .env && -f .env.example ]]; then
    cp .env.example .env
    echo "[washhelper] .env created from .env.example"
fi

if [[ "${1:-}" == "--dev" ]]; then
    docker compose --profile dev up -d --build
else
    docker compose up -d --build
fi

cat <<EOF

Backend  : http://localhost:8099/admin/        (Vue SPA served by Spring Boot)
Swagger  : http://localhost:8099/swagger-ui.html
$( [[ "${1:-}" == "--dev" ]] && echo "Frontend : http://localhost:9528/admin/        (webpack-dev-server, hot reload)" )

Tail logs : docker compose logs -f backend
Stop      : docker compose down
EOF