#!/bin/bash
# Creates per-service DB + role (runs once on empty volume).
# Requires variables from .env.local loaded into the postgres container.
set -euo pipefail

create_service_db() {
  local db_name="$1"
  local db_user="$2"
  local db_password="$3"

  if [[ -z "$db_name" || -z "$db_user" || -z "$db_password" ]]; then
    echo "Skipping empty db triple (name/user/password)" >&2
    return 0
  fi

  echo "Provisioning database=${db_name} user=${db_user}"

  psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" --dbname "${POSTGRES_DB}" <<-EOSQL
DO \$\$
BEGIN
  IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = '${db_user}') THEN
    CREATE ROLE "${db_user}" LOGIN PASSWORD '${db_password//\'/\'\'}';
  ELSE
    ALTER ROLE "${db_user}" WITH PASSWORD '${db_password//\'/\'\'}';
  END IF;
END
\$\$;

SELECT 'CREATE DATABASE "${db_name}" OWNER "${db_user}"'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = '${db_name}')\gexec

GRANT ALL PRIVILEGES ON DATABASE "${db_name}" TO "${db_user}";
EOSQL
}

create_service_schema() {
  local db_name="$1"
  local db_user="$2"
  local schema_name="$3"

  if [[ -z "$db_name" || -z "$db_user" || -z "$schema_name" ]]; then
    return 0
  fi

  echo "Provisioning schema=${schema_name} on database=${db_name}"

  psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" --dbname "${db_name}" <<-EOSQL
CREATE SCHEMA IF NOT EXISTS "${schema_name}" AUTHORIZATION "${db_user}";
GRANT ALL ON SCHEMA "${schema_name}" TO "${db_user}";
ALTER ROLE "${db_user}" SET search_path TO "${schema_name}", public;
EOSQL
}

create_service_db "${PLATFORM_DB_NAME:-}" "${PLATFORM_DB_USER:-}" "${PLATFORM_DB_PASSWORD:-}"
create_service_db "${BOOKING_DB_NAME:-}" "${BOOKING_DB_USER:-}" "${BOOKING_DB_PASSWORD:-}"
create_service_db "${CRM_DB_NAME:-}" "${CRM_DB_USER:-}" "${CRM_DB_PASSWORD:-}"
create_service_db "${HRM_DB_NAME:-}" "${HRM_DB_USER:-}" "${HRM_DB_PASSWORD:-}"
create_service_db "${PMS_DB_NAME:-}" "${PMS_DB_USER:-}" "${PMS_DB_PASSWORD:-}"
create_service_db "${VAULT_DB_NAME:-}" "${VAULT_DB_USER:-}" "${VAULT_DB_PASSWORD:-}"
create_service_db "${COMM_DB_NAME:-}" "${COMM_DB_USER:-}" "${COMM_DB_PASSWORD:-}"

create_service_schema "${PLATFORM_DB_NAME:-}" "${PLATFORM_DB_USER:-}" "${PLATFORM_DB_SCHEMA:-core}"
create_service_schema "${BOOKING_DB_NAME:-}" "${BOOKING_DB_USER:-}" "${BOOKING_DB_SCHEMA:-booking}"
create_service_schema "${CRM_DB_NAME:-}" "${CRM_DB_USER:-}" "${CRM_DB_SCHEMA:-crm}"
create_service_schema "${HRM_DB_NAME:-}" "${HRM_DB_USER:-}" "${HRM_DB_SCHEMA:-staffiq}"
create_service_schema "${PMS_DB_NAME:-}" "${PMS_DB_USER:-}" "${PMS_DB_SCHEMA:-ipms}"
create_service_schema "${VAULT_DB_NAME:-}" "${VAULT_DB_USER:-}" "${VAULT_DB_SCHEMA:-vault}"
create_service_schema "${COMM_DB_NAME:-}" "${COMM_DB_USER:-}" "${COMM_DB_SCHEMA:-comm}"

echo "Per-service databases and schemas provisioned."