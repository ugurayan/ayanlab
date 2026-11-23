#!/usr/bin/env bash
# Run any command with env from .env.local | .env.remote | .env.prod
# Usage:
#   ./scripts/with-env.sh local docker compose -f docker-compose.platform.yml up -d
#   ./scripts/with-env.sh remote ./ayanlab-platform/run-local.sh backend
#   ./scripts/with-env.sh prod ./scripts/publish-vps.sh

set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
# shellcheck source=lib/resolve-env-file.sh
source "${ROOT}/scripts/lib/resolve-env-file.sh"

if [[ $# -lt 2 ]]; then
  echo "Usage: $0 <local|remote|prod> <command...>" >&2
  exit 1
fi
export ENV="$1"
shift
cd "${ROOT}"
load_env_file
echo "ENV file: ${ENV_FILE}"
exec "$@"