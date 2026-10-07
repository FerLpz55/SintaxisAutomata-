# SintaxisAutomata

Proyecto Java para Eclipse basado en el PDF **Sintaxis.txt** de Lenguajes y Automatas II.

El programa implementa:

- Analizador lexico con los tokens del PDF.
- Analizador sintactico basado en la gramatica de `src/fragmento/compilador/sintaxis.txt`.
- Interfaz Swing para escribir o abrir codigo, analizarlo y ver la tabla de tokens.
- Modo consola para analizar archivos `.lang`.
- Pruebas basicas sin dependencias externas.

## Abrir en Eclipse

1. Abre Eclipse.
2. Ve a `File > Import > General > Existing Projects into Workspace`.
3. Selecciona la carpeta del proyecto.
4. Ejecuta `fragmento.compilador.Main` como `Java Application`.

## Sintaxis aceptada

Las palabras reservadas se escriben en minusculas:

```txt
long double if else while break continue read write
```

Ejemplo:

```txt
long contador = 0;
double total = 12.5;

while (contador < 10) {
    write total;
    contador++;

    if (contador == 5) {
        continue;
    } else {
        total += contador;
    }
}
```

## Ejecutar por consola

Con JDK instalado:

```bash
./build.sh
java -jar dist/CompiladorLenguaje.jar examples/valido.lang
```

En Windows:

```bat
build.bat
java -jar dist\CompiladorLenguaje.jar examples\valido.lang
```

## Archivos importantes

- `src/fragmento/compilador/sintaxis.txt`: gramatica tomada del PDF.
- `src/fragmento/compilador/Lexer.java`: crea tokens.
- `src/fragmento/compilador/Parser.java`: valida la sintaxis.
- `src/fragmento/compilador/VentanaCompilador.java`: interfaz grafica.
- `test/fragmento/compilador/Pruebas.java`: pruebas ejecutables.

## Alcance

El PDF contiene tokens y reglas sintacticas. Por eso el proyecto valida analisis lexico y sintactico. No genera codigo maquina ni ejecuta el programa analizado.
