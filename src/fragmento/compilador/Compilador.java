package fragmento.compilador;

import java.util.List;

public final class Compilador {
    public ResultadoCompilacion compilar(String fuente) {
        List<Token> tokens = new Lexer(fuente).analizar();
        Parser parser = new Parser(tokens);
        parser.analizar();
        return new ResultadoCompilacion(tokens, "Analisis lexico y sintactico correcto", parser.getArbol());
    }
}
