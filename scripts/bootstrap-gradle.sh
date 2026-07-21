#!/usr/bin/env bash
set -euo pipefail
VERSION=9.5.0
DEST="${HOME}/.local/gradle-${VERSION}"
if [[ ! -x "$DEST/bin/gradle" ]]; then
  TMP="$(mktemp -d)"; trap 'rm -rf "$TMP"' EXIT
  curl -fL "https://services.gradle.org/distributions/gradle-${VERSION}-bin.zip" -o "$TMP/gradle.zip"
  unzip -q "$TMP/gradle.zip" -d "${HOME}/.local"
fi
export PATH="$DEST/bin:$PATH"
gradle --version
