package fragmento.compilador;

public class Token {
    private int indiceFila;
    private int indiceComienzo;
    private String token;
    private String tipo;

    public Token(String token, int indiceFila, int indiceComienzo) {
        this.indiceFila = indiceFila;
        this.indiceComienzo = indiceComienzo;
        this.token = token;
    }

    public Token(Tokens tipo, String lexema, int linea, int columna) {
        this(lexema, linea, columna);
        this.tipo = tipo.name();
    }

    /**
     * Regresa el tipo de token terminal: IDENTIFICADOR, NUMERO_LONG,
     * NUMERO_DOUBLE, IF, ELSE, entre otros.
     *
     * @return tipo del token
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Define el tipo de token terminal.
     *
     * @param tipo nombre del tipo de token
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Tokens getTipoToken() {
        return Tokens.valueOf(tipo);
    }

    /**
     * Devuelve el texto concreto del token, por ejemplo: if, else, while,
     * ++, double, 3.14 o un identificador.
     *
     * @return texto del token
     */
    public String getToken() {
        return token;
    }

    /**
     * Establece el texto concreto del token.
     *
     * @param token texto del token
     */
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
