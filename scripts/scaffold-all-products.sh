#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LIB="${ROOT}/scripts/lib"

chmod +x "${LIB}/scaffold-backend.sh" "${LIB}/scaffold-ui.sh" "${LIB}/scaffold-mobile.sh"

write_product_compose() {
  local product="$1"
  shift
  local compose="${ROOT}/${product}/docker-compose.yml"
  mkdir -p "${ROOT}/${product}"

  cat > "${compose}" <<'HEADER'
# Attach to shared infra: docker compose -f docker/docker-compose.infra.yml up -d

services:
HEADER

  while [[ $# -gt 0 ]]; do
  local kind="$1"
  local name="$2"
  local port="$3"
  shift 3
  local svc_key="${name//-/_}"
  if [[ "${kind}" == "backend" ]]; then
    cat >> "${compose}" <<EOF
  ${svc_key}:
    build:
      context: ./${name}
      dockerfile: Dockerfile
    container_name: ${name}
    restart: unless-stopped
    env_file:
      - ../.env
    environment:
      SERVER_PORT: 8080
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/\${POSTGRES_DB:-ayanLabs}
      SPRING_DATA_REDIS_HOST: redis
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
    ports:
      - "${port}:8080"
    networks:
      - ayanlab_internal

EOF
  elif [[ "${kind}" == "ui" ]]; then
    cat >> "${compose}" <<EOF
  ${svc_key}:
    build:
      context: ./${name}
      dockerfile: Dockerfile
    container_name: ${name}
    restart: unless-stopped
    ports:
      - "${port}:80"
    networks:
      - ayanlab_internal

EOF
  fi
  done

  cat >> "${compose}" <<'FOOTER'
networks:
  ayanlab_internal:
    external: true
    name: ayanlab_internal
FOOTER
}

# auth
PRODUCT=ayanlab-platform
"${LIB}/scaffold-backend.sh" "${ROOT}/${PRODUCT}/${PRODUCT}-backend" "${PRODUCT}-backend" "8101" "com.ayanlabs.auth"
"${LIB}/scaffold-ui.sh" "${ROOT}/${PRODUCT}/${PRODUCT}-ui" "${PRODUCT}-ui" "3101"
write_product_compose "${PRODUCT}" backend "${PRODUCT}-backend" 8101 ui "${PRODUCT}-ui" 3101

# core
PRODUCT=ayanlab-platform
"${LIB}/scaffold-backend.sh" "${ROOT}/${PRODUCT}/${PRODUCT}-backend" "${PRODUCT}-backend" "8102" "com.ayanlabs.core"
write_product_compose "${PRODUCT}" backend "${PRODUCT}-backend" 8102

for spec in \
  "ayanlab-vault:8103:3103:com.ayanlabs.vault" \
  "ayanlab-hrm:8104:3104:com.ayanlabs.hrm" \
  "ayanlab-pms:8105:3105:com.ayanlabs.pms" \
  "ayanlab-crm:8106:3106:com.ayanlabs.crm" \
  "ayanlab-booking:8107:3107:com.ayanlabs.booking" \
  "ayanlab-comm:8108:3108:com.ayanlabs.comm"; do
  IFS=: read -r PRODUCT BPORT UPORT PKG <<< "${spec}"
  "${LIB}/scaffold-backend.sh" "${ROOT}/${PRODUCT}/${PRODUCT}-backend" "${PRODUCT}-backend" "${BPORT}" "${PKG}"
  "${LIB}/scaffold-ui.sh" "${ROOT}/${PRODUCT}/${PRODUCT}-ui" "${PRODUCT}-ui" "${UPORT}"
  "${LIB}/scaffold-mobile.sh" "${ROOT}/${PRODUCT}/${PRODUCT}-mobile" "${PRODUCT}-mobile"
  write_product_compose "${PRODUCT}" \
    backend "${PRODUCT}-backend" "${BPORT}" \
    ui "${PRODUCT}-ui" "${UPORT}"
done

echo "Scaffold complete under ${ROOT}"