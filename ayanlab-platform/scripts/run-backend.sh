#!/usr/bin/env bash
# Platform Spring Boot (maven spring-boot:run)
set -euo pipefail
PLATFORM_DIR="$(cd "$(dirname "$0")/.." && pwd)"
ROOT="$(cd "$PLATFORM_DIR/.." && pwd)"
export ENV="${ENV:-local}"

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

if [[ -z "${POSTGIS_URL:-}" && "${POSTGIS_HOST:-}" == "postgres" ]]; then
  export POSTGIS_HOST=localhost
fi

echo "Running ayanlab-platform-backend (Maven spring-boot:run)"
echo "  env file: ${ENV_FILE}"
echo "  database: ${POSTGIS_HOST:-?}:${POSTGIS_PORT:-5432}/${POSTGIS_DB:-?} schema=${POSTGIS_SCHEMA:-?}"
echo "  API:      http://localhost:${SERVER_PORT:-8080}"
exec mvn -s "$ROOT/.mvn/settings-central.xml" spring-boot:run "$@"