#!/usr/bin/env bash
# Interactive launcher for platform + product backends / platform UI / mobile apps.
# No Docker — loads repo-root .env.* and passes POSTGIS_* / JWT_* / etc. to Spring (mvn bootRun).
# Usage:
#   ./run-local.sh [.env.local|.env.remote|.env.prod] [target ...]
#   ./run-local.sh local backend          # shorthand
#   ./run-local.sh --env-file ../.env.remote vault
#   ./run-local.sh                        → menu (default: .env.local)
#   ./run-local.sh backend                → platform JVM
#   ./run-local.sh ui                     → React admin UI
#   ./run-local.sh vault                  → delegate to ayanlab-vault/run-local.sh
#   ./run-local.sh hrm mobile             → Flutter (default device: macos)

################################################################################
# cd ayanlab-platform
# ./run-local.sh .env.remote backend
# ./run-local.sh .env.local vault
# ./run-local.sh .env.prod crm

# # kısa isimler
# ./run-local.sh remote backend
# ./run-local.sh local

# # özel yol
# ./run-local.sh --env-file /path/to/.env.prod vault
# ./run-local.sh -e remote hrm mobile
################################################################################


set -euo pipefail
HERE="$(cd "$(dirname "$0")" && pwd)"
ROOT="$(cd "$HERE/.." && pwd)"
# shellcheck source=../scripts/lib/resolve-env-file.sh
source "$ROOT/scripts/lib/resolve-env-file.sh"

# Env spec must run in this shell (not a subshell) so ENV / ENV_FILE reach run-backend.sh.
while [[ $# -gt 0 ]]; do
  case "$1" in
    --env-file)
      [[ $# -ge 2 ]] || { echo "Missing path after --env-file" >&2; exit 1; }
      apply_env_spec "$2" "$ROOT" || exit 1
      shift 2
      ;;
    -e)
      [[ $# -ge 2 ]] || { echo "Missing env after -e" >&2; exit 1; }
      apply_env_spec "$2" "$ROOT" || exit 1
      shift 2
      ;;
    .env.local|.env.remote|.env.prod)
      apply_env_spec "$1" "$ROOT" || exit 1
      shift
      ;;
    local|dev|remote|prod|production)
      apply_env_spec "$1" "$ROOT" || exit 1
      shift
      ;;
    *)
      break
      ;;
  esac
done

if [[ -z "${ENV_FILE:-}" ]]; then
  export ENV="${ENV:-local}"
  ENV_FILE="${ROOT}/$(resolve_env_file)"
  export ENV_FILE
fi

require_env_file() {
  if [[ ! -f "${ENV_FILE}" ]]; then
    echo "Missing ${ENV_FILE} — cp .env.local.example .env.local at repo root" >&2
    exit 1
  fi
}

run_backend() {
  require_env_file
  exec "$HERE/scripts/run-backend.sh" "$@"
}

run_ui() {
  require_env_file
  exec "$HERE/scripts/run-ui.sh" "$@"
}

run_product() {
  local key="$1"
  shift || true
  local dir name
  case "$key" in
    vault)   dir="ayanlab-vault";   name="ayanlab-vault-backend" ;;
    pms)     dir="ayanlab-pms";     name="ayanlab-pms-backend" ;;
    crm)     dir="ayanlab-crm";     name="ayanlab-crm-backend" ;;
    hrm)     dir="ayanlab-hrm";     name="ayanlab-hrm-backend" ;;
    booking) dir="ayanlab-booking"; name="ayanlab-booking-backend" ;;
    comm)    dir="ayanlab-comm";    name="ayanlab-comm-backend" ;;
    *)
      echo "Unknown product: $key" >&2
      exit 1
      ;;
  esac
  if [[ ! -x "$ROOT/$dir/run-local.sh" ]]; then
    echo "Missing $ROOT/$dir/run-local.sh" >&2
    exit 1
  fi
  require_env_file
  echo "Delegating to $name ($dir/run-local.sh)  env=${ENV_FILE}"
  exec "$ROOT/$dir/run-local.sh" "$@"
}

print_menu() {
  cat <<MENU

AyanLab — apps on this machine (ENV=${ENV})
  env file: ${ENV_FILE}

  Platform
    1) ayanlab-platform-backend   (Spring Boot API)
    2) ayanlab-platform-ui        (admin / CRUD UI, port 3102)

  Other product backends
    3) ayanlab-vault-backend
    4) ayanlab-pms-backend
    5) ayanlab-crm-backend
    6) ayanlab-hrm-backend
    7) ayanlab-booking-backend
    8) ayanlab-comm-backend

  Mobile apps (Flutter — default device: macos, FLUTTER_DEVICE ile override)
    9)  hrm mobile
    10) pms mobile
    11) crm mobile
    12) vault mobile
    13) booking mobile
    14) comm mobile

    q) Quit

MENU
}

if [[ $# -gt 0 ]]; then
  case "$1" in
    backend|platform|platform-backend|api)
      shift
      run_backend "$@"
      ;;
    ui|platform-ui|frontend)
      shift
      run_ui "$@"
      ;;
    vault|pms|crm|hrm|booking|comm)
      key="$1"
      shift
      run_product "$key" "$@"
      ;;
    -h|--help|help)
      sed -n '2,11p' "$0"
      echo ""
      echo "Env (optional, before target):"
      echo "  .env.local | .env.remote | .env.prod"
      echo "  local | remote | prod"
      echo "  --env-file <path>   -e <name>"
      echo ""
      echo "  (No Docker — DB from POSTGIS_* / *_DB_* in the env file.)"
      echo ""
      echo "Examples:"
      echo "  ./run-local.sh .env.remote backend"
      echo "  ./run-local.sh remote vault"
      echo "  ./run-local.sh .env.local"
      echo "  ENV_FILE=\$PWD/../.env.prod ./run-local.sh crm"
      exit 0
      ;;
    *)
      echo "Unknown argument: $1 (try --help)" >&2
      exit 1
      ;;
  esac
fi

require_env_file
print_menu
if [[ ! -t 0 ]]; then
  echo "No TTY — pass a target, e.g. ./run-local.sh .env.remote backend" >&2
  exit 1
fi

while true; do
  printf "Choice [1-14/q]: "
  read -r choice
  case "$choice" in
    1) run_backend ;;
    2) run_ui ;;
    3) run_product vault ;;
    4) run_product pms ;;
    5) run_product crm ;;
    6) run_product hrm ;;
    7) run_product booking ;;
    8) run_product comm ;;
    9)  run_product hrm mobile ;;
    10) run_product pms mobile ;;
    11) run_product crm mobile ;;
    12) run_product vault mobile ;;
    13) run_product booking mobile ;;
    14) run_product comm mobile ;;
    q|Q) exit 0 ;;
    *) echo "Invalid choice." ;;
  esac
done