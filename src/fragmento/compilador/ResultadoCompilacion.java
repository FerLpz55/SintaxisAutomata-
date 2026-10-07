package fragmento.compilador;

import java.util.List;

public final class ResultadoCompilacion {
    private final List<Token> tokens;
    private final String mensaje;

    public ResultadoCompilacion(List<Token> tokens, String mensaje) {
        this.tokens = List.copyOf(tokens);
        this.mensaje = mensaje;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public String getMensaje() {
        return mensaje;
    }
}
