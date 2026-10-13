#!/bin/bash
# Prepares a Claude Code cloud session for RuneRealm: downloads the game cache and generates the
# RSA keys (`gradlew install`) when they are missing, then compiles the project so the first build
# or test run is fast. The container is cached after this hook, so it only does real work once.
set -euo pipefail

if [ "${CLAUDE_CODE_REMOTE:-}" != "true" ]; then
  exit 0
fi

cd "$CLAUDE_PROJECT_DIR"

if [ ! -d .data/cache/vanilla ] || [ ! -f .data/game.key ] || [ ! -f .data/server.toml ]; then
  ./gradlew install --console=plain -q
fi

./gradlew compileKotlin compileIntegrationKotlin --console=plain -q
