#!/usr/bin/env bash
# deploy_to_vps.sh — build jar + image, push to VPS, load image, start platform.
#
# Targets api.ayanlab.com (TLS on Caddy/nginx → http://127.0.0.1:8080).
# Default: connect to existing PostGIS on Docker network `dockers_internal`
# (Compose project `dockers` + network `internal` → dockers_internal; container `postgis`).
#
# Usage (from repo root) — same host as: ssh root@147.93.138.158
#   export POSTGIS_USER=... POSTGIS_PASSWORD=... POSTGIS_DB=ayanLabs
#   POSTGIS_DB must match an existing DB on PostGIS (create once as postgres if missing).
#   export JWT_SECRET=...
#   ./scripts/deploy_to_vps.sh
#
# Optional: SSH_KEY=~/.ssh/id_ed25519  (omit to use ssh default identity / agent)
#
# All-in-one (bundled PostGIS) instead of shared VPS services:
#   COMPOSE_FILE_LOCAL=scripts/docker-compose.platform.yml ./scripts/deploy_to_vps.sh
#
# Override service hostnames if your VPS uses different container names:
#   DOCKER_NETWORK=dockers_internal ./scripts/deploy_to_vps.sh

set -euo pipefail
# Required before deploy (do not commit secrets):
#   export POSTGIS_PASSWORD='...' JWT_SECRET='...'
# POSTGIS_DB must match the existing database name exactly (PostgreSQL is case-sensitive:
#   "ayanLabs" ≠ "ayanlabs"). Default matches application.yml and local PostGIS compose.

VPS_USER=${VPS_USER:-root}
VPS_HOST=${VPS_HOST:-147.93.138.158}
SSH_KEY=${SSH_KEY:-}
REMOTE_DIR=${REMOTE_DIR:-/root/dockers/platform}
IMAGE_NAME=${IMAGE_NAME:-platform:latest}
# Shared VPS: platform joins existing `dockers_internal` network and talks to `postgis`
COMPOSE_FILE_LOCAL=${COMPOSE_FILE_LOCAL:-scripts/docker-compose.platform.external-db.yml}
DOCKER_NETWORK=${DOCKER_NETWORK:-dockers_internal}

PROJECT_ROOT=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
PLATFORM_DIR=${PROJECT_ROOT}/platform
REMOTE_COMPOSE_NAME=${REMOTE_COMPOSE_NAME:-docker-compose.yml}

expand_ssh_key() {
  local k="$1"
  if [[ -z "$k" ]]; then
    return 0
  fi
  if [[ "$k" == ~* ]]; then
    k="${k/#\~/$HOME}"
  fi
  echo "$k"
}
SSH_KEY=$(expand_ssh_key "${SSH_KEY}")

SSH_OPTS=(-o StrictHostKeyChecking=accept-new)
SCP_OPTS=(-o StrictHostKeyChecking=accept-new)
if [[ -n "${SSH_KEY}" ]]; then
  if [[ ! -f "${SSH_KEY}" ]]; then
    echo "ERROR: SSH_KEY not found: ${SSH_KEY}" >&2
    exit 1
  fi
  SSH_OPTS+=(-i "${SSH_KEY}")
  SCP_OPTS+=(-i "${SSH_KEY}")
fi

# DB — same variables as VPS PostGIS compose (POSTGIS_DB, POSTGIS_USER, POSTGIS_PASSWORD)
POSTGIS_HOST=${POSTGIS_HOST:-postgis}
POSTGIS_PORT=${POSTGIS_PORT:-5432}
POSTGIS_DB=${POSTGIS_DB:-ayanLabs}
POSTGIS_PASSWORD=${POSTGIS_PASSWORD:-UgurAyanLabs9602.}
POSTGIS_USER=${POSTGIS_USER:-core}
if [[ "${POSTGIS_DB}" == "ayanlabs" ]]; then
  echo "WARNING: POSTGIS_DB=ayanlabs is lowercase; VPS DB is usually ayanLabs (mixed case)." >&2
  echo "  export POSTGIS_DB=ayanLabs   # or fix remote .env on the VPS" >&2
fi
POSTGIS_USER=${POSTGIS_USER:-core}
POSTGIS_SCHEMA=${POSTGIS_SCHEMA:-core}
POSTGIS_SCHEMAS=${POSTGIS_SCHEMAS:-core,ipms,crm,vault,staffiq}
POSTGIS_PASSWORD=${POSTGIS_PASSWORD:-}

# platform container must use Docker DNS name `postgis`, not VPS public IP / localhost
if [[ "${POSTGIS_HOST}" =~ ^[0-9.]+$ ]] || [[ "${POSTGIS_HOST}" == "localhost" || "${POSTGIS_HOST}" == "127.0.0.1" ]]; then
  echo "NOTE: POSTGIS_HOST was '${POSTGIS_HOST}' — using 'postgis' for VPS Docker deploy." >&2
  POSTGIS_HOST=postgis
fi

JWT_SECRET=${JWT_SECRET:-}

if [[ -z "${POSTGIS_PASSWORD}" ]]; then
  echo "ERROR: Set POSTGIS_PASSWORD (and JWT_SECRET) in the environment before deploying." >&2
  echo "  export POSTGIS_PASSWORD='...' JWT_SECRET='...'" >&2
  exit 1
fi

if [[ ! -f "${PROJECT_ROOT}/${COMPOSE_FILE_LOCAL}" ]]; then
  echo "ERROR: Compose file not found: ${PROJECT_ROOT}/${COMPOSE_FILE_LOCAL}" >&2
  exit 1
fi

echo "Project root: ${PROJECT_ROOT}"
echo "Remote: ${VPS_USER}@${VPS_HOST}:${REMOTE_DIR}"
echo "Compose: ${COMPOSE_FILE_LOCAL}"
echo "Docker network: ${DOCKER_NETWORK} (DB host: ${POSTGIS_HOST})"

echo "Building platform jar..."
cd "${PROJECT_ROOT}"
./gradlew :platform:bootJar -q

echo "Building Docker image ${IMAGE_NAME}..."
docker build -t "${IMAGE_NAME}" "${PLATFORM_DIR}"

TMP_TAR="/tmp/ayanlabs_platform_image_$(date +%s).tar"
echo "Saving image to ${TMP_TAR}..."
docker save -o "${TMP_TAR}" "${IMAGE_NAME}"
TAR_BASENAME=$(basename "${TMP_TAR}")

PLATFORM_JAR=$(ls -1 "${PLATFORM_DIR}"/build/libs/*.jar 2>/dev/null | head -1)
if [[ -z "${PLATFORM_JAR}" || ! -f "${PLATFORM_JAR}" ]]; then
  echo "ERROR: Platform jar not found under ${PLATFORM_DIR}/build/libs/" >&2
  exit 1
fi
JAR_BASENAME=$(basename "${PLATFORM_JAR}")

echo "Ensuring remote directory ${REMOTE_DIR}..."
ssh "${SSH_OPTS[@]}" "${VPS_USER}@${VPS_HOST}" "mkdir -p ${REMOTE_DIR}/build/libs"

echo "Copying platform jar..."
scp "${SCP_OPTS[@]}" "${PLATFORM_JAR}" "${VPS_USER}@${VPS_HOST}:${REMOTE_DIR}/build/libs/${JAR_BASENAME}"

echo "Copying image tar..."
scp "${SCP_OPTS[@]}" "${TMP_TAR}" "${VPS_USER}@${VPS_HOST}:${REMOTE_DIR}/${TAR_BASENAME}"

echo "Copying compose file..."
scp "${SCP_OPTS[@]}" \
  "${PROJECT_ROOT}/${COMPOSE_FILE_LOCAL}" \
  "${VPS_USER}@${VPS_HOST}:${REMOTE_DIR}/${REMOTE_COMPOSE_NAME}"

echo "Writing .env on remote..."
JWT_LINE=""
if [[ -n "${JWT_SECRET}" ]]; then
  JWT_LINE="JWT_SECRET=${JWT_SECRET}"
fi
ssh "${SSH_OPTS[@]}" "${VPS_USER}@${VPS_HOST}" "umask 077; cat > ${REMOTE_DIR}/.env" <<EOF
DOCKER_NETWORK=${DOCKER_NETWORK}
POSTGIS_HOST=${POSTGIS_HOST}
POSTGIS_PORT=${POSTGIS_PORT}
POSTGIS_DB=${POSTGIS_DB}
POSTGIS_USER=${POSTGIS_USER}
POSTGIS_PASSWORD=${POSTGIS_PASSWORD}
POSTGIS_SCHEMA=${POSTGIS_SCHEMA}
POSTGIS_SCHEMAS=${POSTGIS_SCHEMAS}
SPRING_AUTOCONFIGURE_EXCLUDE=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration
${JWT_LINE}
EOF

echo "Loading image and starting platform on remote..."
ssh "${SSH_OPTS[@]}" "${VPS_USER}@${VPS_HOST}" bash -s <<EOF
set -euo pipefail
cd ${REMOTE_DIR}
CONTAINER_NAME=platform
LEGACY_CONTAINER=ayanlabs-platform
COMPOSE_CMD="docker compose"
if ! docker compose version >/dev/null 2>&1; then
  COMPOSE_CMD="docker-compose"
fi

if ! docker network inspect ${DOCKER_NETWORK} >/dev/null 2>&1; then
  echo "ERROR: Docker network '${DOCKER_NETWORK}' not found. Start your VPS stack (postgis) first." >&2
  exit 1
fi

# Stop existing stack / containers before loading the new image
if [[ -f ${REMOTE_COMPOSE_NAME} ]]; then
  \${COMPOSE_CMD} -f ${REMOTE_COMPOSE_NAME} down --remove-orphans 2>/dev/null || true
fi
for name in "\${CONTAINER_NAME}" "\${LEGACY_CONTAINER}"; do
  if docker ps -a --format '{{.Names}}' | grep -Fxq "\${name}"; then
    echo "Stopping and removing container: \${name}"
    docker stop "\${name}" 2>/dev/null || true
    docker rm -f "\${name}" 2>/dev/null || true
  fi
done

echo "Loading image ${IMAGE_NAME}..."
docker load -i ${TAR_BASENAME}
rm -f ${TAR_BASENAME}
rm -f build/libs/*.jar

echo "Starting platform..."
\${COMPOSE_CMD} -f ${REMOTE_COMPOSE_NAME} up -d --force-recreate --remove-orphans
EOF

rm -f "${TMP_TAR}"

echo ""
echo "Deployment finished."
echo "- Remote dir: ${REMOTE_DIR}"
echo "- Container: platform (image ${IMAGE_NAME})"
echo "- API (after reverse proxy): https://api.ayanlab.com → http://127.0.0.1:8080"
echo "- DB: ${POSTGIS_HOST}:${POSTGIS_PORT}/${POSTGIS_DB} on network ${DOCKER_NETWORK}"
