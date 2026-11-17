#!/usr/bin/env bash
mvn_install_libs() {
  local root="${1:-}"
  if [[ -z "$root" ]]; then
    root="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
  fi
  local settings="${root}/.mvn/settings-central.xml"
  mvn -s "${settings}" -f "${root}/ayanlab-platform/pom.xml" install -pl ayanlab-platform-core -am -DskipTests -q
}

mvn_project() {
  local root="${1:-}"
  shift || true
  local settings="${root}/.mvn/settings-central.xml"
  mvn -s "${settings}" "$@"
}