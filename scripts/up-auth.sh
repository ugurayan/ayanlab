#!/usr/bin/env bash
# Deprecated name — use ./scripts/up-platform.sh
exec "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/up-platform.sh" "$@"