package fragmento.compilador;

import java.util.ArrayList;
import java.util.List;

public final class Lexer {
    private final String fuente;
    private final List<Token> tokens = new ArrayList<>();
    private int indice;
    private int linea = 1;
    private int columna = 1;

    public Lexer(String fuente) {
        this.fuente = fuente == null ? "" : fuente;
    }

    public List<Token> analizar() {
        while (!fin()) {
            char actual = actual();
            if (Character.isWhitespace(actual)) {
                avanzarEspacio();
            } else if (esInicioIdentificador(actual)) {
                leerIdentificadorOPalabra();
            } else if (Character.isDigit(actual)) {
                leerNumero();
            } else {
                leerSimbolo();
            }
        }
        tokens.add(new Token(TokenType.EOF, "", linea, columna));
        return tokens;
    }

    private void leerIdentificadorOPalabra() {
        int lineaInicio = linea;
        int columnaInicio = columna;
        int inicio = indice;
        avanzar();
        while (!fin() && esParteIdentificador(actual())) {
            avanzar();
        }

        String lexema = fuente.substring(inicio, indice);
        tokens.add(new Token(palabraReservada(lexema), lexema, lineaInicio, columnaInicio));
    }

    private TokenType palabraReservada(String lexema) {
        return switch (lexema) {
            case "long" -> TokenType.LONG;
            case "double" -> TokenType.DOUBLE;
            case "if" -> TokenType.IF;
            case "else" -> TokenType.ELSE;
            case "while" -> TokenType.WHILE;
            case "break" -> TokenType.BREAK;
            case "continue" -> TokenType.CONTINUE;
            case "read" -> TokenType.READ;
            case "write" -> TokenType.WRITE;
            default -> TokenType.IDENTIFICADOR;
        };
    }

    private void leerNumero() {
        int lineaInicio = linea;
        int columnaInicio = columna;
        int inicio = indice;
        while (!fin() && Character.isDigit(actual())) {
            avanzar();
        }

        boolean esDouble = false;
        if (!fin() && actual() == '.') {
            esDouble = true;
            avanzar();
            if (fin() || !Character.isDigit(actual())) {
                error("numero double incompleto");
            }
            while (!fin() && Character.isDigit(actual())) {
                avanzar();
            }
        }

        if (!fin() && esParteIdentificador(actual())) {
            error("numero seguido de caracteres no validos");
        }

        String lexema = fuente.substring(inicio, indice);
        tokens.add(new Token(esDouble ? TokenType.NUMERO_DOUBLE : TokenType.NUMERO_LONG, lexema, lineaInicio, columnaInicio));
    }

    private void leerSimbolo() {
        int lineaInicio = linea;
        int columnaInicio = columna;
        char actual = actual();
        char siguiente = mirar(1);

        if (actual == '/' && siguiente == '/') {
            while (!fin() && actual() != '\n') {
                avanzar();
            }
            return;
        }

        if (actual == '/' && siguiente == '*') {
            avanzar();
            avanzar();
            while (!fin()) {
                if (actual() == '*' && mirar(1) == '/') {
                    avanzar();
                    avanzar();
                    return;
                }
                avanzar();
            }
            throw new CompilacionException("Lexico", "comentario sin cerrar", lineaInicio, columnaInicio);
        }

        TokenType tipo = switch (String.valueOf(actual) + siguiente) {
            case "!=" -> TokenType.DISTINTO;
            case "==" -> TokenType.IGUAL;
            case "-=" -> TokenType.MENOSNUMERO;
            case "<=" -> TokenType.MENORIGUAL;
            case "+=" -> TokenType.MASNUMERO;
            case ">=" -> TokenType.MAYORIGUAL;
            case "++" -> TokenType.MASUNO;
            case "--" -> TokenType.MENOSUNO;
            case "/=" -> TokenType.DIVIDIDONUMERO;
            case "*=" -> TokenType.PORNUMERO;
            case "&&" -> TokenType.AND;
            case "||" -> TokenType.OR;
            default -> null;
        };
        if (tipo != null) {
            tokens.add(new Token(tipo, fuente.substring(indice, indice + 2), lineaInicio, columnaInicio));
            avanzar();
            avanzar();
            return;
        }

        tipo = switch (actual) {
            case '=' -> TokenType.ASIGNADOR;
            case '/' -> TokenType.DIVISION;
            case '*' -> TokenType.POR;
            case '+' -> TokenType.MAS;
            case '-' -> TokenType.MENOS;
            case '>' -> TokenType.MAYOR;
            case '<' -> TokenType.MENOR;
            case '(' -> TokenType.PARIZQ;
            case ')' -> TokenType.PARDER;
            case '{' -> TokenType.LLAVEIZQ;
            case '}' -> TokenType.LLAVEDER;
            case ';' -> TokenType.PYCOMA;
            case ',' -> TokenType.COMA;
            default -> null;
        };

        if (tipo == null) {
            error("simbolo no reconocido '" + actual + "'");
        }
        tokens.add(new Token(tipo, Character.toString(actual), lineaInicio, columnaInicio));
        avanzar();
    }

    private void avanzarEspacio() {
        if (actual() == '\r' && mirar(1) == '\n') {
            indice += 2;
            linea++;
            columna = 1;
        } else {
            avanzar();
        }
    }

    private void avanzar() {
        if (actual() == '\n') {
            linea++;
            columna = 1;
        } else {
            columna++;
        }
        indice++;
    }

    private char actual() {
        return fuente.charAt(indice);
    }

    private char mirar(int desplazamiento) {
        int posicion = indice + desplazamiento;
        if (posicion >= fuente.length()) {
            return '\0';
        }
        return fuente.charAt(posicion);
    }

    private boolean fin() {
        return indice >= fuente.length();
    }

    private boolean esInicioIdentificador(char caracter) {
        return Character.isLetter(caracter) || caracter == '_';
    }

    private boolean esParteIdentificador(char caracter) {
        return Character.isLetterOrDigit(caracter) || caracter == '_';
    }

    private void error(String mensaje) {
        throw new CompilacionException("Lexico", mensaje, linea, columna);
    }
}
