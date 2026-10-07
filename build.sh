#!/usr/bin/env bash
set -euo pipefail

rm -rf bin dist
mkdir -p bin dist
java com.sun.tools.javac.Main -encoding UTF-8 -d bin $(find src test -name '*.java' | sort)
java -cp bin fragmento.compilador.Pruebas
if command -v jar >/dev/null 2>&1; then
    jar --create --file dist/CompiladorLenguaje.jar --main-class fragmento.compilador.Main -C bin .
else
    java -m jdk.jartool/sun.tools.jar.Main --create --file dist/CompiladorLenguaje.jar --main-class fragmento.compilador.Main -C bin .
fi
