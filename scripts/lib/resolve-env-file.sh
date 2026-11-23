#!/usr/bin/env bash
# Resolve .env.local | .env.remote | .env.prod from ENV or ENV_FILE.
resolve_env_file() {
  if [[ -n "${ENV_FILE:-}" ]]; then
    echo "${ENV_FILE}"
    return 0
  fi
  case "${ENV:-local}" in
    local|dev) echo ".env.local" ;;
    remote) echo ".env.remote" ;;
    prod|production) echo ".env.prod" ;;
    *)
      echo "Unknown ENV=${ENV} (use local, remote, or prod)" >&2
      return 1
      ;;
  esac
}

# CLI / launcher: .env.local | local | absolute path → export ENV + ENV_FILE
apply_env_spec() {
  local spec="${1:?env spec}"
  local search_root="${2:-}"
  local file base

  case "${spec}" in
    local|dev) spec=".env.local" ;;
    remote) spec=".env.remote" ;;
    prod|production) spec=".env.prod" ;;
  esac

  if [[ "${spec}" == /* ]]; then
    file="${spec}"
  elif [[ "${spec}" == .env.* ]]; then
    if [[ -n "${search_root}" ]]; then
      file="${search_root}/${spec}"
    else
      file="${spec}"
    fi
  else
    echo "Unknown env spec: ${spec} (use .env.local, .env.remote, .env.prod, or local|remote|prod)" >&2
    return 1
  fi

  base="$(basename "${file}")"
  case "${base}" in
    .env.local) export ENV=local ;;
    .env.remote) export ENV=remote ;;
    .env.prod) export ENV=prod ;;
    *)
      export ENV="${ENV:-local}"
      ;;
  esac
  export ENV_FILE="${file}"
}

load_env_file() {
  local file search_root="${1:-}"
  file="$(resolve_env_file)" || return 1
  if [[ ! -f "${file}" && -n "${search_root}" && -f "${search_root}/${file}" ]]; then
    file="${search_root}/${file}"
  fi
  if [[ ! -f "${file}" ]]; then
    local hint="${file}"
    if [[ -n "${search_root}" ]]; then
      hint="${search_root}/${file}"
    fi
    echo "Missing ${hint} (ENV=${ENV:-local}) — cp .env.local.example .env.local at repo root" >&2
    return 1
  fi
  set -a
  # shellcheck disable=SC1090
  source "${file}"
  set +a
  export ENV_FILE="${file}"
}