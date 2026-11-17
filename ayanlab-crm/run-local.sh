#!/usr/bin/env bash
set -euo pipefail
HERE="$(cd "$(dirname "$0")" && pwd)"
ROOT="$(cd "$HERE/.." && pwd)"
export ENV="${ENV:-local}"

# shellcheck source=scripts/lib/resolve-env-file.sh
source "$ROOT/scripts/lib/resolve-env-file.sh"
# shellcheck source=scripts/lib/mvn-install-libs.sh
source "$ROOT/scripts/lib/mvn-install-libs.sh"

mvn_install_libs "$ROOT"
cd "$HERE/ayanlab-crm-backend"
load_env_file "$ROOT"

# shellcheck source=scripts/lib/map-db-env.sh
source "$ROOT/scripts/lib/map-db-env.sh"
map_db_env CRM || exit 1

if [[ -z "${POSTGIS_URL:-}" && "${POSTGIS_HOST:-}" == "postgres" ]]; then
  export POSTGIS_HOST=localhost
fi

echo "Running Maven spring-boot:run"
echo "  env file: ${ENV_FILE}"
echo "  database: ${POSTGIS_HOST:-?}/${POSTGIS_DB:-?} schema=${POSTGIS_SCHEMA:-?}"
exec mvn -s "$ROOT/.mvn/settings-central.xml" spring-boot:run "$@"
