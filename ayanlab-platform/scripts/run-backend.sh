#!/usr/bin/env bash
# Platform Spring Boot (maven spring-boot:run)
set -euo pipefail
PLATFORM_DIR="$(cd "$(dirname "$0")/.." && pwd)"
ROOT="$(cd "$PLATFORM_DIR/.." && pwd)"
if [[ -z "${ENV_FILE:-}" ]]; then
  export ENV="${ENV:-local}"
fi

# shellcheck source=../../scripts/lib/resolve-env-file.sh
source "$ROOT/scripts/lib/resolve-env-file.sh"
# shellcheck source=../../scripts/lib/mvn-install-libs.sh
source "$ROOT/scripts/lib/mvn-install-libs.sh"

mvn_install_libs "$ROOT"
cd "$PLATFORM_DIR/ayanlab-platform-backend"
load_env_file "$ROOT"

# shellcheck source=../../scripts/lib/map-db-env.sh
source "$ROOT/scripts/lib/map-db-env.sh"
map_db_env PLATFORM || exit 1

# Compose service name on the JVM host → use POSTGIS_HOST from .env (often localhost).
if [[ -z "${POSTGIS_URL:-}" && "${POSTGIS_HOST:-}" == "postgres" ]]; then
  export POSTGIS_HOST=localhost
fi

echo "Running ayanlab-platform-backend (Maven spring-boot:run)"
echo "  env file: ${ENV_FILE}"
echo "  database: ${POSTGIS_HOST:-?}:${POSTGIS_PORT:-5432}/${POSTGIS_DB:-?} schema=${POSTGIS_SCHEMA:-?}"
export SERVER_PORT="${SERVER_PORT:-8102}"
echo "  API:      http://localhost:${SERVER_PORT}"
exec mvn -s "$ROOT/.mvn/settings-central.xml" spring-boot:run \
  -Dspring-boot.run.arguments="--server.port=${SERVER_PORT}" "$@"