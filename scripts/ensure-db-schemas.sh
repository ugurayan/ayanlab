#!/usr/bin/env bash
# Create PostgreSQL schemas on existing volumes (init scripts only run on empty data dir).
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
# shellcheck source=lib/resolve-env-file.sh
source "${ROOT}/scripts/lib/resolve-env-file.sh"
# shellcheck source=lib/docker.sh
source "${ROOT}/scripts/lib/docker.sh"
cd "${ROOT}"

ENV_FILE="$(resolve_env_file)" || exit 1
set -a
# shellcheck disable=SC1090
source "${ENV_FILE}"
set +a

SUPER="${POSTGRES_SUPERUSER:-ayanlab_super}"

if ! docker ps --format '{{.Names}}' | grep -qx 'ayanlab-postgres'; then
  echo "ayanlab-postgres is not running — start with ENV=local ./scripts/up-platform.sh" >&2
  exit 1
fi

create_service_schema() {
  local db_name="$1"
  local db_user="$2"
  local schema_name="$3"
  [[ -n "$db_name" && -n "$db_user" && -n "$schema_name" ]] || return 0
  echo "schema ${schema_name} on ${db_name}"
  docker exec ayanlab-postgres psql -v ON_ERROR_STOP=1 -U "${SUPER}" -d "${db_name}" \
    -c "CREATE SCHEMA IF NOT EXISTS \"${schema_name}\" AUTHORIZATION \"${db_user}\";" \
    -c "GRANT ALL ON SCHEMA \"${schema_name}\" TO \"${db_user}\";"
}

# Unified platform monolith: ayanlab_platform / schema core
create_service_schema "${PLATFORM_DB_NAME:-}" "${PLATFORM_DB_USER:-}" "${PLATFORM_DB_SCHEMA:-core}"
create_service_schema "${BOOKING_DB_NAME:-}" "${BOOKING_DB_USER:-}" "${BOOKING_DB_SCHEMA:-booking}"
create_service_schema "${CRM_DB_NAME:-}" "${CRM_DB_USER:-}" "${CRM_DB_SCHEMA:-crm}"
create_service_schema "${HRM_DB_NAME:-}" "${HRM_DB_USER:-}" "${HRM_DB_SCHEMA:-staffiq}"
create_service_schema "${PMS_DB_NAME:-}" "${PMS_DB_USER:-}" "${PMS_DB_SCHEMA:-ipms}"
create_service_schema "${VAULT_DB_NAME:-}" "${VAULT_DB_USER:-}" "${VAULT_DB_SCHEMA:-vault}"
create_service_schema "${COMM_DB_NAME:-}" "${COMM_DB_USER:-}" "${COMM_DB_SCHEMA:-comm}"

echo "Schemas ensured."