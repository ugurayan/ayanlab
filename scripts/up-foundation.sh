#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "${ROOT}"

# shellcheck source=lib/resolve-env-file.sh
source "${ROOT}/scripts/lib/resolve-env-file.sh"
# shellcheck source=lib/docker.sh
source "${ROOT}/scripts/lib/docker.sh"
ENV_FILE="$(resolve_env_file)" || exit 1
[[ -f "${ENV_FILE}" ]] || { echo "Missing ${ENV_FILE}" >&2; exit 1; }
export ENV_FILE
docker compose --env-file "${ENV_FILE}" -f docker-compose.foundation.yml up -d --build "$@"
"${ROOT}/scripts/ensure-db-schemas.sh"
docker restart ayanlab-platform-backend 2>/dev/null || true
echo "Platform API: http://localhost:8102"
echo "Platform UI:  http://localhost:3102"
echo "Swagger:      http://localhost:8102/swagger-ui/index.html"