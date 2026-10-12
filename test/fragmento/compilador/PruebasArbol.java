package fragmento.compilador;

import java.util.ArrayList;
import java.util.List;

public final class PruebasArbol {
    private PruebasArbol() {
    }

    public static void main(String[] args) {
        String[] programas = {
                "", ";", "long a = 1;", "double a = 1.5;", "long a,b,c;", "long a;",
                "a = 1;", "a++;", "a--;", "a /= 2;", "read a;", "write a;",
                "write a + 2;", "write (a + 2);", "write (a + (b * 2));",
                "if (a < b) write a;", "if (a < b) {}", "if (a < b) {write a;}",
                "if (a < b) {write a; write b;}", "if (a < b) {;} else {}",
                "if (a < b) write a; else write b;", "if (a != b) write a;",
                "while (a < b) {}", "while (a < b) a++;", "while (a < b) {a++; break;}",
                "while (a < b) {if(a == 1) continue; else break;}",
                "if (a < b && b > c) write a;", "if ((a < b) && (b > c)) write a;",
                "if ((a + 2) != (b * 3)) {read a;} else {write b;}",
                "long a,b; read a; a += 2; write (a + 1);",
                "while(a != b) {if(a < b) {a++;} else {b++;} write a;}",
                "while(a < b) {if(a == 1) break; else continue; a++;}",
                "while(a < b) {while(c != d) {c++;} a++;}",
                "if(a < b) {if(c < d) {} else {} write a;} else {;}",
                "if ((a < b || c != d) && e >= f) {write a;} else {}",
                "while(a < b) {; if(a == 1) {;} else {;} continue;}"
        };
        for (String programa : programas) {
            comprobarPrograma(programa);
        }
        comprobarConsultas();
        comprobarAgregadosSucesivos();
        System.out.println("Pruebas del arbol: " + programas.length + " programas y consultas pasaron");
    }

    private static void comprobarPrograma(String programa) {
        try {
            ResultadoCompilacion resultado = new Compilador().compilar(programa);
            ArbolSintactico arbol = resultado.getArbol();
            exigir(arbol != null, "El compilador debe devolver el arbol");
            List<Token> esperados = resultado.getTokens().stream()
                    .filter(token -> token.getTipoToken() != TokenType.EOT).toList();
            ArrayList<NodoSintactico> terminales = arbol.obtenerTodosLosTerminalesHijos(arbol.getRaiz());
            exigir(terminales.size() == esperados.size(), "Terminales perdidos o duplicados");
            for (int i = 0; i < terminales.size(); i++) {
                exigir(terminales.get(i).getToken() == esperados.get(i), "Terminal fuera de orden: " + i);
            }
            exigir(!arbol.toString().isEmpty(), "El arbol debe tener representacion textual");
            comprobarProfundidades(arbol, arbol.getRaiz(), 0);
        } catch (RuntimeException | AssertionError ex) {
            throw new AssertionError("Fallo el arbol de: " + programa, ex);
        }
    }

    private static void comprobarProfundidades(ArbolSintactico arbol, NodoSintactico nodo, int esperado) {
        exigir(arbol.getNivelProfundidadNodo(nodo) == esperado, "Profundidad incorrecta: " + nodo.getNombre());
        exigir(arbol.getNivelProfundidadNodo(arbol.getRaiz(), nodo) == esperado,
                "Profundidad incorrecta desde la raiz indicada");
        for (NodoSintactico hijo : nodo.getDerivados()) {
            comprobarProfundidades(arbol, hijo, esperado + 1);
        }
    }

    private static void comprobarConsultas() {
        ArbolSintactico arbol = new Compilador().compilar(
                "long a = 1; if(a != 2) {long b; write b;} write a;").getArbol();
        NodoSintactico raiz = arbol.getRaiz();
        ArrayList<NodoSintactico> declaraciones = arbol.obtenerTodosLosDerivados("DECLARACION");
        exigir(declaraciones.size() == 2, "La busqueda debe encontrar declaraciones anidadas");
        exigir(arbol.obtenerDerivadosDirectosDeUnaSecuencia(raiz, "DECLARACION").size() == 1,
                "La consulta de secuencia debe excluir las declaraciones del bloque interno");
        exigir(arbol.obtenerSecuencia(raiz).size() == 4, "Tres instrucciones y una secuencia vacia final");
        exigir(arbol.obtenerTodosLosDerivados("SECUENCIA").size() == 6,
                "Buscar todos los derivados debe recorrer tambien los nodos que coinciden");
        NodoSintactico declaracion = declaraciones.get(0);
        NodoSintactico tipo = declaracion.getDerivado("TIPO").get(0);
        NodoSintactico longTerminal = tipo.getDerivado(0);
        exigir(arbol.getNivelProfundidadNodo(longTerminal) == 5, "LONG debe estar a profundidad 5");
        exigir(arbol.getNivelProfundidadNodo(declaracion, longTerminal) == 2,
                "La profundidad relativa debe comenzar en la subraiz");
        exigir(arbol.obtenerTodosLosTerminalesHijos(longTerminal).get(0) == longTerminal,
                "Consultar un terminal debe devolverlo");
        exigir(arbol.toString().contains("0 SECUENCIA ==> 1 ORACION"), "Formato textual de la raiz");
        exigir(arbol.toString().contains("LONG (long)"), "El formato debe mostrar el lexema terminal");
        exigir(arbol.toString().contains("3 SECUENCIA ==> 4 (lambda)"),
                "La secuencia final debe aparecer con su profundidad correcta");
        try {
            arbol.getNivelProfundidadNodo(new NodoSintactico("AJENO"));
            throw new AssertionError("Un nodo ajeno debe ser rechazado");
        } catch (IllegalArgumentException esperado) {
        }
        ArbolSintactico vacio = new ArbolSintactico();
        exigir(vacio.getNivelProfundidadNodo(vacio.getRaiz()) == 0, "Profundidad de la raiz vacia");
        exigir(vacio.toString().contains("(lambda)"), "La secuencia vacia representa lambda");
        ArbolSintactico puntoComa = new Compilador().compilar("; long a;").getArbol();
        exigir(puntoComa.obtenerDerivadosDirectosDeUnaSecuencia(puntoComa.getRaiz(), "DECLARACION").size() == 1,
                "Una instruccion vacia no debe romper la consulta de declaraciones");
    }

    private static void comprobarAgregadosSucesivos() {
        ArbolSintactico arbol = new ArbolSintactico();
        List<Token> esperados = new ArrayList<>();
        for (String instruccion : List.of("long a = 1;", "a++;", "while(a < 3) {a++;}")) {
            ArrayList<Token> terminales = new ArrayList<>(new Lexer(instruccion).analizar());
            terminales.remove(terminales.size() - 1);
            arbol.agregarTerminales(terminales);
            esperados.addAll(terminales);
        }
        List<Token> reales = arbol.obtenerTodosLosTerminalesHijos(arbol.getRaiz()).stream()
                .map(NodoSintactico::getToken).toList();
        exigir(reales.equals(esperados), "Agregar instrucciones sucesivas debe conservar todos los terminales");
    }

    private static void exigir(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
    }
}
