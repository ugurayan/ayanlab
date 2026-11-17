#!/usr/bin/env bash
# Interactive launcher for platform + product backends / platform UI.
# Usage:
#   ./run-local.sh              → menu
#   ./run-local.sh backend      → platform JVM
#   ./run-local.sh ui           → React admin UI
#   ./run-local.sh vault        → delegate to ayanlab-vault/run-local.sh
set -euo pipefail
HERE="$(cd "$(dirname "$0")" && pwd)"
ROOT="$(cd "$HERE/.." && pwd)"
export ENV="${ENV:-local}"

run_backend() {
  exec "$HERE/scripts/run-backend.sh" "$@"
}

run_ui() {
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
  echo "Delegating to $name ($dir/run-local.sh)"
  exec "$ROOT/$dir/run-local.sh" "$@"
}

print_menu() {
  cat <<'MENU'

AyanLab — local run (ENV=local)

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
      sed -n '2,8p' "$0"
      echo ""
      echo "Examples:"
      echo "  ENV=local ./run-local.sh"
      echo "  ENV=local ./run-local.sh backend"
      echo "  ENV=local ./run-local.sh ui"
      echo "  ENV=local ./run-local.sh vault"
      exit 0
      ;;
    *)
      echo "Unknown argument: $1 (try --help)" >&2
      exit 1
      ;;
  esac
fi

print_menu
if [[ ! -t 0 ]]; then
  echo "No TTY — pass a target, e.g. ./run-local.sh backend" >&2
  exit 1
fi

while true; do
  printf "Choice [1-8/q]: "
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
    q|Q) exit 0 ;;
    *) echo "Invalid choice." ;;
  esac
done