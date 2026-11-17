#!/usr/bin/env bash
# Resolve .env.local | .env.prod from ENV or ENV_FILE.
resolve_env_file() {
  if [[ -n "${ENV_FILE:-}" ]]; then
    echo "${ENV_FILE}"
    return 0
  fi
  case "${ENV:-local}" in
    local|dev) echo ".env.local" ;;
    prod|production) echo ".env.prod" ;;
    *)
      echo "Unknown ENV=${ENV} (use local or prod)" >&2
      return 1
      ;;
  esac
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