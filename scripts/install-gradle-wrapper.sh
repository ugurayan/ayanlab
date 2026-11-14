#!/usr/bin/env bash
# Copy Gradle wrapper into each *-backend (no repo-root gradlew).
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SRC_WRAPPER="${ROOT}/old/wrapper"
SRC_GRADLE="${ROOT}/old/gradle/wrapper"

if [[ ! -f "${SRC_WRAPPER}/gradle-wrapper.jar" ]]; then
  echo "Missing old/wrapper/gradle-wrapper.jar" >&2
  exit 1
fi

install_one() {
  local dir="$1"
  mkdir -p "${dir}/gradle/wrapper"
  cp "${SRC_WRAPPER}/gradle-wrapper.jar" "${dir}/gradle/wrapper/"
  if [[ -f "${SRC_GRADLE}/gradle-wrapper.properties" ]]; then
    cp "${SRC_GRADLE}/gradle-wrapper.properties" "${dir}/gradle/wrapper/"
  elif [[ -f "${SRC_WRAPPER}/gradle-wrapper.properties" ]]; then
    cp "${SRC_WRAPPER}/gradle-wrapper.properties" "${dir}/gradle/wrapper/"
  fi
  cp "${ROOT}/old/gradlew" "${dir}/gradlew"
  cp "${ROOT}/old/gradlew.bat" "${dir}/gradlew.bat" 2>/dev/null || true
  chmod +x "${dir}/gradlew"
}

for prod in ayanlab-auth ayanlab-core ayanlab-vault ayanlab-hrm ayanlab-pms ayanlab-crm ayanlab-booking ayanlab-comm; do
  install_one "${ROOT}/${prod}/${prod}-backend"
done
echo "Gradle wrapper installed in each *-backend"