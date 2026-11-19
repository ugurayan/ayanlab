#!/usr/bin/env bash
set -euo pipefail
HERE="$(cd "$(dirname "$0")" && pwd)"
ROOT="$(cd "$HERE/.." && pwd)"
export ENV="${ENV:-local}"

# shellcheck source=scripts/lib/resolve-env-file.sh
source "$ROOT/scripts/lib/resolve-env-file.sh"
# shellcheck source=scripts/lib/mvn-install-libs.sh
source "$ROOT/scripts/lib/mvn-install-libs.sh"

# Mobile (Flutter): ./run-local.sh mobile [extra flutter run args]
# Device override: FLUTTER_DEVICE=ios ./run-local.sh mobile
run_mobile() {
  local mobile_dir="$HERE/ayanlab-comm-mobile"
  if ! command -v flutter >/dev/null 2>&1; then
    echo "flutter not found — Flutter SDK gerekli" >&2
    exit 1
  fi
  echo "Running flutter (${FLUTTER_DEVICE:-macos}): $mobile_dir"
  cd "$mobile_dir"
  flutter pub get
  exec flutter run -d "${FLUTTER_DEVICE:-macos}" "$@"
}

if [[ "${1:-}" == "mobile" ]]; then
  shift
  run_mobile "$@"
fi


mvn_install_libs "$ROOT"
cd "$HERE/ayanlab-comm-backend"
load_env_file "$ROOT"

# shellcheck source=scripts/lib/map-db-env.sh
source "$ROOT/scripts/lib/map-db-env.sh"
map_db_env COMM || exit 1

if [[ -z "${POSTGIS_URL:-}" && "${POSTGIS_HOST:-}" == "postgres" ]]; then
  export POSTGIS_HOST=localhost
fi

echo "Running Maven spring-boot:run"
echo "  env file: ${ENV_FILE}"
echo "  database: ${POSTGIS_HOST:-?}/${POSTGIS_DB:-?} schema=${POSTGIS_SCHEMA:-?}"
exec mvn -s "$ROOT/.mvn/settings-central.xml" spring-boot:run "$@"
