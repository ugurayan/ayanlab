#!/usr/bin/env bash
# Provision Postgres on a remote host from repo-root .env.remote:
#   - POSTGRES_SUPERUSER (cluster app superuser for AyanLab)
#   - databases in *_DB_NAME (deduped; often single DB "ayanlab")
#   - login roles *_DB_USER / *_DB_PASSWORD
#   - schemas *_DB_SCHEMA per service
#
# Connects with REMOTE_HOST_* (admin session), e.g. hosting panel root DB user.
################################################################################
# Usage or RUN COMMAND
#   ./scripts/remote_db_initialization.sh
################################################################################
# Requires: psql client, .env.remote at repo root (gitignored).
# PostgreSQL should be running on the remote host.

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ENV_FILE="${ROOT}/.env.remote"

SERVICE_PREFIXES=(PLATFORM BOOKING CRM HRM PMS VAULT COMM)

usage() {
  sed -n '2,12p' "$0" | sed 's/^# \{0,1\}//'
  exit "${1:-0}"
}

if [[ "${1:-}" == "-h" || "${1:-}" == "--help" ]]; then
  usage 0
fi

if [[ ! -f "${ENV_FILE}" ]]; then
  echo "Missing ${ENV_FILE} — copy from .env.local.example and set REMOTE_HOST_*" >&2
  exit 1
fi

PSQL_BIN=""
PSQL_VIA_DOCKER=""
PSQL_DOCKER_IMAGE="${PSQL_DOCKER_IMAGE:-postgres:16-alpine}"

resolve_psql() {
  if command -v psql >/dev/null 2>&1; then
    PSQL_BIN="$(command -v psql)"
    return 0
  fi
  if command -v brew >/dev/null 2>&1; then
    local brew_psql
    brew_psql="$(brew --prefix libpq 2>/dev/null)/bin/psql"
    if [[ -x "${brew_psql}" ]]; then
      PSQL_BIN="${brew_psql}"
      return 0
    fi
    brew_psql="$(brew --prefix postgresql@16 2>/dev/null)/bin/psql"
    if [[ -x "${brew_psql}" ]]; then
      PSQL_BIN="${brew_psql}"
      return 0
    fi
  fi
  if command -v docker >/dev/null 2>&1; then
    PSQL_VIA_DOCKER=1
    return 0
  fi
  return 1
}

if ! resolve_psql; then
  cat >&2 <<'EOF'
psql not found (and Docker/Podman not available).

macOS — prefer lightweight client (bottle, no full postgresql@16 compile):
  xcode-select --install    # finish GUI install on Sequoia, then:
  brew install libpq
  export PATH="$(brew --prefix libpq)/bin:$PATH"

Or use Docker for this script only (no local psql):
  start Docker Desktop, then re-run ./scripts/remote_db_initialization.sh
EOF
  exit 1
fi

psql_exec() {
  if [[ -n "${PSQL_VIA_DOCKER}" ]]; then
    docker run --rm -i -e PGPASSWORD "${PSQL_DOCKER_IMAGE}" psql "$@"
  else
    "${PSQL_BIN}" "$@"
  fi
}

set -a
# shellcheck disable=SC1090
source "${ENV_FILE}"
set +a

sql_escape() {
  printf '%s' "$1" | sed "s/'/''/g"
}

required_vars=(
  REMOTE_HOST
  REMOTE_HOST_DB
  REMOTE_HOST_DB_USER
  REMOTE_HOST_DB_PASSWORD
  POSTGRES_SUPERUSER
  POSTGRES_SUPERUSER_PASSWORD
)

missing=0
for v in "${required_vars[@]}"; do
  if [[ -z "${!v:-}" ]]; then
    echo "MISSING in .env.remote: ${v}" >&2
    missing=$((missing + 1))
  fi
done

for prefix in "${SERVICE_PREFIXES[@]}"; do
  for suffix in DB_NAME DB_USER DB_PASSWORD DB_SCHEMA; do
    v="${prefix}_${suffix}"
    if [[ -z "${!v:-}" ]]; then
      echo "MISSING in .env.remote: ${v}" >&2
      missing=$((missing + 1))
    fi
  done
done

if [[ $missing -gt 0 ]]; then
  echo "Fix ${ENV_FILE} and re-run." >&2
  exit 1
fi

REMOTE_HOST_PORT="${REMOTE_HOST_PORT:-5432}"
PGADMIN_USER="${REMOTE_HOST_DB_USER}"
PGADMIN_DB="${REMOTE_HOST_DB}"
APP_SUPER="${POSTGRES_SUPERUSER}"
APP_SUPER_PASS="${POSTGRES_SUPERUSER_PASSWORD}"

export PGPASSWORD="${REMOTE_HOST_DB_PASSWORD}"

psql_admin() {
  psql_exec -h "${REMOTE_HOST}" -p "${REMOTE_HOST_PORT}" -U "${PGADMIN_USER}" -d "${PGADMIN_DB}" \
    -v ON_ERROR_STOP=1 "$@"
}

psql_on_db() {
  local db="$1"
  shift
  psql_exec -h "${REMOTE_HOST}" -p "${REMOTE_HOST_PORT}" -U "${PGADMIN_USER}" -d "${db}" \
    -v ON_ERROR_STOP=1 "$@"
}

ensure_login_role() {
  local role="$1"
  local pass="$2"
  local pass_sql
  pass_sql="$(sql_escape "${pass}")"
  echo "role ${role}"
  psql_admin -c "
DO \$\$
BEGIN
  IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = '${role}') THEN
    CREATE ROLE \"${role}\" LOGIN PASSWORD '${pass_sql}';
  ELSE
    ALTER ROLE \"${role}\" WITH PASSWORD '${pass_sql}';
  END IF;
END
\$\$;"
}

ensure_database() {
  local db_name="$1"
  local owner="$2"
  echo "database ${db_name} (owner ${owner})"
  local exists
  exists="$(psql_admin -tAc "SELECT 1 FROM pg_database WHERE datname='${db_name}'" | tr -d '[:space:]')"
  if [[ "${exists}" != "1" ]]; then
    psql_admin -c "CREATE DATABASE \"${db_name}\" OWNER \"${owner}\";"
  fi
  psql_admin -c "GRANT ALL PRIVILEGES ON DATABASE \"${db_name}\" TO \"${owner}\";"
}

grant_connect() {
  local db_name="$1"
  local role="$2"
  psql_admin -c "GRANT CONNECT ON DATABASE \"${db_name}\" TO \"${role}\";"
}

ensure_schema() {
  local db_name="$1"
  local role="$2"
  local schema="$3"
  echo "schema ${schema} on ${db_name} for ${role}"
  psql_on_db "${db_name}" -c "
CREATE SCHEMA IF NOT EXISTS \"${schema}\" AUTHORIZATION \"${role}\";
GRANT ALL ON SCHEMA \"${schema}\" TO \"${role}\";
ALTER ROLE \"${role}\" SET search_path TO \"${schema}\", public;"
}

echo "Remote Postgres init"
echo "  host=${REMOTE_HOST}:${REMOTE_HOST_PORT}"
echo "  admin=${PGADMIN_USER}@${PGADMIN_DB}"
echo "  env=${ENV_FILE}"
if [[ -n "${PSQL_VIA_DOCKER}" ]]; then
  echo "  psql=docker (${PSQL_DOCKER_IMAGE})"
else
  echo "  psql=${PSQL_BIN}"
fi
echo ""

ensure_login_role "${APP_SUPER}" "${APP_SUPER_PASS}"

created_dbs=""
db_already_created() {
  [[ " ${created_dbs} " == *" $1 "* ]]
}
mark_db_created() {
  created_dbs+=" $1 "
}

for prefix in "${SERVICE_PREFIXES[@]}"; do
  db_name_var="${prefix}_DB_NAME"
  db_name="${!db_name_var}"
  if ! db_already_created "${db_name}"; then
    ensure_database "${db_name}" "${APP_SUPER}"
    mark_db_created "${db_name}"
  fi
done

for prefix in "${SERVICE_PREFIXES[@]}"; do
  db_name_var="${prefix}_DB_NAME"
  user_var="${prefix}_DB_USER"
  pass_var="${prefix}_DB_PASSWORD"
  ensure_login_role "${!user_var}" "${!pass_var}"
  grant_connect "${!db_name_var}" "${!user_var}"
done

for prefix in "${SERVICE_PREFIXES[@]}"; do
  db_name_var="${prefix}_DB_NAME"
  user_var="${prefix}_DB_USER"
  schema_var="${prefix}_DB_SCHEMA"
  ensure_schema "${!db_name_var}" "${!user_var}" "${!schema_var}"
done

unset PGPASSWORD

echo ""
echo "Done. Created/updated: superuser ${APP_SUPER}, DB(s)${created_dbs}, roles and schemas per .env.remote"