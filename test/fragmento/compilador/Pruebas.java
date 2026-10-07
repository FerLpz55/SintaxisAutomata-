package fragmento.compilador;

public final class Pruebas {
    private Pruebas() {
    }

    public static void main(String[] args) {
        Token token = new Token("edad", 1, 6);
        token.setTipo("IDENTIFICADOR");
        token.setToken("contador");
        token.setIndiceFila(2);
        token.setIndiceComienzo(4);
        if (!token.getToken().equals("contador")
                || !token.getTipo().equals("IDENTIFICADOR")
                || token.getTipoToken() != TokenType.IDENTIFICADOR
                || token.getLinea() != 2 || token.getColumna() != 4
                || !token.getLexema().equals("contador")) {
            throw new AssertionError("Los metodos de Token deben compartir los mismos datos");
        }

        debeAceptar("programa basico", """
                long a = 1;
                double b = 2.5;
                read a;
                write b;
                """);

        debeAceptar("while con controles", """
                long contador = 0;
                while (contador < 10) {
                    contador++;
                    if (contador == 5) continue;
                    if (contador >= 8) {
                        break;
                    } else {
                        write contador;
                    }
                }
                """);

        debeAceptar("condiciones compuestas", """
                long a,b,c;
                if ((a < b) && (b != c)) {
                    a += 1;
                } else {
                    write c;
                }
                """);

        debeRechazar("break fuera de while", "break;");
        debeRechazar("falta punto y coma", "long x = 3");
        debeRechazar("condicion sin comparador", "if (x) write x;");

        System.out.println("Todas las pruebas pasaron");
    }

    private static void debeAceptar(String nombre, String fuente) {
        try {
            new Compilador().compilar(fuente);
        } catch (RuntimeException ex) {
            throw new AssertionError("Debia aceptar " + nombre + ": " + ex.getMessage(), ex);
        }
    }

    private static void debeRechazar(String nombre, String fuente) {
        try {
            new Compilador().compilar(fuente);
            throw new AssertionError("Debia rechazar " + nombre);
        } catch (CompilacionException esperado) {
            // Caso correcto.
        }
    }
}
