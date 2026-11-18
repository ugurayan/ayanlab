#!/usr/bin/env bash
# run-apps.sh — AyanLab uygulama başlatıcı
# Kullanım: ./run-apps.sh
#
# 1) Docker altyapısı (Postgres + Redis + Kafka)
# 2) Platform Docker stack
# 3..) Ürün seç → Backend / UI / Mobile (yakında) ayrı
# Seed: platform backend açılınca default-data.json demo kullanıcıları yükler
#
# Host portları: 8080 KULLANILMAZ (platform API = 8102). Loglar: ./logs/

set -uo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_DIR="$ROOT/logs"
mkdir -p "$LOG_DIR"

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

check_env() {
  if [[ ! -f "$ROOT/.env.local" ]]; then
    red ".env.local bulunamadı. Önce: cp .env.local.example .env.local"
    return 1
  fi
}

ensure_node_modules() {
  local ui_dir="$1"
  if [[ ! -d "$ui_dir/node_modules" ]]; then
    blue "node_modules yok — npm install ($ui_dir)..."
    (cd "$ui_dir" && npm install) || { red "npm install başarısız"; return 1; }
  fi
}

port_in_use() {
  lsof -ti tcp:"$1" >/dev/null 2>&1
}

# Portu dinleyen süreçleri (ve mümkünse parent'ı) öldür — run öncesi zorunlu.
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
  # parent (mvn / npm) da yakalanırsa birlikte kapat
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
  # Eski HRM Vite varsayılanı
  if [[ "$p" == "hrm" ]]; then
    kill_port 5173 quiet
  fi
}

# Tüm lokal app portlarını öldür (Docker'a dokunmaz).
kill_all_local() {
  blue "Tüm lokal app'ler kill ediliyor..."
  for p in "${PRODUCTS[@]}"; do
    kill_product "$p"
  done
  # stray Vite / eski portlar
  kill_port 5173 quiet
  green "✔ Tüm lokal app portları temiz"
}

# --- docker ----------------------------------------------------------------

infra_up() {
  check_env || return 1
  blue "Docker altyapısı (Postgres + Redis + Kafka)..."
  docker compose --env-file "$ROOT/.env.local" \
    -f "$ROOT/docker/docker-compose.infra.yml" up -d --build \
    || { red "Infra başlatılamadı"; return 1; }
  green "✔ ayanlab_internal + postgres + redis + kafka"
  docker ps --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}' | grep -E 'ayanlab|NAMES' || true
}

platform_docker() {
  check_env || return 1
  blue "Platform Docker stack (Postgres + Redis + backend :8102 + UI :3102)..."
  ENV=local "$ROOT/scripts/up-platform.sh" || { red "Platform stack başarısız"; return 1; }
  green "✔ Platform API http://localhost:8102 — UI http://localhost:3102"
  yellow "  Demo kullanıcılar backend açılışında default-data.json ile seed edilir."
}

# --- lokal süreçler --------------------------------------------------------

run_backend() {
  local p="$1"
  local dir
  dir="$(product_dir "$p")"
  local port
  port="$(api_port "$p")"
  local log="$LOG_DIR/$p-backend.log"

  [[ -d "$dir" ]] || { red "Klasör yok: $dir"; return 1; }
  [[ -x "$dir/run-local.sh" ]] || { red "run-local.sh yok: $dir"; return 1; }
  check_env || return 1

  # Her çalıştırmadan önce eski süreci öldür
  yellow "Önce kill: $p backend :$port"
  kill_port "$port"

  blue "▶ $p backend :$port  (log: logs/$p-backend.log)"
  if [[ "$p" == "platform" ]]; then
    # platform/run-local.sh menülü; doğrudan scripts/run-backend.sh
    (cd "$dir" && SERVER_PORT="$port" nohup ./scripts/run-backend.sh \
      > "$log" 2>&1 &)
  else
    (cd "$dir" && SERVER_PORT="$port" nohup ./run-local.sh \
      > "$log" 2>&1 &)
  fi
  green "  $p backend → http://localhost:$port"
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
  local log="$LOG_DIR/$p-ui.log"

  [[ -d "$ui_dir" ]] || { red "UI klasörü yok: $ui_dir"; return 1; }
  check_env || return 1

  # Her çalıştırmadan önce eski süreci öldür
  yellow "Önce kill: $p UI :$port"
  kill_port "$port"
  [[ "$p" == "hrm" ]] && kill_port 5173 quiet

  ensure_node_modules "$ui_dir" || return 1
  blue "▶ $p UI :$port  (log: logs/$p-ui.log)"

  # HRM: auth → platform, iş verisi → mock (VITE_USE_MOCKS)
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

  (cd "$ui_dir" && env "${env_extra[@]}" nohup npm run dev -- --port "$port" --host 0.0.0.0 \
    > "$log" 2>&1 &)
  green "  $p UI → http://localhost:$port"
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
    red "flutter bulunamadı — Flutter SDK kurulu değil"
    return 1
  fi
  yellow "Mobile başlatılıyor (flutter run): $mobile_dir"
  (cd "$mobile_dir" && flutter pub get) || { red "flutter pub get başarısız"; return 1; }
  (cd "$mobile_dir" && nohup flutter run > "$ROOT/logs/$p-mobile.log" 2>&1 &
    echo $! > "$ROOT/logs/$p-mobile.pid")
  green "Mobile başlatıldı — log: logs/$p-mobile.log (bağlı cihaz/emülatör gerekir)"
}

run_component() {
  local p="$1"
  local component="$2"
  case "$component" in
    backend) run_backend "$p" ;;
    ui)      run_ui "$p" ;;
    mobile)  run_mobile "$p" ;;
    all)
      run_backend "$p"
      run_ui "$p"
      ;;
    *) red "Bilinmeyen bileşen: $component"; return 1 ;;
  esac
}

# Demo kullanıcı seed: platform backend’i (yeniden) başlat → DefaultDataLoader
seed_demo_users() {
  check_env || return 1
  yellow "Demo kullanıcılar platform backend açılışında default-data.json ile yüklenir."
  yellow "Eksik kullanıcılar oluşturulur; mevcutlar atlanır."
  echo ""
  echo "  HRM (staffiq) — şifre: password"
  echo "    next.is.future@icloud.com   PLATFORM_ADMIN"
  echo "    admin@acme.test             ORG_ADMIN"
  echo "    manager@acme.test           LOCATION_MANAGER"
  echo "    finance@acme.test           ORG_FINANCE"
  echo "    agent@staffing.test         AGENT_USER"
  echo "    notApproved@platform.test   WORKER"
  echo ""
  # run_backend zaten önce kill eder
  run_backend platform
}

# Sadece lokal app'ler (Postgres/Redis/Kafka docker'a dokunmaz)
stop_all_local() {
  kill_all_local
}

# Lokal app'ler + tüm ayanlab-* docker konteynerleri
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
  green "✔ Hepsi kill edildi"
}

# --- alt menü: ürün bileşeni -----------------------------------------------

pick_component() {
  local p="$1"
  local label
  label="$(product_label "$p")"
  echo ""
  blue "── $label ──"
  echo "  1) Backend  (:$(api_port "$p"))  — önce kill, sonra run"
  echo "  2) UI       (:$(ui_port "$p"))  — önce kill, sonra run"
  echo "  3) Mobile   (yakında)"
  echo "  4) Backend + UI"
  echo "  5) Sadece kill (backend + UI)"
  echo "  0) Geri"
  printf "Bileşen: "
  read -r c
  case "$c" in
    1) run_component "$p" backend ;;
    2) run_component "$p" ui ;;
    3) run_component "$p" mobile ;;
    4) run_component "$p" all ;;
    5) kill_product "$p"; green "✔ $label kill edildi" ;;
    0) return 0 ;;
    *) red "Geçersiz"; return 1 ;;
  esac
}

# --- ana menü --------------------------------------------------------------

main_menu() {
  echo ""
  blue "════════════════════════════════════════"
  blue "   AyanLab — Uygulama Başlatıcı"
  blue "════════════════════════════════════════"
  echo "  1) Docker altyapısı (Postgres + Redis + Kafka)"
  echo "  2) Platform — Docker stack (API :8102 + UI :3102)"
  echo "  3) Platform — Lokal (backend / UI / mobile)"
  echo "  4) HRM"
  echo "  5) Vault"
  echo "  6) PMS"
  echo "  7) CRM"
  echo "  8) Booking"
  echo "  9) Comm"
  echo " 10) Demo kullanıcıları seed et (platform backend)"
  echo " 11) Tüm lokal app'leri kill et (Docker'a dokunmaz)"
  echo " 12) Hepsini kill et (lokal + ayanlab Docker)"
  echo "  0) Çıkış"
  echo ""
  printf "Seçimin: "
}

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
