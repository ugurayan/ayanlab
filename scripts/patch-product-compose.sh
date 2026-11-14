#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

patch_backend() {
  local compose="$1"
  local backend_dir="$2"
  python3 <<PY
from pathlib import Path
import re
p = Path("${compose}")
text = p.read_text()
# Replace backend build block (first service with -backend in dockerfile path)
new = text
pattern = r'(build:\s*\n\s*context: )\./[^/\n]+-backend(\s*\n\s*dockerfile: Dockerfile)'
repl = r'\1..\\2\n      dockerfile: docker/Dockerfile.backend\n      args:\n        BACKEND_DIR: ${backend_dir}'
if re.search(pattern, text):
    new = re.sub(pattern, f'build:\\n      context: ..\\n      dockerfile: docker/Dockerfile.backend\\n      args:\\n        BACKEND_DIR: {backend_dir}', text, count=1)
    p.write_text(new)
PY
}

for prod in ayanlab-auth ayanlab-core ayanlab-vault ayanlab-hrm ayanlab-pms ayanlab-crm ayanlab-booking ayanlab-comm; do
  compose="${ROOT}/${prod}/docker-compose.yml"
  [[ -f "${compose}" ]] || continue
  backend_dir="${prod}/${prod}-backend"
  # manual sed for reliability
  if grep -q "context: ./${prod}-backend" "${compose}" 2>/dev/null; then
    awk -v bd="${backend_dir}" '
      /context: \.\/'"${prod}"'-backend/ {
        print "      context: .."
        print "      dockerfile: docker/Dockerfile.backend"
        print "      args:"
        print "        BACKEND_DIR: " bd
        skip=1
        next
      }
      skip && /dockerfile: Dockerfile/ { skip=0; next }
      { print }
    ' "${compose}" > "${compose}.tmp" && mv "${compose}.tmp" "${compose}"
  fi
done
echo "compose patched"
PY