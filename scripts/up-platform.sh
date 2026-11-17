#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
# shellcheck source=lib/resolve-env-file.sh
source "${ROOT}/scripts/lib/resolve-env-file.sh"
# shellcheck source=lib/docker.sh
source "${ROOT}/scripts/lib/docker.sh"
cd "${ROOT}"

ENV_FILE="$(resolve_env_file)" || exit 1
[[ -f "${ENV_FILE}" ]] || { echo "Missing ${ENV_FILE}" >&2; exit 1; }

export ENV
"${ROOT}/scripts/validate-env-local.sh" || exit 1

export ENV_FILE
docker compose --env-file "${ENV_FILE}" -f docker-compose.platform.yml up -d --build --remove-orphans "$@"
echo "Loaded env: ${ENV_FILE}"
"${ROOT}/scripts/ensure-db-schemas.sh"
docker restart ayanlab-platform-backend 2>/dev/null || true

echo ""
echo "Platform API:  http://localhost:8102/auth/api/login  (auth)"
echo "               http://localhost:8102/core/api/...     (business)"
echo "Swagger:       http://localhost:8102/swagger-ui/index.html"
echo "UI:            http://localhost:3102"
echo "Postgres:      localhost:${AYANLAB_POSTGRES_HOST_PORT:-5433}  db=${PLATFORM_DB_NAME:-ayanlab_platform}  schema=core"