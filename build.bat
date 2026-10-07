@echo off
setlocal

if exist bin rmdir /s /q bin
if exist dist rmdir /s /q dist
mkdir bin
mkdir dist

for /r src %%f in (*.java) do echo %%f>> fuentes.txt
for /r test %%f in (*.java) do echo %%f>> fuentes.txt
javac -encoding UTF-8 -d bin @fuentes.txt
java -cp bin fragmento.compilador.Pruebas
jar --create --file dist\CompiladorLenguaje.jar --main-class fragmento.compilador.Main -C bin .
del fuentes.txt
