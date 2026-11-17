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

# shellcheck source=../../scripts/lib/docker.sh
source "$ROOT/scripts/lib/docker.sh"
if docker ps --format '{{.Names}}' 2>/dev/null | grep -qx 'ayanlab-postgres'; then
  echo "Waiting for ayanlab-postgres (init/recovery can take ~30s after down -v)..."
  ready=0
  for _ in $(seq 1 45); do
    if docker exec ayanlab-postgres pg_isready -U "${POSTGRES_SUPERUSER:-ayanlab_super}" -d "${POSTGIS_DB}" -q 2>/dev/null; then
      ready=1
      break
    fi
    sleep 2
  done
  if [[ "$ready" -ne 1 ]]; then
    echo "Postgres not ready on ${POSTGIS_HOST:-localhost}:${POSTGIS_PORT:-5433} — check: docker logs ayanlab-postgres" >&2
    exit 1
  fi
fi

echo "Running ayanlab-platform-backend (Maven spring-boot:run)"
echo "  env file: ${ENV_FILE}"
echo "  database: ${POSTGIS_HOST:-?}:${POSTGIS_PORT:-5432}/${POSTGIS_DB:-?} schema=${POSTGIS_SCHEMA:-?}"
echo "  API:      http://localhost:${SERVER_PORT:-8080}"
exec mvn -s "$ROOT/.mvn/settings-central.xml" spring-boot:run "$@"