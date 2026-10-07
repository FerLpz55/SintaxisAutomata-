package fragmento.compilador;

public class CompilacionException extends RuntimeException {
    private final String etapa;
    private final int linea;
    private final int columna;

    public CompilacionException(String etapa, String mensaje, int linea, int columna) {
        super(etapa + " en linea " + linea + ", columna " + columna + ": " + mensaje);
        this.etapa = etapa;
        this.linea = linea;
        this.columna = columna;
    }

    public String getEtapa() {
        return etapa;
    }

    public int getLinea() {
        return linea;
    }

    public int getColumna() {
        return columna;
    }
}
