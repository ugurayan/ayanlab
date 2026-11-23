#!/usr/bin/env bash
# =============================================================================
# run-apps.sh — AyanLab uygulama başlatıcı (repo kökünden)
# =============================================================================
#
# Ortam dosyası (.env) hangi Postgres’e gideceğini belirler:
#   .env.local   → genelde docker localhost:5433, ayrı DB’ler (ayanlab_platform, …)
#   .env.remote  → VPS (REMOTE_HOST / POSTGIS_*, tek DB ayanlab + şemalar)
#   .env.prod    → üretim değerleri (aynı mekanizma)
#
# Dosyalar repo kökünde; git’e girmez. Şablon: cp .env.local.example .env.local
# Remote şema/rol: ./scripts/01.remote_db_initialization.sh  (ilk kurulum)
#
# --- Nasıl çalıştırılır -----------------------------------------
#
# 1) İnteraktif menü (env + seçimler):
#      ./run-apps.sh
#      ./run-apps.sh .env.local
#      ./run-apps.sh .env.remote
#      ./run-apps.sh remote          # kısa → .env.remote
#      ./run-apps.sh local           # kısa → .env.local
#
# 2) Menüsüz — tek komut (ön plan, log bu terminalde; Ctrl+C ile dur):
#      ./run-apps.sh .env.remote platform backend
#      ./run-apps.sh .env.local platform ui
#      ./run-apps.sh .env.remote hrm backend
#      ./run-apps.sh .env.remote hrm ui
#      ./run-apps.sh remote vault ui
#
# 3) Özel env yolu:
#      ./run-apps.sh --env-file /path/to/.env.prod crm backend
#      ./run-apps.sh -e remote hrm ui
#
# 4) Yardım:
#      ./run-apps.sh --help
#
# Ürün adları: platform | hrm | vault | pms | crm | booking | comm
# Bileşenler:   backend | ui | mobile
#
# Menüde 1–2 Docker (infra / platform stack); 3–9 lokal JVM veya UI.
# Backend’ler: ENV + ENV_FILE export → run-local.sh / Spring (Docker beklemez).
#
# --- Portlar (host) ---------------------------------------------
#   platform  API 8102   UI 3102
#   hrm       API 8104   UI 3104
#   vault     API 8103   UI 3103
#   pms       API 8105   UI 3105
#   crm       API 8106   UI 3106
#   booking   API 8107   UI 3107
#   comm      API 8108   UI 3108
#   (8080 kullanılmaz)
#
# HRM UI: auth proxy → localhost:8102, API → localhost:8104/hrm/api
#        (önce platform + hrm backend çalışıyor olmalı)
#
# Ortam değişkeni ile (CLI argümanı yerine):
#   ENV_FILE=$PWD/.env.remote ./run-apps.sh hrm ui
#
# =============================================================================

set -uo pipefail

print_usage() {
  sed -n '3,/^set -uo/p' "$0" | sed '/^set -uo/d' | sed 's/^# \{0,1\}//'
}

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_DIR="$ROOT/logs"
mkdir -p "$LOG_DIR"

# shellcheck source=scripts/lib/resolve-env-file.sh
source "$ROOT/scripts/lib/resolve-env-file.sh"

CLI_PRODUCT=""
CLI_COMPONENT=""

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
    -h|--help)
      print_usage
      exit 0
      ;;
    *)
      lower="$(printf '%s' "$1" | tr '[:upper:]' '[:lower:]')"
      case "$lower" in
        platform|hrm|vault|pms|crm|booking|comm)
          CLI_PRODUCT="$lower"
          shift
          ;;
        backend|ui|mobile)
          CLI_COMPONENT="$lower"
          shift
          ;;
        *)
          echo "Unknown argument: $1 (try --help)" >&2
          exit 1
          ;;
      esac
      ;;
  esac
done

if [[ -z "${ENV_FILE:-}" ]]; then
  export ENV="${ENV:-local}"
  ENV_FILE="${ROOT}/$(resolve_env_file)"
  export ENV_FILE
fi

PRODUCTS=(platform hrm vault pms crm booking comm)

# --- portlar (host; asla 8080 yok) ----------------------------------------

ui_port() {
  case "$1" in
    platform) echo 3102 ;;
    hrm)      echo 3104 ;;
    vault)    echo 3103 ;;
    pms)      echo 3105 ;;
    crm)      echo 3106 ;;
    booking)  echo 3107 ;;
    comm)     echo 3108 ;;
    *)        echo 0 ;;
  esac
}

api_port() {
  case "$1" in
    platform) echo 8102 ;;
    hrm)      echo 8104 ;;
    vault)    echo 8103 ;;
    pms)      echo 8105 ;;
    crm)      echo 8106 ;;
    booking)  echo 8107 ;;
    comm)     echo 8108 ;;
    *)        echo 0 ;;
  esac
}

product_dir() {
  echo "$ROOT/ayanlab-$1"
}

product_label() {
  echo "$1" | tr '[:lower:]' '[:upper:]'
}

# --- yardımcılar -----------------------------------------------------------

blue()  { printf "\033[1;34m%s\033[0m\n" "$*"; }
green() { printf "\033[1;32m%s\033[0m\n" "$*"; }
red()   { printf "\033[1;31m%s\033[0m\n" "$*"; }
yellow(){ printf "\033[1;33m%s\033[0m\n" "$*"; }

require_env_file() {
  if [[ ! -f "${ENV_FILE}" ]]; then
    red "Env dosyası yok: ${ENV_FILE}"
    red "  cp .env.local.example .env.local  veya  .env.remote oluştur"
    return 1
  fi
}

show_db_hint() {
  # shellcheck disable=SC1090
  set -a
  # shellcheck source=/dev/null
  source "${ENV_FILE}"
  set +a
  local host="${POSTGIS_HOST:-localhost}"
  local port="${POSTGIS_PORT:-5432}"
  if [[ "${ENV:-}" == "remote" && -n "${REMOTE_HOST:-}" ]]; then
    case "${host}" in
      localhost|127.0.0.1|postgres|"") host="${REMOTE_HOST}"; port="${REMOTE_HOST_PORT:-5432}" ;;
    esac
  fi
  yellow "  env: ${ENV_FILE}  (ENV=${ENV})"
  yellow "  DB örnek: ${host}:${port} (platform → PLATFORM_DB_* / POSTGIS_*)"
}

ensure_node_modules() {
  local ui_dir="$1"
  if [[ ! -d "$ui_dir/node_modules" ]]; then
    blue "node_modules yok — npm install ($ui_dir)..."
    (cd "$ui_dir" && npm install) || { red "npm install başarısız"; return 1; }
  fi
}

kill_port() {
  local port="$1"
  local quiet="${2:-}"
  local pids parents
  pids="$(lsof -ti tcp:"$port" -sTCP:LISTEN 2>/dev/null || true)"
  [[ -z "$pids" ]] && pids="$(lsof -ti tcp:"$port" 2>/dev/null || true)"
  if [[ -z "$pids" ]]; then
    return 0
  fi
  [[ -z "$quiet" ]] && yellow "  kill :$port → PID $pids"
  parents=""
  for pid in $pids; do
    local pp
    pp="$(ps -o ppid= -p "$pid" 2>/dev/null | tr -d ' ' || true)"
    if [[ -n "$pp" && "$pp" != "1" && "$pp" != "$$" ]]; then
      parents="$parents $pp"
    fi
  done
  # shellcheck disable=SC2086
  kill $pids $parents 2>/dev/null || true
  sleep 1
  pids="$(lsof -ti tcp:"$port" 2>/dev/null || true)"
  if [[ -n "$pids" ]]; then
    # shellcheck disable=SC2086
    kill -9 $pids 2>/dev/null || true
    sleep 0.5
  fi
}

kill_product() {
  local p="$1"
  yellow "Kill $p (api :$(api_port "$p") + ui :$(ui_port "$p"))"
  kill_port "$(api_port "$p")"
  kill_port "$(ui_port "$p")"
  if [[ "$p" == "hrm" ]]; then
    kill_port 5173 quiet
  fi
}

kill_all_local() {
  blue "Tüm lokal app'ler kill ediliyor..."
  for p in "${PRODUCTS[@]}"; do
    kill_product "$p"
  done
  kill_port 5173 quiet
  green "✔ Tüm lokal app portları temiz"
}

# --- docker (env dosyası ile) ----------------------------------------------

infra_up() {
  require_env_file || return 1
  blue "Docker altyapısı (Postgres + Redis + Kafka)..."
  docker compose --env-file "${ENV_FILE}" \
    -f "$ROOT/docker/docker-compose.infra.yml" up -d --build \
    || { red "Infra başlatılamadı"; return 1; }
  green "✔ ayanlab_internal + postgres + redis + kafka"
  docker ps --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}' | grep -E 'ayanlab|NAMES' || true
}

platform_docker() {
  require_env_file || return 1
  blue "Platform Docker stack..."
  export ENV_FILE
  ENV="${ENV:-local}" "$ROOT/scripts/up-platform.sh" || { red "Platform stack başarısız"; return 1; }
  green "✔ Platform API http://localhost:8102 — UI http://localhost:3102"
}

# --- lokal süreçler (ön plan — çıktı bu terminalde) ------------------------

run_backend() {
  local p="$1"
  local dir
  dir="$(product_dir "$p")"
  local port
  port="$(api_port "$p")"

  [[ -d "$dir" ]] || { red "Klasör yok: $dir"; return 1; }
  require_env_file || return 1

  yellow "Önce kill: $p backend :$port"
  kill_port "$port"

  blue "▶ $p backend :$port  (ön plan — Ctrl+C ile dur)"
  show_db_hint

  export ENV ENV_FILE
  if [[ "$p" == "platform" ]]; then
    [[ -x "$dir/scripts/run-backend.sh" ]] || { red "run-backend.sh yok"; return 1; }
    (cd "$dir" && SERVER_PORT="$port" exec ./scripts/run-backend.sh)
  else
    [[ -x "$dir/run-local.sh" ]] || { red "run-local.sh yok: $dir"; return 1; }
    (cd "$dir" && SERVER_PORT="$port" exec ./run-local.sh)
  fi
}

run_ui() {
  local p="$1"
  local dir
  dir="$(product_dir "$p")"
  local ui_dir="$dir/ayanlab-$p-ui"
  local port
  port="$(ui_port "$p")"
  local api
  api="$(api_port "$p")"
  local auth_api
  auth_api="$(api_port platform)"

  [[ -d "$ui_dir" ]] || { red "UI klasörü yok: $ui_dir"; return 1; }
  require_env_file || return 1

  yellow "Önce kill: $p UI :$port"
  kill_port "$port"
  [[ "$p" == "hrm" ]] && kill_port 5173 quiet

  ensure_node_modules "$ui_dir" || return 1
  blue "▶ $p UI :$port  (ön plan — Ctrl+C ile dur)"
  show_db_hint

  local env_extra=()
  if [[ "$p" == "hrm" ]]; then
    env_extra+=(
      "VITE_AUTH_API_BASE_URL="
      "VITE_PROXY_TARGET=http://localhost:${auth_api}"
      "VITE_API_BASE_URL=http://localhost:${api}/hrm/api"
      "VITE_APP_ID=f01ae844-85fd-48a0-90ed-8bdd7e0e48a6"
      "VITE_APP_KEY=staffiq"
      "VITE_USE_MOCKS=true"
    )
  elif [[ "$p" == "platform" ]]; then
    env_extra+=("VITE_PROXY_TARGET=http://localhost:${api}")
  else
    env_extra+=("VITE_API_BASE_URL=http://localhost:${api}")
  fi

  # macOS env(1) has no "exec" subcommand — run npm directly.
  (cd "$ui_dir" && env "${env_extra[@]}" npm run dev -- --port "$port" --host 0.0.0.0)
}

run_mobile() {
  local p="$1"
  local mobile_dir
  mobile_dir="$(product_dir "$p")/ayanlab-$p-mobile"
  if [[ ! -d "$mobile_dir" ]]; then
    yellow "Mobile klasörü yok: $mobile_dir"
    return 1
  fi
  if ! command -v flutter >/dev/null 2>&1; then
    red "flutter bulunamadı"
    return 1
  fi
  require_env_file || return 1
  blue "▶ $p mobile (ön plan — Ctrl+C ile dur)"
  (cd "$mobile_dir" && flutter pub get) || return 1
  (cd "$mobile_dir" && exec flutter run -d "${FLUTTER_DEVICE:-macos}")
}

run_component() {
  local p="$1"
  local component="$2"
  case "$component" in
    backend) run_backend "$p" ;;
    ui)      run_ui "$p" ;;
    mobile)  run_mobile "$p" ;;
    *) red "Bilinmeyen bileşen: $component"; return 1 ;;
  esac
}

seed_demo_users() {
  require_env_file || return 1
  yellow "Platform backend yeniden başlatılır → default-data.json seed"
  echo ""
  echo "  HRM (staffiq) — şifre: password"
  echo "    next.is.future@icloud.com   PLATFORM_ADMIN"
  echo "    admin@acme.test             ORG_ADMIN"
  echo ""
  run_backend platform
}

stop_all_local() {
  kill_all_local
}

stop_all() {
  kill_all_local
  blue "ayanlab-* Docker konteynerleri durduruluyor..."
  local containers
  containers="$(docker ps --format '{{.Names}}' 2>/dev/null | grep -E '^ayanlab-' || true)"
  if [[ -n "$containers" ]]; then
    echo "$containers" | xargs docker stop >/dev/null 2>&1
    green "✔ Docker konteynerleri durduruldu"
  else
    yellow "  Çalışan ayanlab-* konteyner yok"
  fi
}

pick_component() {
  local p="$1"
  local label
  label="$(product_label "$p")"
  echo ""
  blue "── $label ──"
  show_db_hint
  echo "  1) Backend  (:$(api_port "$p"))  — ön plan"
  echo "  2) UI       (:$(ui_port "$p"))  — ön plan"
  echo "  3) Mobile   — ön plan"
  echo "  4) Sadece kill (backend + UI portları)"
  echo "  0) Geri"
  printf "Bileşen (tek seçim): "
  read -r c
  case "$c" in
    1) run_component "$p" backend ;;
    2) run_component "$p" ui ;;
    3) run_component "$p" mobile ;;
    4) kill_product "$p"; green "✔ $label kill edildi" ;;
    0) return 0 ;;
    *) red "Geçersiz"; return 1 ;;
  esac
}

main_menu() {
  echo ""
  blue "════════════════════════════════════════"
  blue "   AyanLab — Uygulama Başlatıcı"
  blue "════════════════════════════════════════"
  show_db_hint
  echo ""
  echo "  1) Docker altyapısı (Postgres + Redis + Kafka)"
  echo "  2) Platform — Docker stack (API :8102 + UI :3102)"
  echo "  3) Platform — Lokal (backend / UI / mobile, ön plan)"
  echo "  4) HRM"
  echo "  5) Vault"
  echo "  6) PMS"
  echo "  7) CRM"
  echo "  8) Booking"
  echo "  9) Comm"
  echo " 10) Demo kullanıcıları seed (platform backend, ön plan)"
  echo " 11) Tüm lokal app'leri kill et"
  echo " 12) Hepsini kill et (lokal + Docker)"
  echo "  0) Çıkış"
  echo ""
  printf "Seçimin: "
}

if [[ ! -f "${ENV_FILE}" ]]; then
  red "Env dosyası yok: ${ENV_FILE}"
  red "Örnek: ./run-apps.sh .env.local   veya   cp .env.local.example .env.local"
  exit 1
fi

if [[ -n "${CLI_PRODUCT}" && -n "${CLI_COMPONENT}" ]]; then
  run_component "$CLI_PRODUCT" "$CLI_COMPONENT"
  exit $?
fi

if [[ -n "${CLI_PRODUCT}" ]]; then
  pick_component "$CLI_PRODUCT"
  exit $?
fi

while true; do
  main_menu
  read -r choice
  case "$choice" in
    1)  infra_up ;;
    2)  platform_docker ;;
    3)  pick_component platform ;;
    4)  pick_component hrm ;;
    5)  pick_component vault ;;
    6)  pick_component pms ;;
    7)  pick_component crm ;;
    8)  pick_component booking ;;
    9)  pick_component comm ;;
    10) seed_demo_users ;;
    11) stop_all_local ;;
    12) stop_all ;;
    0)  green "Görüşürüz!"; exit 0 ;;
    *)  red "Geçersiz seçim: $choice" ;;
  esac
done