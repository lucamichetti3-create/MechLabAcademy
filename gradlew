#!/usr/bin/env sh
set -eu
if command -v gradle >/dev/null 2>&1; then exec gradle "$@"; fi
cat >&2 <<'EOF'
Gradle non è installato nel PATH e questo archivio sorgente non include binari.
Esegui ./scripts/bootstrap-gradle.sh oppure apri il progetto in Android Studio.
La CI usa gradle/actions/setup-gradle con Gradle 9.5.0.
EOF
exit 1
