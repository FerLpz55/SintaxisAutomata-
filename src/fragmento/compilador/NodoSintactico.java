package fragmento.compilador;

import java.util.ArrayList;

public class NodoSintactico {
    private final String nombre;
    private ArrayList<NodoSintactico> derivados;
    private final Token token;
    private final boolean esTerminal;

    /** Usar solo en caso de que sea terminal. */
    public NodoSintactico(Token token) {
        this.nombre = token.getTipo();
        this.token = token;
        derivados = new ArrayList<>();
        esTerminal = true;
    }

    /** Usar solo en caso de que sea no terminal. */
    public NodoSintactico(String nombre) {
        this.nombre = nombre;
        token = null;
        derivados = new ArrayList<>();
        esTerminal = false;
    }

    /** Reemplaza todos los derivados anteriores. */
    public void setDerivados(ArrayList<NodoSintactico> derivados) {
        this.derivados = derivados;
    }

    /** Agrega un derivado al final de la lista. */
    public void agregarDerivado(NodoSintactico derivado) {
        derivados.add(derivado);
    }

    public ArrayList<NodoSintactico> getDerivados() {
        return derivados;
    }

    public NodoSintactico getDerivado(int index) {
        return derivados.get(index);
    }

    /** Devuelve los derivados directos que tienen el nombre indicado. */
    public ArrayList<NodoSintactico> getDerivado(String nombre) {
        ArrayList<NodoSintactico> d = new ArrayList<>();
        for (int i = 0; i < derivados.size(); i++) {
            if (derivados.get(i).getNombre().equals(nombre)) {
                d.add(derivados.get(i));
            }
        }
        return d;
    }

    public String getNombre() {
        return nombre;
    }

    public Token getToken() {
        return token;
    }

    public boolean esTerminal() {
        return esTerminal;
    }

    public boolean tieneDerivados() {
        return !derivados.isEmpty();
    }
}
