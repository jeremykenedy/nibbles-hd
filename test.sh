#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
mkdir -p "$ROOT/build/tests"
javac -g -d "$ROOT/build/tests" "$ROOT/src/com/jeremykenedy/nibbleshd/NibblesOptions.java" \
  "$ROOT/src/com/jeremykenedy/nibbleshd/NibblesGame.java" "$ROOT/tests/NibblesTest.java"
java -cp "$ROOT/build/tests" com.jeremykenedy.nibbleshd.NibblesTest
