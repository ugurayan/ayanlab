# shellcheck shell=bash
# Interactive zsh often has `alias docker=podman`; bash scripts do not. Shim only when docker binary is missing.
if ! command -v docker >/dev/null 2>&1; then
  if command -v podman >/dev/null 2>&1; then
    docker() {
      podman "$@"
    }
  else
    docker() {
      echo "docker: not found (install Docker or Podman, or add alias docker=podman in your shell)" >&2
      return 127
    }
  fi
fi