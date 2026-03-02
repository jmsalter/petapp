#!/bin/sh
# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup batch script, version 3.3.2
# ----------------------------------------------------------------------------

set -euf
[ "${MVNW_VERBOSE-}" != debug ] || set -x

case "$(uname)" in
CYGWIN* | MINGW* | MSYS*)
  [ -z "${JAVA_HOME-}" ] || JAVA_HOME="$(cygpath --unix "$JAVA_HOME")"
  ;;
esac

if [ -z "${JAVA_HOME-}" ]; then
  JAVACMD="$(command -v java || true)"
else
  JAVACMD="$JAVA_HOME/bin/java"
fi

if [ ! -x "${JAVACMD-}" ]; then
  echo "Error: JAVA_HOME is not defined correctly." >&2
  echo "  Cannot execute: ${JAVACMD-}" >&2
  exit 1
fi

BASE_DIR="$(CDPATH='' cd -- "$(dirname -- "$0")" && pwd)"
WRAPPER_PROPERTIES="$BASE_DIR/.mvn/wrapper/maven-wrapper.properties"

if [ ! -f "$WRAPPER_PROPERTIES" ]; then
  echo "Error: $WRAPPER_PROPERTIES is missing." >&2
  exit 1
fi

while IFS='=' read -r key value; do
  case "$key" in
    distributionUrl) DISTRIBUTION_URL="$value" ;;
    distributionType) DISTRIBUTION_TYPE="$value" ;;
  esac
done < "$WRAPPER_PROPERTIES"

MAVEN_USER_HOME="${MAVEN_USER_HOME:-${HOME}/.m2}"
WRAPPER_DISTS="$MAVEN_USER_HOME/wrapper/dists"
mkdir -p "$WRAPPER_DISTS"

DIST_NAME="$(basename "$DISTRIBUTION_URL")"
DIST_DIR="$WRAPPER_DISTS/${DIST_NAME%.zip}"
MAVEN_HOME="$DIST_DIR/apache-maven-3.9.9"

download() {
  target="$1"
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$DISTRIBUTION_URL" -o "$target"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$DISTRIBUTION_URL" -O "$target"
  else
    echo "Error: neither curl nor wget is installed, cannot download Maven." >&2
    exit 1
  fi
}

if [ ! -x "$MAVEN_HOME/bin/mvn" ]; then
  mkdir -p "$DIST_DIR"
  TMP_ZIP="$DIST_DIR/$DIST_NAME"
  if [ ! -f "$TMP_ZIP" ]; then
    echo "Downloading Maven from $DISTRIBUTION_URL"
    download "$TMP_ZIP"
  fi
  echo "Extracting Maven to $DIST_DIR"
  unzip -oq "$TMP_ZIP" -d "$DIST_DIR"
fi

exec "$MAVEN_HOME/bin/mvn" "$@"
