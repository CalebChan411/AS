#!/bin/sh
set -eu
cd "$(dirname "$0")"
mkdir -p build
"${JAVAC:-javac}" -d build ./*.java
exec java -cp build Main
