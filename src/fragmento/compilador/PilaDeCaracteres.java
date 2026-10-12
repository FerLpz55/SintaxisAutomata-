package fragmento.compilador;

import java.util.ArrayList;

public class PilaDeCaracteres {
    private ArrayList<Character> pila;
    private int tope;

    public PilaDeCaracteres() {
        pila = new ArrayList<>();
        tope = -1;
        agregarCaracter('z');
    }

    public boolean vacia() {
        return tope == -1;
    }

    public int getIndiceDeTope() {
        return tope;
    }

    /** Agrega un caracter al tope de la pila. */
    public void agregarCaracter(char c) {
        pila.add(Character.valueOf(c));
        tope++;
    }

    /** Metodo destructivo: devuelve y elimina el caracter del tope. */
    public char getCaracterDeTope() throws NullPointerException {
        if (vacia()) throw new NullPointerException("La pila está vacía.");
        char c = pila.remove(tope).charValue();
        tope--;
        return c;
    }

    /** Metodo no destructivo: devuelve el caracter sin eliminarlo. */
    public char verTope() throws NullPointerException {
        if (vacia()) throw new NullPointerException("La pila está vacía.");
        return pila.get(tope).charValue();
    }
}
