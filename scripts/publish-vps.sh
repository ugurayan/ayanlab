#!/usr/bin/env bash
# publish-vps.sh — sync repo and rebuild product stacks on VPS (or local with MODE=local).
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
MODE=${MODE:-vps}
VPS_USER=${VPS_USER:-root}
VPS_HOST=${VPS_HOST:-}
REMOTE_DIR=${REMOTE_DIR:-/root/dockers/ayanlab}
PRODUCT=${PRODUCT:-all}
SSH_KEY=${SSH_KEY:-}

ALL_PRODUCTS=(ayanlab-auth ayanlab-core ayanlab-vault ayanlab-hrm ayanlab-pms ayanlab-crm ayanlab-booking ayanlab-comm)

expand_ssh_key() {
  local k="$1"
  [[ -z "$k" ]] && return 0
  [[ "$k" == ~* ]] && k="${k/#\~/$HOME}"
  echo "$k"
}
SSH_KEY=$(expand_ssh_key "${SSH_KEY}")

SSH_OPTS=(-o StrictHostKeyChecking=accept-new)
RSYNC_SSH="ssh ${SSH_OPTS[*]}"
if [[ -n "${SSH_KEY}" ]]; then
  SSH_OPTS+=(-i "${SSH_KEY}")
  RSYNC_SSH="ssh -i ${SSH_KEY} ${SSH_OPTS[*]}"
fi

products_to_build() {
  if [[ "${PRODUCT}" == "all" ]]; then
    printf '%s\n' "${ALL_PRODUCTS[@]}"
  else
    IFS=',' read -ra parts <<< "${PRODUCT}"
    for p in "${parts[@]}"; do
      case "${p}" in
        auth) echo ayanlab-auth ;;
        core) echo ayanlab-core ;;
        vault) echo ayanlab-vault ;;
        hrm) echo ayanlab-hrm ;;
        pms) echo ayanlab-pms ;;
        crm) echo ayanlab-crm ;;
        booking) echo ayanlab-booking ;;
        comm) echo ayanlab-comm ;;
        ayanlab-*) echo "${p}" ;;
        *) echo "Unknown product alias: ${p}" >&2; exit 1 ;;
      esac
    done
  fi
}

run_compose() {
  local dir="$1"
  docker compose -f "${dir}/docker-compose.yml" up -d --build
}

if [[ "${MODE}" == "local" ]]; then
  docker compose -f "${ROOT}/docker/docker-compose.infra.yml" up -d
  while IFS= read -r prod; do
    [[ -z "${prod}" ]] && continue
    echo "==> ${prod}"
    run_compose "${ROOT}/${prod}"
  done < <(products_to_build)
  exit 0
fi

if [[ -z "${VPS_HOST}" ]]; then
  echo "Set VPS_HOST or MODE=local" >&2
  exit 1
fi

rsync -az --delete \
  --exclude '.git' \
  --exclude 'old/build' \
  --exclude 'old/.gradle' \
  --exclude '**/node_modules' \
  --exclude '**/.gradle' \
  --exclude '**/build' \
  -e "${RSYNC_SSH}" \
  "${ROOT}/" "${VPS_USER}@${VPS_HOST}:${REMOTE_DIR}/"

ssh "${SSH_OPTS[@]}" "${VPS_USER}@${VPS_HOST}" bash -s <<REMOTE
set -euo pipefail
cd "${REMOTE_DIR}"
docker compose -f docker/docker-compose.infra.yml up -d
REMOTE

while IFS= read -r prod; do
  [[ -z "${prod}" ]] && continue
  ssh "${SSH_OPTS[@]}" "${VPS_USER}@${VPS_HOST}" \
    "cd ${REMOTE_DIR}/${prod} && docker compose up -d --build"
done < <(products_to_build)

echo "Deploy finished."