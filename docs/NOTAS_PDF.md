# Notas de transcripcion del PDF

El archivo `src/fragmento/compilador/sintaxis.txt` contiene la gramatica del PDF de la tarea.

Se hizo una correccion minima de puntuacion: en la captura aparece `ESCRIBIR ::= WRITE TERMINO` sin punto y coma, pero todas las demas reglas terminan con `;`. En el proyecto se dejo como `ESCRIBIR ::= WRITE TERMINO ;` para que la gramatica quede completa.

El PDF define tokens y sintaxis. No incluye reglas semanticas como tabla de simbolos, validacion de variables declaradas o generacion de codigo objeto, asi que este proyecto implementa analisis lexico y sintactico.
