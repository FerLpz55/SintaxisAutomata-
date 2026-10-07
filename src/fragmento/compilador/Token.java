package fragmento.compilador;

public final class Token {
    private final TokenType tipo;
    private final String lexema;
    private final int linea;
    private final int columna;

    public Token(TokenType tipo, String lexema, int linea, int columna) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.linea = linea;
        this.columna = columna;
    }

    public TokenType getTipo() {
        return tipo;
    }

    public String getLexema() {
        return lexema;
    }

    public int getLinea() {
        return linea;
    }

    public int getColumna() {
        return columna;
    }

    @Override
    public String toString() {
        return tipo + "('" + lexema + "') [" + linea + ":" + columna + "]";
    }
}
