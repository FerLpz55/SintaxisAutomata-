package fragmento.compilador;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public final class Parser {
    private static final Set<Tokens> INICIO_ORACION = EnumSet.of(
            Tokens.LONG, Tokens.DOUBLE, Tokens.IDENTIFICADOR,
            Tokens.READ, Tokens.WRITE, Tokens.PYCOMA);

    private final List<Token> tokens;
    private int posicion;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void analizar() {
        secuencia(false);
        esperar(Tokens.EOT, "se esperaba fin del programa");
    }

    private void secuencia(boolean dentroBucle) {
        while (!ver(Tokens.EOT) && !ver(Tokens.LLAVEDER) && !ver(Tokens.ELSE)) {
            if (inicioOracion()) {
                oracion();
            } else if (ver(Tokens.IF)) {
                si(dentroBucle);
            } else if (ver(Tokens.WHILE)) {
                mientras();
            } else if (dentroBucle && inicioControl()) {
                control();
            } else {
                error("se esperaba una oracion, if o while");
            }
        }
    }

    private void oracion() {
        if (aceptar(Tokens.PYCOMA)) {
            return;
        }
        instruccion();
        esperar(Tokens.PYCOMA, "falta ';' al final de la oracion");
    }

    private void instruccion() {
        if (ver(Tokens.LONG) || ver(Tokens.DOUBLE)) {
            declaracion();
        } else if (ver(Tokens.IDENTIFICADOR)) {
            asignacion();
        } else if (ver(Tokens.READ)) {
            leer();
        } else if (ver(Tokens.WRITE)) {
            escribir();
        } else {
            error("se esperaba declaracion, asignacion, read o write");
        }
    }

    private void declaracion() {
        tipo();
        esperar(Tokens.IDENTIFICADOR, "se esperaba identificador en declaracion");
        if (aceptar(Tokens.ASIGNADOR)) {
            termino();
        } else {
            while (aceptar(Tokens.COMA)) {
                esperar(Tokens.IDENTIFICADOR, "se esperaba identificador despues de ','");
            }
        }
    }

    private void asignacion() {
        esperar(Tokens.IDENTIFICADOR, "se esperaba identificador");
        if (aceptar(Tokens.ASIGNADOR, Tokens.MENOSNUMERO, Tokens.MASNUMERO,
                Tokens.PORNUMERO, Tokens.DIVIDIDONUMERO)) {
            termino();
        } else if (aceptar(Tokens.MASUNO, Tokens.MENOSUNO)) {
            return;
        } else {
            error("se esperaba operador de asignacion");
        }
    }

    private void si(boolean dentroBucle) {
        esperar(Tokens.IF, "se esperaba if");
        condicionEntreParentesis();
        cuerpoIf(dentroBucle);
        if (aceptar(Tokens.ELSE)) {
            cuerpoElse(dentroBucle);
        }
    }

    private void cuerpoIf(boolean dentroBucle) {
        if (aceptar(Tokens.LLAVEIZQ)) {
            secuencia(dentroBucle);
            esperar(Tokens.LLAVEDER, "falta '}' para cerrar el if");
        } else if (dentroBucle && inicioControl()) {
            control();
        } else {
            oracion();
        }
    }

    private void cuerpoElse(boolean dentroBucle) {
        if (aceptar(Tokens.LLAVEIZQ)) {
            secuencia(dentroBucle);
            esperar(Tokens.LLAVEDER, "falta '}' para cerrar el else");
        } else if (dentroBucle && inicioControl()) {
            control();
        } else {
            oracion();
        }
    }

    private void mientras() {
        esperar(Tokens.WHILE, "se esperaba while");
        condicionEntreParentesis();
        if (aceptar(Tokens.LLAVEIZQ)) {
            secuencia(true);
            esperar(Tokens.LLAVEDER, "falta '}' para cerrar el while");
        } else if (inicioControl()) {
            control();
        } else {
            oracion();
        }
    }

    private void control() {
        if (aceptar(Tokens.BREAK, Tokens.CONTINUE)) {
            esperar(Tokens.PYCOMA, "falta ';' despues del control de bucle");
            return;
        }
        error("se esperaba break o continue");
    }

    private void condicionEntreParentesis() {
        esperar(Tokens.PARIZQ, "falta '(' antes de la condicion");
        condicion();
        esperar(Tokens.PARDER, "falta ')' despues de la condicion");
    }

    private void condicion() {
        condicionSimple();
        while (aceptar(Tokens.AND, Tokens.OR)) {
            condicionSimple();
        }
    }

    private void condicionSimple() {
        if (ver(Tokens.PARIZQ)) {
            int marca = posicion;
            try {
                termino();
                comparador();
                termino();
                return;
            } catch (CompilacionException ex) {
                posicion = marca;
            }
            esperar(Tokens.PARIZQ, "falta '('");
            condicion();
            esperar(Tokens.PARDER, "falta ')' para cerrar condicion agrupada");
            return;
        }

        termino();
        comparador();
        termino();
    }

    private void termino() {
        if (aceptar(Tokens.PARIZQ)) {
            termino();
            esperar(Tokens.PARDER, "falta ')' para cerrar termino");
            return;
        }
        operando();
        if (aceptar(Tokens.MAS, Tokens.MENOS, Tokens.POR, Tokens.DIVISION)) {
            termino();
        }
    }

    private void operando() {
        if (aceptar(Tokens.NUMERO_DOUBLE, Tokens.NUMERO_LONG, Tokens.IDENTIFICADOR)) {
            return;
        }
        error("se esperaba numero o identificador");
    }

    private void comparador() {
        if (!aceptar(Tokens.IGUAL, Tokens.DISTINTO, Tokens.MAYORIGUAL,
                Tokens.MENORIGUAL, Tokens.MAYOR, Tokens.MENOR)) {
            error("se esperaba comparador");
        }
    }

    private void tipo() {
        if (!aceptar(Tokens.DOUBLE, Tokens.LONG)) {
            error("se esperaba tipo long o double");
        }
    }

    private void leer() {
        esperar(Tokens.READ, "se esperaba read");
        esperar(Tokens.IDENTIFICADOR, "read necesita un identificador");
    }

    private void escribir() {
        esperar(Tokens.WRITE, "se esperaba write");
        termino();
    }

    private boolean inicioOracion() {
        return INICIO_ORACION.contains(actual().getTipoToken());
    }

    private boolean inicioControl() {
        return ver(Tokens.BREAK) || ver(Tokens.CONTINUE);
    }

    private boolean aceptar(Tokens... tipos) {
        for (Tokens tipo : tipos) {
            if (ver(tipo)) {
                posicion++;
                return true;
            }
        }
        return false;
    }

    private void esperar(Tokens tipo, String mensaje) {
        if (!aceptar(tipo)) {
            error(mensaje + ". Token actual: " + actual().getLexema());
        }
    }

    private boolean ver(Tokens tipo) {
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
