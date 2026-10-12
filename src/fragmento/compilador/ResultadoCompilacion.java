package fragmento.compilador;

import java.util.List;

public final class ResultadoCompilacion {
    private final List<Token> tokens;
    private final String mensaje;
    private final ArbolSintactico arbol;

    public ResultadoCompilacion(List<Token> tokens, String mensaje) {
        this(tokens, mensaje, null);
    }

    public ResultadoCompilacion(List<Token> tokens, String mensaje, ArbolSintactico arbol) {
        this.tokens = List.copyOf(tokens);
        this.mensaje = mensaje;
        this.arbol = arbol;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public String getMensaje() {
        return mensaje;
    }

    public ArbolSintactico getArbol() {
        return arbol;
    }
}
