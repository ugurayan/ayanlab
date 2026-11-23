#!/usr/bin/env bash
# Map {SERVICE}_DB_* from .env → POSTGIS_* for Spring Boot.

map_db_env() {
  local prefix="${1:?service prefix AUTH|CORE|...}"
  local name_var="${prefix}_DB_NAME"
  local user_var="${prefix}_DB_USER"
  local pass_var="${prefix}_DB_PASSWORD"
  local schema_var="${prefix}_DB_SCHEMA"

  export POSTGIS_DB="${!name_var:-}"
  export POSTGIS_USER="${!user_var:-}"
  export POSTGIS_PASSWORD="${!pass_var:-}"
  export POSTGIS_SCHEMA="${!schema_var:-}"
  export POSTGIS_HOST="${POSTGIS_HOST:-localhost}"
  export POSTGIS_PORT="${POSTGIS_PORT:-5432}"

  # .env.remote: JVM on laptop → VPS Postgres (unless POSTGIS_HOST already points elsewhere).
  if [[ "${AYANLAB_ENV:-}" == "remote" || "${ENV:-}" == "remote" ]] && [[ -n "${REMOTE_HOST:-}" ]]; then
    case "${POSTGIS_HOST}" in
      localhost|127.0.0.1|postgres|"")
        export POSTGIS_HOST="${REMOTE_HOST}"
        export POSTGIS_PORT="${REMOTE_HOST_PORT:-5432}"
        ;;
    esac
  fi

  if [[ -z "${POSTGIS_DB}" || -z "${POSTGIS_USER}" || -z "${POSTGIS_PASSWORD}" ]]; then
    echo "Missing DB config for ${prefix} (${name_var}, ${user_var}, ${pass_var}) in ${ENV_FILE:-.env}" >&2
    return 1
  fi
}