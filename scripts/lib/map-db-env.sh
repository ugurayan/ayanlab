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

  if [[ -z "${POSTGIS_DB}" || -z "${POSTGIS_USER}" || -z "${POSTGIS_PASSWORD}" ]]; then
    echo "Missing DB config for ${prefix} (${name_var}, ${user_var}, ${pass_var}) in ${ENV_FILE:-.env}" >&2
    return 1
  fi
}