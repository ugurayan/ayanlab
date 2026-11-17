#!/usr/bin/env bash
# Verify .env.local (or ENV=prod) has required DB + TZ settings.
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
# shellcheck source=lib/resolve-env-file.sh
source "${ROOT}/scripts/lib/resolve-env-file.sh"
cd "${ROOT}"
load_env_file

required_vars=(
  TZ
  POSTGRES_SUPERUSER
  POSTGRES_SUPERUSER_PASSWORD
  PLATFORM_DB_NAME PLATFORM_DB_USER PLATFORM_DB_PASSWORD PLATFORM_DB_SCHEMA
  BOOKING_DB_NAME BOOKING_DB_USER BOOKING_DB_PASSWORD BOOKING_DB_SCHEMA
  CRM_DB_NAME CRM_DB_USER CRM_DB_PASSWORD CRM_DB_SCHEMA
  HRM_DB_NAME HRM_DB_USER HRM_DB_PASSWORD HRM_DB_SCHEMA
  PMS_DB_NAME PMS_DB_USER PMS_DB_PASSWORD PMS_DB_SCHEMA
  VAULT_DB_NAME VAULT_DB_USER VAULT_DB_PASSWORD VAULT_DB_SCHEMA
  COMM_DB_NAME COMM_DB_USER COMM_DB_PASSWORD COMM_DB_SCHEMA
  JWT_SECRET
)

missing=0
for v in "${required_vars[@]}"; do
  if [[ -z "${!v:-}" ]]; then
    echo "MISSING: $v"
    missing=$((missing + 1))
  fi
done

if [[ "${TZ:-}" != "America/Los_Angeles" ]]; then
  echo "WARN: TZ=${TZ:-} (expected America/Los_Angeles for Los Angeles)"
fi

if [[ $missing -gt 0 ]]; then
  echo "Validation failed ($missing missing). Edit ${ENV_FILE}" >&2
  exit 1
fi

echo "OK: ${ENV_FILE} — platform + 6 product DBs + superuser + JWT defined."