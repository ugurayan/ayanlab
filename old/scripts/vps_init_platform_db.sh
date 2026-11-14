#!/usr/bin/env bash
# One-time: create platform database on VPS PostGIS.
#
# From your laptop:
#   ssh root@147.93.138.158 'POSTGIS_DB=ayanLabs POSTGIS_USER=core bash -s' < scripts/vps_init_platform_db.sh
#
# On the VPS:
#   POSTGIS_DB=ayanLabs POSTGIS_USER=core ./vps_init_platform_db.sh

set -euo pipefail

POSTGIS_DB=${POSTGIS_DB:-ayanLabs}
POSTGIS_USER=${POSTGIS_USER:-core}
POSTGIS_CONTAINER=${POSTGIS_CONTAINER:-postgis}
POSTGRES_SUPERUSER=${POSTGRES_SUPERUSER:-postgres}

exists=$(docker exec "${POSTGIS_CONTAINER}" psql -U "${POSTGRES_SUPERUSER}" -tAc \
  "SELECT 1 FROM pg_database WHERE datname = '${POSTGIS_DB}'" || true)

if [[ "${exists}" == "1" ]]; then
  echo "Database '${POSTGIS_DB}' already exists."
else
  echo "Creating database '${POSTGIS_DB}' with owner '${POSTGIS_USER}'..."
  docker exec "${POSTGIS_CONTAINER}" psql -U "${POSTGRES_SUPERUSER}" -v ON_ERROR_STOP=1 \
    -c "CREATE DATABASE \"${POSTGIS_DB}\" OWNER \"${POSTGIS_USER}\";"
  echo "Created."
fi

echo "Granting schema privileges to ${POSTGIS_USER}..."
docker exec "${POSTGIS_CONTAINER}" psql -U "${POSTGRES_SUPERUSER}" -d "${POSTGIS_DB}" -v ON_ERROR_STOP=1 <<SQL
GRANT ALL ON SCHEMA public TO "${POSTGIS_USER}";
SQL

echo "OK — platform can use POSTGIS_DB=${POSTGIS_DB} POSTGIS_USER=${POSTGIS_USER}"
