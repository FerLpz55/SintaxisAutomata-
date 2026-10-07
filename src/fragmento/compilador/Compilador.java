package fragmento.compilador;

import java.util.List;

public final class Compilador {
    public ResultadoCompilacion compilar(String fuente) {
        List<Token> tokens = new Lexer(fuente).analizar();
        new Parser(tokens).analizar();
        return new ResultadoCompilacion(tokens, "Analisis lexico y sintactico correcto");
    }
}
