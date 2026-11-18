#!/usr/bin/env bash
# Orchestrate root docker-compose.yml via Compose profiles.
#
# Usage:
#   ./scripts/compose.sh up   <stack> [compose service names...]
#   ./scripts/compose.sh down <stack>
#   ./scripts/compose.sh ps|logs|build ...  (optional <stack> for profile flags)
#
# Stacks (comma-merged when you pass several):
#   infra              postgres, redis, kafka
#   platform           + platform API/UI (implies infra)
#   vault|hrm|pms|crm|booking|comm
#   products           all product profiles (not platform)
#   all                infra + platform + every product
#
# Examples:
#   ./scripts/compose.sh up infra
#   ./scripts/compose.sh up platform
#   ./scripts/compose.sh up infra postgres redis
#   ./scripts/compose.sh down platform
#   ./scripts/compose.sh logs -f postgres

set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
# shellcheck source=lib/resolve-env-file.sh
source "${ROOT}/scripts/lib/resolve-env-file.sh"
# shellcheck source=lib/docker.sh
source "${ROOT}/scripts/lib/docker.sh"
cd "${ROOT}"

COMPOSE_FILE="${ROOT}/docker-compose.yml"
PRODUCT_PROFILES=(vault hrm pms crm booking comm)

usage() {
  sed -n '2,20p' "$0" | sed 's/^# \{0,1\}//'
  exit "${1:-0}"
}

stack_profiles() {
  local stack="$1"
  case "${stack}" in
    infra) echo infra ;;
    platform) echo "infra platform" ;;
    vault|hrm|pms|crm|booking|comm) echo "infra ${stack}" ;;
    products) echo "infra ${PRODUCT_PROFILES[*]}" ;;
    all|full) echo "infra platform ${PRODUCT_PROFILES[*]}" ;;
  esac
}

expand_stacks() {
  local out=()
  local tok
  for tok in "$@"; do
    IFS=' ' read -r -a parts <<< "$(stack_profiles "${tok}")"
    local p
    for p in "${parts[@]}"; do
      out+=("${p}")
    done
  done
  printf '%s\n' "${out[@]}" | awk '!seen[$0]++'
}

ENV_FILE="$(resolve_env_file)" || exit 1
[[ -f "${ENV_FILE}" ]] || { echo "Missing ${ENV_FILE}" >&2; exit 1; }
export ENV ENV_FILE

if [[ $# -lt 1 ]]; then
  usage 1
fi

cmd="$1"
shift

stacks=()
while [[ $# -gt 0 ]]; do
  case "$1" in
    infra|platform|vault|hrm|pms|crm|booking|comm|products|all|full)
      stacks+=("$1")
      shift
      ;;
    *)
      break
      ;;
  esac
done

profile_flags=()
if [[ ${#stacks[@]} -gt 0 ]]; then
  while IFS= read -r p; do
    [[ -z "${p}" ]] && continue
    profile_flags+=(--profile "${p}")
  done < <(expand_stacks "${stacks[@]}")
fi

compose() {
  docker compose --env-file "${ENV_FILE}" -f "${COMPOSE_FILE}" "${profile_flags[@]}" "$@"
}

case "${cmd}" in
  up)
    [[ ${#stacks[@]} -eq 0 ]] && { echo "up requires a stack (e.g. infra, platform)" >&2; usage 1; }
    if [[ "${ENV:-local}" == "local" && -f "${ROOT}/scripts/validate-env-local.sh" ]]; then
      "${ROOT}/scripts/validate-env-local.sh" || exit 1
    fi
    up_args=(-d --build --remove-orphans)
    compose up "${up_args[@]}" "$@"
    if [[ " ${stacks[*]} " == *" platform "* ]] || [[ " ${stacks[*]} " == *" all "* ]] || [[ " ${stacks[*]} " == *" full "* ]]; then
      "${ROOT}/scripts/ensure-db-schemas.sh" || true
      docker restart ayanlab-platform-backend 2>/dev/null || true
      echo ""
      echo "Loaded env: ${ENV_FILE}"
      echo "Platform API:  http://localhost:8102/auth/api/login"
      echo "Swagger:       http://localhost:8102/swagger-ui/index.html"
      echo "UI:            http://localhost:3102"
      echo "Postgres:      localhost:${AYANLAB_POSTGRES_HOST_PORT:-5433}"
    fi
    ;;
  down)
    [[ ${#stacks[@]} -eq 0 ]] && { echo "down requires a stack" >&2; usage 1; }
    compose down "$@"
    ;;
  ps|logs|build|config|pull|restart|stop|start|top|events|images)
    compose "${cmd}" "$@"
    ;;
  stacks|help|-h|--help)
    usage 0
    ;;
  *)
    echo "Unknown command: ${cmd}" >&2
    usage 1
    ;;
esac