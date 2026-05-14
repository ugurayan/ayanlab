#!/usr/bin/env bash
# deploy_to_vps.sh — build jar + image, push to VPS, load image, start stack.
#
# Usage (from repo root):
#   export VPS_USER=deploy VPS_HOST=api.ayanlab.com SSH_KEY=~/.ssh/id_ed25519
#   export POSTGIS_USER=... POSTGIS_PASSWORD=... POSTGIS_DB=ayanlabs
#   ./scripts/deploy_to_vps.sh
#
# COMPOSE_FILE:
#   scripts/docker-compose.platform.yml              — platform + PostGIS (default)
#   scripts/docker-compose.platform.external-db.yml — only platform; attach DB container to network `ayanlabs`
#
# DNS: point api.ayanlab.com A/AAAA to the VPS. Terminate TLS on Caddy/nginx and proxy to 127.0.0.1:8080.

set -euo pipefail

VPS_USER=${VPS_USER:-root}
VPS_HOST=${VPS_HOST:-api.ayanlab.com}
SSH_KEY=${SSH_KEY:-$HOME/.ssh/id_rsa}
REMOTE_DIR=${REMOTE_DIR:-/opt/ayanlabs/platform_deploy}
IMAGE_NAME=${IMAGE_NAME:-ayanlabs/platform:latest}
# Relative to repo root; copied to remote as the compose file name basename
COMPOSE_FILE_LOCAL=${COMPOSE_FILE_LOCAL:-scripts/docker-compose.platform.yml}

PROJECT_ROOT=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
PLATFORM_DIR=${PROJECT_ROOT}/platform
REMOTE_COMPOSE_NAME=${REMOTE_COMPOSE_NAME:-docker-compose.yml}

expand_ssh_key() {
  local k="$1"
  if [[ "$k" == ~* ]]; then
    k="${k/#\~/$HOME}"
  fi
  echo "$k"
}
SSH_KEY=$(expand_ssh_key "${SSH_KEY}")

# DB — app reads POSTGIS_* (see platform/src/main/resources/application.yml).
# Inside Docker Compose, hostname for the DB service is `postgis`.
POSTGIS_HOST=${POSTGIS_HOST:-postgis}
POSTGIS_PORT=${POSTGIS_PORT:-5432}
POSTGIS_DB=${POSTGIS_DB:-ayanlabs}
POSTGIS_USER=${POSTGIS_USER:-core}
POSTGIS_SCHEMA=${POSTGIS_SCHEMA:-core}
# POSTGIS_PASSWORD must be set by you (export before run); no default in production path
POSTGIS_PASSWORD=${POSTGIS_PASSWORD:-}

JWT_SECRET=${JWT_SECRET:-}

if [[ -z "${POSTGIS_PASSWORD}" ]]; then
  echo "ERROR: Set POSTGIS_PASSWORD (and preferably JWT_SECRET) in the environment before deploying." >&2
  echo "  export POSTGIS_PASSWORD='...' JWT_SECRET='...'" >&2
  exit 1
fi

if [[ ! -f "${PROJECT_ROOT}/${COMPOSE_FILE_LOCAL}" ]]; then
  echo "ERROR: Compose file not found: ${PROJECT_ROOT}/${COMPOSE_FILE_LOCAL}" >&2
  exit 1
fi

echo "Project root: ${PROJECT_ROOT}"
echo "Remote: ${VPS_USER}@${VPS_HOST}:${REMOTE_DIR}"
echo "Compose (local): ${COMPOSE_FILE_LOCAL}"

echo "Building platform jar..."
cd "${PROJECT_ROOT}"
./gradlew :platform:bootJar -q

echo "Building Docker image ${IMAGE_NAME}..."
docker build -t "${IMAGE_NAME}" "${PLATFORM_DIR}"

TMP_TAR="/tmp/ayanlabs_platform_image_$(date +%s).tar"
echo "Saving image to ${TMP_TAR}..."
docker save -o "${TMP_TAR}" "${IMAGE_NAME}"
TAR_BASENAME=$(basename "${TMP_TAR}")

echo "Ensuring remote directory..."
ssh -i "${SSH_KEY}" -o StrictHostKeyChecking=accept-new "${VPS_USER}@${VPS_HOST}" "mkdir -p ${REMOTE_DIR}"

echo "Copying image tar..."
scp -i "${SSH_KEY}" -o StrictHostKeyChecking=accept-new "${TMP_TAR}" "${VPS_USER}@${VPS_HOST}:${REMOTE_DIR}/${TAR_BASENAME}"

echo "Copying compose file..."
scp -i "${SSH_KEY}" -o StrictHostKeyChecking=accept-new \
  "${PROJECT_ROOT}/${COMPOSE_FILE_LOCAL}" \
  "${VPS_USER}@${VPS_HOST}:${REMOTE_DIR}/${REMOTE_COMPOSE_NAME}"

echo "Writing .env on remote..."
JWT_LINE=""
if [[ -n "${JWT_SECRET}" ]]; then
  JWT_LINE="JWT_SECRET=${JWT_SECRET}"
fi
ssh -i "${SSH_KEY}" -o StrictHostKeyChecking=accept-new "${VPS_USER}@${VPS_HOST}" "umask 077; cat > ${REMOTE_DIR}/.env" <<EOF
POSTGIS_HOST=${POSTGIS_HOST}
POSTGIS_PORT=${POSTGIS_PORT}
POSTGIS_DB=${POSTGIS_DB}
POSTGIS_USER=${POSTGIS_USER}
POSTGIS_PASSWORD=${POSTGIS_PASSWORD}
POSTGIS_SCHEMA=${POSTGIS_SCHEMA}
REDIS_HOST=redis
REDIS_PORT=6379
SPRING_AUTOCONFIGURE_EXCLUDE=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration
${JWT_LINE}
EOF

echo "Loading image and starting stack on remote..."
ssh -i "${SSH_KEY}" -o StrictHostKeyChecking=accept-new "${VPS_USER}@${VPS_HOST}" bash -s <<EOF
set -euo pipefail
cd ${REMOTE_DIR}
docker load -i ${TAR_BASENAME}
docker compose -f ${REMOTE_COMPOSE_NAME} up -d --remove-orphans
EOF

rm -f "${TMP_TAR}"

echo ""
echo "Deployment finished."
echo "- API (after reverse proxy): https://api.ayanlab.com  (DNS A/AAAA → this VPS)"
echo "- Platform listens on host: 127.0.0.1:8080 — configure Caddy/nginx to proxy api.ayanlab.com → http://127.0.0.1:8080"
echo "- Swagger: https://api.ayanlab.com/swagger-ui.html"
echo ""
echo "If the DB container already exists separately, redeploy with:"
echo "  COMPOSE_FILE_LOCAL=scripts/docker-compose.platform.external-db.yml \\"
echo "  ./scripts/deploy_to_vps.sh"
echo "and attach both \`postgis\` and \`ayanlabs-platform\` to the same external Docker network; keep POSTGIS_HOST=postgis in .env."
