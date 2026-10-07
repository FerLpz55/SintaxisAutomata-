package fragmento.compilador;

public final class Token {
    private String tipo;
    private String token;
    private int indiceFila;
    private int indiceComienzo;

    public Token(String token, int indiceFila, int indiceComienzo) {
        this.token = token;
        this.indiceFila = indiceFila;
        this.indiceComienzo = indiceComienzo;
    }

    public Token(TokenType tipo, String lexema, int linea, int columna) {
        this(lexema, linea, columna);
        this.tipo = tipo.name();
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public TokenType getTipoToken() {
        return TokenType.valueOf(tipo);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getIndiceFila() {
        return indiceFila;
    }

    public void setIndiceFila(int indiceFila) {
        this.indiceFila = indiceFila;
    }

    public int getIndiceComienzo() {
        return indiceComienzo;
    }

    public void setIndiceComienzo(int indiceComienzo) {
        this.indiceComienzo = indiceComienzo;
    }

    public String getLexema() {
        return getToken();
    }

    public int getLinea() {
        return getIndiceFila();
    }

    public int getColumna() {
        return getIndiceComienzo();
    }

    @Override
    public String toString() {
        return tipo + "('" + token + "') [" + indiceFila + ":" + indiceComienzo + "]";
    }
}
