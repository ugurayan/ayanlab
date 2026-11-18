#!/usr/bin/env bash
# Scaffolds a Flutter mobile app from the golden template.
#
# Usage:
#   scripts/scaffold-mobile.sh <app>        # app: hrm, pms, vault, crm, booking, comm...
#
# Creates ayanlab-<app>/ayanlab-<app>-mobile from scripts/mobile-template,
# replacing the old React Native stub entirely.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TEMPLATE="$ROOT/scripts/mobile-template"

if [[ $# -ne 1 ]]; then
  echo "usage: $0 <app>   (e.g. hrm, pms, vault, crm, booking, comm)" >&2
  exit 1
fi

APP="$1"
TARGET="$ROOT/ayanlab-${APP}/ayanlab-${APP}-mobile"
PKG="ayanlab_${APP//-/_}_mobile"
APP_TITLE="AyanLab $(echo "$APP" | tr '[:lower:]' '[:upper:]')"

if [[ ! -d "$TEMPLATE" ]]; then
  echo "template not found: $TEMPLATE" >&2
  exit 1
fi

echo "==> Scaffolding $TARGET"
rm -rf "$TARGET"
mkdir -p "$(dirname "$TARGET")"

# Copy template (skip generated platform folders if present)
cp -R "$TEMPLATE" "$TARGET"
rm -rf "$TARGET/android" "$TARGET/ios" "$TARGET/build" "$TARGET/.dart_tool"

# Rewrite package name and app title
sed -i '' "s/name: mobile_template/name: ${PKG}/" "$TARGET/pubspec.yaml"
LC_ALL=C find "$TARGET/lib" "$TARGET/test" -name '*.dart' -exec \
  sed -i '' "s/package:mobile_template/package:${PKG}/g" {} +
LC_ALL=C find "$TARGET/lib" -name '*.arb' -exec \
  sed -i '' "s/__APP_NAME__/${APP_TITLE}/g" {} +

# Generated apps commit their platform folders; drop template-only ignores
sed -i '' '/^android\/$/d;/^ios\/$/d' "$TARGET/.gitignore"

# Generate platform folders (android/ios) from the existing pubspec
if command -v flutter >/dev/null 2>&1; then
  (cd "$TARGET" && flutter create . --platforms ios,android --org com.ayanlab --project-name "${PKG}")
  (cd "$TARGET" && flutter pub get)
else
  echo "!! flutter not found: platform folders not generated." >&2
  echo "   Run 'flutter create . --platforms ios,android --org com.ayanlab --project-name ${PKG}' later." >&2
fi

echo "==> Done: $TARGET"
