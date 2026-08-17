#!/usr/bin/env bash
set -euo pipefail

JAVA_CANDIDATE="8.0.502-zulu"
SDKMAN_DIR="${SDKMAN_DIR:-$HOME/.sdkman}"
JAVA_HOME_CANDIDATE="${SDKMAN_DIR}/candidates/java/${JAVA_CANDIDATE}"

if [ ! -d "${JAVA_HOME_CANDIDATE}" ]; then
  echo "Java ${JAVA_CANDIDATE} is not installed in ${SDKMAN_DIR}." >&2
  echo "Run: source ~/.sdkman/bin/sdkman-init.sh && sdk install java ${JAVA_CANDIDATE}" >&2
  exit 1
fi

export JAVA_HOME="${JAVA_HOME_CANDIDATE}"
export PATH="${JAVA_HOME}/bin:${PATH}"

exec mvn "$@"
