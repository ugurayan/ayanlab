#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

write_settings() {
  local dir="$1"
  local name="$2"
  local with_security="$3"
  cat > "${dir}/settings.gradle.kts" <<EOF
rootProject.name = "${name}"

include(":libs:ayanlab-core-domain")
project(":libs:ayanlab-core-domain").projectDir = file("../../libs/ayanlab-core-domain")
EOF
  if [[ "${with_security}" == "true" ]]; then
    cat >> "${dir}/settings.gradle.kts" <<EOF

include(":libs:ayanlab-security")
project(":libs:ayanlab-security").projectDir = file("../../libs/ayanlab-security")
EOF
  fi
}

# Core — platform admin API, no JWT filter required on all routes (add security later)
write_settings "${ROOT}/ayanlab-core/ayanlab-core-backend" "ayanlab-core-backend" false

# Auth
write_settings "${ROOT}/ayanlab-auth/ayanlab-auth-backend" "ayanlab-auth-backend" true

for pair in \
  "ayanlab-vault:ayanlab-vault-backend" \
  "ayanlab-pms:ayanlab-pms-backend" \
  "ayanlab-crm:ayanlab-crm-backend" \
  "ayanlab-hrm:ayanlab-hrm-backend" \
  "ayanlab-booking:ayanlab-booking-backend" \
  "ayanlab-comm:ayanlab-comm-backend"; do
  IFS=: read -r prod name <<< "${pair}"
  write_settings "${ROOT}/${prod}/${name}" "${name}" true
done

echo "settings.gradle.kts written"