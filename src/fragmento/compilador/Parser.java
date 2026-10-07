package fragmento.compilador;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public final class Parser {
    private static final Set<TokenType> INICIO_ORACION = EnumSet.of(
            TokenType.LONG, TokenType.DOUBLE, TokenType.IDENTIFICADOR,
            TokenType.READ, TokenType.WRITE, TokenType.PYCOMA);

    private final List<Token> tokens;
    private int posicion;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void analizar() {
        secuencia(false);
        esperar(TokenType.EOF, "se esperaba fin del programa");
    }

    private void secuencia(boolean dentroBucle) {
        while (!ver(TokenType.EOF) && !ver(TokenType.LLAVEDER) && !ver(TokenType.ELSE)) {
            if (inicioOracion()) {
                oracion();
            } else if (ver(TokenType.IF)) {
                si(dentroBucle);
            } else if (ver(TokenType.WHILE)) {
                mientras();
            } else if (dentroBucle && inicioControl()) {
                control();
            } else {
                error("se esperaba una oracion, if o while");
            }
        }
    }

    private void oracion() {
        if (aceptar(TokenType.PYCOMA)) {
            return;
        }
        instruccion();
        esperar(TokenType.PYCOMA, "falta ';' al final de la oracion");
    }

    private void instruccion() {
        if (ver(TokenType.LONG) || ver(TokenType.DOUBLE)) {
            declaracion();
        } else if (ver(TokenType.IDENTIFICADOR)) {
            asignacion();
        } else if (ver(TokenType.READ)) {
            leer();
        } else if (ver(TokenType.WRITE)) {
            escribir();
        } else {
            error("se esperaba declaracion, asignacion, read o write");
        }
    }

    private void declaracion() {
        tipo();
        esperar(TokenType.IDENTIFICADOR, "se esperaba identificador en declaracion");
        if (aceptar(TokenType.ASIGNADOR)) {
            termino();
        } else {
            while (aceptar(TokenType.COMA)) {
                esperar(TokenType.IDENTIFICADOR, "se esperaba identificador despues de ','");
            }
        }
    }

    private void asignacion() {
        esperar(TokenType.IDENTIFICADOR, "se esperaba identificador");
        if (aceptar(TokenType.ASIGNADOR, TokenType.MENOSNUMERO, TokenType.MASNUMERO,
                TokenType.PORNUMERO, TokenType.DIVIDIDONUMERO)) {
            termino();
        } else if (aceptar(TokenType.MASUNO, TokenType.MENOSUNO)) {
            return;
        } else {
            error("se esperaba operador de asignacion");
        }
    }

    private void si(boolean dentroBucle) {
        esperar(TokenType.IF, "se esperaba if");
        condicionEntreParentesis();
        cuerpoIf(dentroBucle);
        if (aceptar(TokenType.ELSE)) {
            cuerpoElse(dentroBucle);
        }
    }

    private void cuerpoIf(boolean dentroBucle) {
        if (aceptar(TokenType.LLAVEIZQ)) {
            secuencia(dentroBucle);
            esperar(TokenType.LLAVEDER, "falta '}' para cerrar el if");
        } else if (dentroBucle && inicioControl()) {
            control();
        } else {
            oracion();
        }
    }

    private void cuerpoElse(boolean dentroBucle) {
        if (aceptar(TokenType.LLAVEIZQ)) {
            secuencia(dentroBucle);
            esperar(TokenType.LLAVEDER, "falta '}' para cerrar el else");
        } else if (dentroBucle && inicioControl()) {
            control();
        } else {
            oracion();
        }
    }

    private void mientras() {
        esperar(TokenType.WHILE, "se esperaba while");
        condicionEntreParentesis();
        if (aceptar(TokenType.LLAVEIZQ)) {
            secuencia(true);
            esperar(TokenType.LLAVEDER, "falta '}' para cerrar el while");
        } else if (inicioControl()) {
            control();
        } else {
            oracion();
        }
    }

    private void control() {
        if (aceptar(TokenType.BREAK, TokenType.CONTINUE)) {
            esperar(TokenType.PYCOMA, "falta ';' despues del control de bucle");
            return;
        }
        error("se esperaba break o continue");
    }

    private void condicionEntreParentesis() {
        esperar(TokenType.PARIZQ, "falta '(' antes de la condicion");
        condicion();
        esperar(TokenType.PARDER, "falta ')' despues de la condicion");
    }

    private void condicion() {
        condicionSimple();
        while (aceptar(TokenType.AND, TokenType.OR)) {
            condicionSimple();
        }
    }

    private void condicionSimple() {
        if (ver(TokenType.PARIZQ)) {
            int marca = posicion;
            try {
                termino();
                comparador();
                termino();
                return;
            } catch (CompilacionException ex) {
                posicion = marca;
            }
            esperar(TokenType.PARIZQ, "falta '('");
            condicion();
            esperar(TokenType.PARDER, "falta ')' para cerrar condicion agrupada");
            return;
        }

        termino();
        comparador();
        termino();
    }

    private void termino() {
        if (aceptar(TokenType.PARIZQ)) {
            termino();
            esperar(TokenType.PARDER, "falta ')' para cerrar termino");
            return;
        }
        operando();
        if (aceptar(TokenType.MAS, TokenType.MENOS, TokenType.POR, TokenType.DIVISION)) {
            termino();
        }
    }

    private void operando() {
        if (aceptar(TokenType.NUMERO_DOUBLE, TokenType.NUMERO_LONG, TokenType.IDENTIFICADOR)) {
            return;
        }
        error("se esperaba numero o identificador");
    }

    private void comparador() {
        if (!aceptar(TokenType.IGUAL, TokenType.DISTINTO, TokenType.MAYORIGUAL,
                TokenType.MENORIGUAL, TokenType.MAYOR, TokenType.MENOR)) {
            error("se esperaba comparador");
        }
    }

    private void tipo() {
        if (!aceptar(TokenType.DOUBLE, TokenType.LONG)) {
            error("se esperaba tipo long o double");
        }
    }

    private void leer() {
        esperar(TokenType.READ, "se esperaba read");
        esperar(TokenType.IDENTIFICADOR, "read necesita un identificador");
    }

    private void escribir() {
        esperar(TokenType.WRITE, "se esperaba write");
        termino();
    }

    private boolean inicioOracion() {
        return INICIO_ORACION.contains(actual().getTipoToken());
    }

    private boolean inicioControl() {
        return ver(TokenType.BREAK) || ver(TokenType.CONTINUE);
    }

    private boolean aceptar(TokenType... tipos) {
        for (TokenType tipo : tipos) {
            if (ver(tipo)) {
                posicion++;
                return true;
            }
        }
        return false;
    }

    private void esperar(TokenType tipo, String mensaje) {
        if (!aceptar(tipo)) {
            error(mensaje + ". Token actual: " + actual().getLexema());
        }
    }

    private boolean ver(TokenType tipo) {
        return actual().getTipoToken() == tipo;
    }

    private Token actual() {
        return tokens.get(posicion);
    }

    private void error(String mensaje) {
        Token token = actual();
        throw new CompilacionException("Sintactico", mensaje, token.getLinea(), token.getColumna());
    }
}
