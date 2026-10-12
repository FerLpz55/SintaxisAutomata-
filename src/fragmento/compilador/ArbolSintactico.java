package fragmento.compilador;

import java.util.ArrayList;

/**
 * Arbol sintactico basado en el material de clase de ArbolSintactico.java.
 * Conserva los nodos y metodos del ejemplo; corrige el encadenamiento de
 * instrucciones, parentesis, comparaciones y busqueda de profundidad.
 * Los terminales deben corresponder a instrucciones completas ya validadas.
 */
public class ArbolSintactico {
    private final NodoSintactico raiz;

    public ArbolSintactico() {
        raiz = new NodoSintactico("SECUENCIA");
    }

    /** Agrega una instruccion completa; el parser ya debe haber validado sus terminales. */
    public void agregarTerminales(ArrayList<Token> terminales) {
        NodoSintactico sub_raiz = buscarSecuenciaSinDerivados();
        switch (terminales.get(0).getTipo()) {
            case "DOUBLE":
            agregarDeclaracion(sub_raiz, terminales);
            break;
            case "IDENTIFICADOR":
            agregarAsignacion(sub_raiz, terminales);
            break;
            case "IF":
            agregarSi(sub_raiz, terminales);
            break;
            case "LONG":
            agregarDeclaracion(sub_raiz, terminales);
            break;
            case "PYCOMA":
            agregarInstruccionVacia(sub_raiz, terminales);
            break;
            case "READ":
            agregarLeer(sub_raiz, terminales);
            break;
            case "WHILE":
            agregarMientras(sub_raiz, terminales);
            break;
            case "WRITE":
            agregarEscribir(sub_raiz, terminales);
            break;
        }
        sub_raiz.agregarDerivado(new NodoSintactico("SECUENCIA") );
    }
    private int buscarIndiceParentesisDeCierre(ArrayList<Token> alt)
    {
        int i_cierre = 0;
        int abiertos = 0;
        boolean abertura=false;
        for(int i=0;i<alt.size();i++)
        {
            if (alt.get(i).getTipo().equals("PARIZQ"))
            {
                abiertos++;
                abertura=true;
            }
            if (abertura&&alt.get(i).getTipo().equals("PARDER") )
            abiertos--;
            if (abertura&&abiertos==0)
            {
                i_cierre = i;
                break;
            }
        }
        return i_cierre;
    }
    private NodoSintactico buscarSecuenciaSinDerivados()
    {
        NodoSintactico ns=raiz;
        while(ns.tieneDerivados())
        ns=ns.getDerivado("SECUENCIA").get(0);
        return ns;
    }
    private void agregarDeclaracion(NodoSintactico subsubraiz,ArrayList<Token> terminales)
    {
        ArrayList<NodoSintactico> auxiliar = new ArrayList<>();
        auxiliar.add(new NodoSintactico("ORACION"));
        auxiliar.add(new NodoSintactico("INSTRUCCION") );
        auxiliar.add(new NodoSintactico("DECLARACION") );
        auxiliar.add(new NodoSintactico("TIPO"));
        auxiliar.add(new NodoSintactico(terminales.get(0)));
        for(int i=1;i<auxiliar.size();i++)
        auxiliar.get(i-1).agregarDerivado(auxiliar.get(i));
        if(terminales.get(2).getTipo().equals("COMA") ||terminales.get(2).getTipo().equals("PYCOMA") )
        {
            for(int i=1;i<terminales.size()-1; i++)
            {
                if (i%2==1)
                {
                    if(i==1)
                    {
                        auxiliar.add(new NodoSintactico("LISTADO"));
                        auxiliar.get(auxiliar.size()-4).agregarDerivado(auxiliar.get(auxiliar.size()-1));
                        auxiliar.add(new NodoSintactico(terminales.get(i)));
                        auxiliar.get(auxiliar.size()-2).agregarDerivado(auxiliar.get(auxiliar.size()-1));
                        if(i<terminales.size()-2)
                        {
                            i++;
                            auxiliar.add(new NodoSintactico(terminales.get(i)));
                            auxiliar.get(auxiliar.size()-3).agregarDerivado(auxiliar.get(auxiliar.size()-1));
                        }
                    } else {
                        auxiliar.add(new NodoSintactico("LISTADO"));
                        auxiliar.get(auxiliar.size()-4).agregarDerivado(auxiliar.get(auxiliar.size()-1));
                        auxiliar.add(new NodoSintactico(terminales.get(i)));
                        auxiliar.get(auxiliar.size()-2).agregarDerivado(auxiliar.get(auxiliar.size()-1));
                        if(i<terminales.size()-2)
                        {
                            i++;
                            auxiliar.add(new NodoSintactico(terminales.get(i)));
                            auxiliar.get(auxiliar.size()-3).agregarDerivado(auxiliar.get(auxiliar.size()-1));
                        }
                    }
                }
            }
        }
        if(terminales.get(2).getTipo().equals("ASIGNADOR") )
        {
            auxiliar.add(new NodoSintactico(terminales.get(1)));
            auxiliar.add(new NodoSintactico(terminales.get(2)));
            auxiliar.add(new NodoSintactico("TERMINO"));
            for(int i=auxiliar.size()-3; i<auxiliar.size(); i++)
            auxiliar.get(auxiliar.size()-6).agregarDerivado(auxiliar.get(i));
            agregarTermino(auxiliar.get(auxiliar.size()-1), new ArrayList<>(terminales.subList(3,terminales.size()-1)));
        }
        auxiliar.add(new NodoSintactico(terminales.get(terminales.size()-1)));
        auxiliar.get(0).agregarDerivado(auxiliar.get(auxiliar.size()-1));
        subsubraiz.agregarDerivado(auxiliar.get(0));
    }
    private void agregarAsignacion(NodoSintactico subsubraiz,ArrayList<Token> terminales)
    {
        ArrayList<NodoSintactico> auxiliar = new ArrayList<>();
        auxiliar.add(new NodoSintactico("ORACION"));
        auxiliar.add(new NodoSintactico("INSTRUCCION") );
        auxiliar.add(new NodoSintactico("ASIGNACION"));
        auxiliar.add(new NodoSintactico(terminales.get(0)));
        for(int i=1;i<auxiliar.size();i++)
        auxiliar.get(i-1).agregarDerivado(auxiliar.get(i));
        if (terminales.get(1).getTipo().equals("MASUNO") ||terminales.get(1).getTipo().equals ("MENOSUNO") )
        {
            auxiliar.add(new NodoSintactico(terminales.get(1)));
            auxiliar.get(2).agregarDerivado(auxiliar.get(auxiliar.size()-1));
        } else {
            auxiliar.add(new NodoSintactico(terminales.get(1)));
            auxiliar.add(new NodoSintactico("TERMINO"));
            for(int i=auxiliar.size()-2;i<auxiliar.size();i++)
            auxiliar.get(auxiliar.size()-4).agregarDerivado(auxiliar.get(i));
            agregarTermino(auxiliar.get(auxiliar.size()-1), new ArrayList<>(terminales.subList(2,terminales.size()-1)));
        }
        auxiliar.add(new NodoSintactico(terminales.get(terminales.size()-1)));
        auxiliar.get(0).agregarDerivado(auxiliar.get(auxiliar.size()-1));
        subsubraiz.agregarDerivado(auxiliar.get(0));
    }
    private void agregarLeer(NodoSintactico subsubraiz,ArrayList<Token> terminales)
    {
        ArrayList<NodoSintactico> auxiliar = new ArrayList<>();
        auxiliar.add(new NodoSintactico("ORACION"));
        auxiliar.add(new NodoSintactico("INSTRUCCION") );
        auxiliar.get(auxiliar.size()-2).agregarDerivado(auxiliar.get(auxiliar.size()-1));
        auxiliar.add(new NodoSintactico("LEER"));
        auxiliar.get(auxiliar.size()-2).agregarDerivado(auxiliar.get(auxiliar.size()-1));
        auxiliar.add(new NodoSintactico(terminales.get(0)));
        auxiliar.get(2).agregarDerivado(auxiliar.get(auxiliar.size()-1));
        auxiliar.add(new NodoSintactico(terminales.get(1)));
        auxiliar.get(2).agregarDerivado(auxiliar.get(auxiliar.size()-1));
        auxiliar.add(new NodoSintactico(terminales.get(2)));
        auxiliar.get(0).agregarDerivado(auxiliar.get(auxiliar.size()-1));
        subsubraiz.agregarDerivado(auxiliar.get(0));
    }
    private void agregarEscribir(NodoSintactico subsubraiz,ArrayList<Token> terminales)
    {
        ArrayList<NodoSintactico> auxiliar = new ArrayList<>();
        auxiliar.add(new NodoSintactico("ORACION"));
        auxiliar.add(new NodoSintactico("INSTRUCCION") );
        auxiliar.add(new NodoSintactico("ESCRIBIR"));
        auxiliar.add(new NodoSintactico(terminales.get(0)));
        for(int i=1;i<auxiliar.size();i++)
        auxiliar.get(i-1).agregarDerivado(auxiliar.get(i));
        auxiliar.add(new NodoSintactico("TERMINO"));
        auxiliar.get(auxiliar.size()-3).agregarDerivado(auxiliar.get(auxiliar.size()-1));
        agregarTermino(auxiliar.get(auxiliar.size()-1), new ArrayList<>(terminales.subList(1,terminales.size()-1)));
        auxiliar.add(new NodoSintactico(terminales.get(terminales.size()-1)));
        auxiliar.get(0).agregarDerivado(auxiliar.get(auxiliar.size()-1));
        subsubraiz.agregarDerivado(auxiliar.get(0));
    }
    private void agregarSi(NodoSintactico subsubraiz, ArrayList<Token> terminales) {
        agregarCondicional(subsubraiz, terminales, false);
    }
    private void agregarSiBucle(NodoSintactico subsubraiz, ArrayList<Token> terminales) {
        agregarCondicional(subsubraiz, terminales, true);
    }
    private void agregarMientras(NodoSintactico subsubraiz, ArrayList<Token> terminales) {
        NodoSintactico mientras = new NodoSintactico("MIENTRAS");
        int cierre = buscarIndiceParentesisDeCierre(terminales);
        mientras.agregarDerivado(new NodoSintactico(terminales.get(0)));
        mientras.agregarDerivado(new NodoSintactico(terminales.get(1)));
        NodoSintactico condicion = new NodoSintactico("CONDICION");
        agregarCondicion(condicion, new ArrayList<>(terminales.subList(2, cierre)));
        mientras.agregarDerivado(condicion);
        mientras.agregarDerivado(new NodoSintactico(terminales.get(cierre)));
        agregarCuerpo(mientras, new ArrayList<>(terminales.subList(cierre + 1, terminales.size())), true);
        subsubraiz.agregarDerivado(mientras);
    }
    private int buscarIndiceLlaveDeCierre(ArrayList<Token> alt)
    {
        int i_cierre = 0;
        int abiertas = 0;
        boolean abertura=false;
        for(int i=0;i<alt.size();i++)
        {
            if (alt.get(i).getTipo().equals("LLAVEIZQ"))
            {
                abiertas++;
                abertura=true;
            }
            if (abertura&&alt. get (i).getTipo().equals("LLAVEDER") )
            abiertas--;
            if (abertura&&abiertas==0)
            {
                i_cierre = i;
                break;
            }
        }
        return i_cierre;
    }
    private void agregarInstruccionVacia(NodoSintactico subsubraiz,ArrayList<Token> terminales)
    {
        ArrayList<NodoSintactico> auxiliar = new ArrayList<>();
        auxiliar.add(new NodoSintactico("ORACION"));
        auxiliar.add(new NodoSintactico("INSTRUCCION") );
        auxiliar.get(0).agregarDerivado(auxiliar.get(auxiliar.size()-1));
        auxiliar.add(new NodoSintactico(terminales.get(0)));
        auxiliar.get(0).agregarDerivado(auxiliar.get(auxiliar.size()-1));
        subsubraiz.agregarDerivado(auxiliar.get(0));
    }
    private void agregarSecuencia(NodoSintactico subsubraiz, ArrayList<Token> terminales) {
        agregarListaInstrucciones(subsubraiz, terminales, false);
    }
    private void agregarBloqueBucle(NodoSintactico subsubraiz, ArrayList<Token> terminales) {
        agregarListaInstrucciones(subsubraiz, terminales, true);
    }
    private void agregarControl(NodoSintactico subsubraiz,ArrayList<Token> terminales)
    {
        ArrayList<NodoSintactico> auxiliar = new ArrayList<>();
        auxiliar.add(new NodoSintactico("CONTROL"));
        auxiliar.add(new NodoSintactico(terminales.get(0)));
        auxiliar.add(new NodoSintactico(terminales.get(1)));
        for(int i=2;i>0;i--)
        auxiliar.get(auxiliar.size()-3).agregarDerivado(auxiliar.get(auxiliar.size()-i));
        subsubraiz.agregarDerivado(auxiliar.get(0));
    }
    private int buscarIndiceProximoPYCOMA(ArrayList<Token> terminales)
    {
        int ipyc=-1;
        for(int i=0;i<terminales.size();i++)
        if (terminales.get(i).getTipo().equals("PYCOMA") )
        {
            ipyc=i;
            break;
        }
        return ipyc;
    }
    private void agregarSiNo(NodoSintactico subsubraiz, ArrayList<Token> terminales) {
        subsubraiz.agregarDerivado(new NodoSintactico(terminales.get(0)));
        agregarCuerpo(subsubraiz, new ArrayList<>(terminales.subList(1, terminales.size())), false);
    }
    private void agregarSiNoBucle(NodoSintactico subsubraiz, ArrayList<Token> terminales) {
        subsubraiz.agregarDerivado(new NodoSintactico(terminales.get(0)));
        agregarCuerpo(subsubraiz, new ArrayList<>(terminales.subList(1, terminales.size())), true);
    }
    private void agregarTermino(NodoSintactico subsubraiz, ArrayList<Token> terminales) {
        if (terminales.isEmpty()) {
            throw new IllegalArgumentException("El termino no contiene terminales.");
        }
        if (terminales.get(0).getTipo().equals("PARIZQ")) {
            int cierre = buscarIndiceParentesisDeCierre(terminales);
            if (cierre != terminales.size() - 1) {
                throw new IllegalArgumentException("Los parentesis no abarcan el termino completo.");
            }
            subsubraiz.agregarDerivado(new NodoSintactico(terminales.get(0)));
            NodoSintactico termino = new NodoSintactico("TERMINO");
            agregarTermino(termino, new ArrayList<>(terminales.subList(1, cierre)));
            subsubraiz.agregarDerivado(termino);
            subsubraiz.agregarDerivado(new NodoSintactico(terminales.get(cierre)));
            return;
        }
        NodoSintactico operando = new NodoSintactico("OPERANDO");
        operando.agregarDerivado(new NodoSintactico(terminales.get(0)));
        subsubraiz.agregarDerivado(operando);
        if (terminales.size() > 1) {
            NodoSintactico operador = new NodoSintactico("OPERADOR");
            operador.agregarDerivado(new NodoSintactico(terminales.get(1)));
            subsubraiz.agregarDerivado(operador);
            NodoSintactico termino = new NodoSintactico("TERMINO");
            agregarTermino(termino, new ArrayList<>(terminales.subList(2, terminales.size())));
            subsubraiz.agregarDerivado(termino);
        }
    }
    private int buscarIndiceOY(ArrayList<Token> alt)
    {
        int indice=-1;
        for(int i=0;i<alt.size();i++)
        if(alt.get(i).getTipo().equals ("AND") ||alt.get(i).getTipo().equals("OR"))
        {
            indice=i;
            break;
        }
        return indice;
    }
    private void agregarCondicionSimple(NodoSintactico subsubraiz, ArrayList<Token> terminales) {
        int comparador = -1;
        int parentesis = 0;
        for (int i = 0; i < terminales.size(); i++) {
            String tipo = terminales.get(i).getTipo();
            if (tipo.equals("PARIZQ")) parentesis++;
            if (tipo.equals("PARDER")) parentesis--;
            if (parentesis == 0 && switch (tipo) {
                case "IGUAL", "DISTINTO", "MAYOR", "MAYORIGUAL", "MENOR", "MENORIGUAL" -> true;
                default -> false;
            }) {
                comparador = i;
                break;
            }
        }
        if (comparador < 1 || comparador == terminales.size() - 1) {
            throw new IllegalArgumentException("La condicion no contiene una comparacion completa.");
        }
        NodoSintactico izquierdo = new NodoSintactico("TERMINO");
        agregarTermino(izquierdo, new ArrayList<>(terminales.subList(0, comparador)));
        subsubraiz.agregarDerivado(izquierdo);
        NodoSintactico nodoComparador = new NodoSintactico("COMPARADOR");
        nodoComparador.agregarDerivado(new NodoSintactico(terminales.get(comparador)));
        subsubraiz.agregarDerivado(nodoComparador);
        NodoSintactico derecho = new NodoSintactico("TERMINO");
        agregarTermino(derecho, new ArrayList<>(terminales.subList(comparador + 1, terminales.size())));
        subsubraiz.agregarDerivado(derecho);
    }
    private int buscarIndiceUltimaOcurrenciaNodoSintactico(ArrayList<NodoSintactico> arreglo,String nombreNodo)
    {
        int ions = -1;
        for(int i=arreglo.size()-1; i>-1; i--)
        if (arreglo.get(i).getNombre().equals(nombreNodo) )
        {
            ions=i;
            break;
        }
        return ions;
    }
    public NodoSintactico getRaiz()
    {
        return raiz;
    }
    /** Representa las ramas con el formato de niveles del material de clase. */
    @Override
    public String toString() {
        String texto = subToString(raiz, 0);
        return texto.substring(0, texto.length() - 1);
    }

    private String subToString(NodoSintactico antecedente, int na) {
        if (!antecedente.tieneDerivados()) {
            return na + " " + antecedente.getNombre() + " ==> " + (na + 1) + " (lambda)\n";
        }
        StringBuilder texto = new StringBuilder();
        for (NodoSintactico consecuente : antecedente.getDerivados()) {
            texto.append(na).append(' ').append(antecedente.getNombre())
                    .append(" ==> ").append(na + 1).append(' ').append(consecuente.getNombre());
            if (consecuente.esTerminal()) {
                texto.append(" (").append(consecuente.getToken().getToken()).append(')');
            }
            texto.append('\n');
            if (!consecuente.esTerminal()) {
                texto.append(subToString(consecuente, na + 1));
            }
        }
        return texto.toString();
    }

    public int getNivelProfundidadNodo(NodoSintactico nodo) throws IllegalArgumentException {
        return getNivelProfundidadNodo(raiz, nodo);
    }
    public int getNivelProfundidadNodo(NodoSintactico nodoRaiz, NodoSintactico nodo) throws IllegalArgumentException {
        int nivel = subGetNivelProfundidadNodo(nodoRaiz, nodo, 0);
        if (nivel < 0) {
            throw new IllegalArgumentException("El argumento nodo no pertenece al arbol indicado.");
        }
        return nivel;
    }
    private int subGetNivelProfundidadNodo(NodoSintactico nodoSubRaiz, NodoSintactico nodo, int nivel) {
        if (nodoSubRaiz.equals(nodo)) return nivel;
        for (NodoSintactico derivado : nodoSubRaiz.getDerivados()) {
            int encontrado = subGetNivelProfundidadNodo(derivado, nodo, nivel + 1);
            if (encontrado >= 0) return encontrado;
        }
        return -1;
    }
    private int buscarIndiceHijoMasProfundoSinDerivados(ArrayList<NodoSintactico> arreglo, NodoSintactico subraiz,
    String nombreNodo) throws IllegalArgumentException
    {
        int indice = -1;
        for(int i=0;i<arreglo.size();i++)
        {
            if (arreglo.get(i).getNombre().equals(nombreNodo) )
            {
                if(arreglo.get(i).esTerminal())
                {
                    throw new IllegalArgumentException("El argumento nombreNodo especifica un nodo terminal.");
                }
                if(!arreglo.get(i).tieneDerivados())
                {
                    if (indice==-1)
                    {
                        indice = i;
                    } else {
                        if (getNivelProfundidadNodo(subraiz, arreglo.get(i))>getNivelProfundidadNodo(subraiz,
                        arreglo.get(indice)))
                        {
                            indice = i;
                        }
                    }
                }
            }
        }
        return indice;
    }
    private void agregarCondicion(NodoSintactico subsubraiz, ArrayList<Token> terminales) {
        if (terminales.isEmpty()) {
            throw new IllegalArgumentException("La condicion no contiene terminales.");
        }
        int parentesis = 0;
        int oy = -1;
        for (int i = 0; i < terminales.size(); i++) {
            String tipo = terminales.get(i).getTipo();
            if (tipo.equals("PARIZQ")) parentesis++;
            if (tipo.equals("PARDER")) parentesis--;
            if (parentesis == 0 && (tipo.equals("AND") || tipo.equals("OR"))) {
                oy = i;
                break;
            }
        }
        if (oy >= 0) {
            NodoSintactico izquierda = new NodoSintactico("CONDICION");
            agregarCondicion(izquierda, new ArrayList<>(terminales.subList(0, oy)));
            subsubraiz.agregarDerivado(izquierda);
            NodoSintactico operador = new NodoSintactico("OY");
            operador.agregarDerivado(new NodoSintactico(terminales.get(oy)));
            subsubraiz.agregarDerivado(operador);
            NodoSintactico derecha = new NodoSintactico("CONDICION");
            agregarCondicion(derecha, new ArrayList<>(terminales.subList(oy + 1, terminales.size())));
            subsubraiz.agregarDerivado(derecha);
        } else if (terminales.get(0).getTipo().equals("PARIZQ")
        && buscarIndiceParentesisDeCierre(terminales) == terminales.size() - 1) {
            subsubraiz.agregarDerivado(new NodoSintactico(terminales.get(0)));
            NodoSintactico condicion = new NodoSintactico("CONDICION");
            agregarCondicion(condicion, new ArrayList<>(terminales.subList(1, terminales.size() - 1)));
            subsubraiz.agregarDerivado(condicion);
            subsubraiz.agregarDerivado(new NodoSintactico(terminales.get(terminales.size() - 1)));
        } else {
            agregarCondicionSimple(subsubraiz, terminales);
        }
    }

    public ArrayList<NodoSintactico> obtenerTodosLosDerivados(String nombre) {
        return obtenerTodosLosDerivados(raiz, nombre);
    }

    public ArrayList<NodoSintactico> obtenerTodosLosDerivados(NodoSintactico subraiz,String nombre)
    {
        ArrayList<NodoSintactico> alns = new ArrayList<>();
        for(int i=0;i<subraiz.getDerivados().size();i++)
        {
            if (subraiz.getDerivado(i).getNombre().equals (nombre) )
            {
                alns.add(subraiz.getDerivado(i));
                if (subraiz.getDerivado(i).tieneDerivados())
                {
                    try{
                        alns.addAll(obtenerTodosLosDerivados(subraiz.getDerivado(i),nombre) );
                    }catch(NullPointerException exc) {}
                }
            } else {
                if (subraiz.getDerivado(i).tieneDerivados())
                {
                    try{
                        alns.addAll(obtenerTodosLosDerivados(subraiz.getDerivado(i),nombre) );
                    }catch(NullPointerException exc) {}
                }
            }
        }
        return alns;
    }

    public ArrayList<NodoSintactico> obtenerDerivadosDirectosDeUnaSecuencia(NodoSintactico inicioSecuencia, String buscado) {
        ArrayList<NodoSintactico> encontrados = new ArrayList<>();
        for (NodoSintactico secuencia : obtenerSecuencia(inicioSecuencia)) {
            if (!secuencia.tieneDerivados()) continue;
            NodoSintactico instruccion = secuencia.getDerivado(0);
            if (instruccion.getNombre().equals(buscado)) {
                encontrados.add(instruccion);
            } else if (instruccion.getNombre().equals("ORACION")) {
                for (NodoSintactico contenedor : instruccion.getDerivado("INSTRUCCION")) {
                    for (NodoSintactico derivado : contenedor.getDerivados()) {
                        if (derivado.getNombre().equals(buscado)) encontrados.add(derivado);
                    }
                }
            }
        }
        return encontrados;
    }
    public ArrayList<NodoSintactico> obtenerSecuencia(NodoSintactico inicioSecuencia)
    {
        ArrayList<NodoSintactico> secuencia = new ArrayList<>();
        secuencia. add(inicioSecuencia);
        while(secuencia.get(secuencia.size()-1).tieneDerivados())
        {
            if (secuencia. get (secuencia.size()-1).getDerivados().size()>1)
            secuencia.add(secuencia.get(secuencia.size()-1).getDerivado(1));
            else break;
        }
        return secuencia;
    }
    public ArrayList<NodoSintactico> obtenerTodosLosTerminalesHijos(NodoSintactico subraiz)
    {
        ArrayList<NodoSintactico> retorno = new ArrayList<>();
        if(subraiz.esTerminal())
        {
            retorno.add(subraiz);
        } else {
            for(int i=0;i<subraiz.getDerivados().size();i++)
            {
                if (subraiz.getDerivado(i).esTerminal())
                {
                    retorno.add(subraiz.getDerivado(i));
                } else {
                    retorno.addAll(obtenerTodosLosTerminalesHijos(subraiz.getDerivado(i)));
                }
            }
        }
        return retorno;
    }

    /** Encadena cada instruccion una sola vez, conservando SECUENCIA y BLOQUEBUCLE. */
    private void agregarListaInstrucciones(NodoSintactico subraiz, ArrayList<Token> terminales, boolean dentroBucle) {
        NodoSintactico actual = subraiz;
        int inicio = 0;
        while (inicio < terminales.size()) {
            int fin = buscarFinInstruccion(terminales, inicio);
            agregarInstruccion(actual, new ArrayList<>(terminales.subList(inicio, fin)), dentroBucle);
            NodoSintactico siguiente = new NodoSintactico(dentroBucle ? "BLOQUEBUCLE" : "SECUENCIA");
            actual.agregarDerivado(siguiente);
            actual = siguiente;
            inicio = fin;
        }
    }

    private void agregarInstruccion(NodoSintactico subraiz, ArrayList<Token> terminales, boolean dentroBucle) {
        switch (terminales.get(0).getTipo()) {
            case "LONG", "DOUBLE": agregarDeclaracion(subraiz, terminales); break;
            case "IDENTIFICADOR": agregarAsignacion(subraiz, terminales); break;
            case "PYCOMA": agregarInstruccionVacia(subraiz, terminales); break;
            case "READ": agregarLeer(subraiz, terminales); break;
            case "WRITE": agregarEscribir(subraiz, terminales); break;
            case "WHILE": agregarMientras(subraiz, terminales); break;
            case "IF":
            if (dentroBucle) agregarSiBucle(subraiz, terminales);
            else agregarSi(subraiz, terminales);
            break;
            case "BREAK", "CONTINUE":
            if (!dentroBucle) throw new IllegalArgumentException("Control fuera de bucle.");
            agregarControl(subraiz, terminales);
            break;
            default: throw new IllegalArgumentException("Inicio de instruccion desconocido.");
        }
    }

    /** Devuelve el limite exclusivo, incluyendo el else que corresponda al if. */
    private int buscarFinInstruccion(ArrayList<Token> terminales, int inicio) {
        String tipo = terminales.get(inicio).getTipo();
        if (tipo.equals("IF") || tipo.equals("WHILE")) {
            int cierre = inicio + buscarIndiceParentesisDeCierre(
            new ArrayList<>(terminales.subList(inicio, terminales.size())));
            int fin = buscarFinCuerpo(terminales, cierre + 1);
            if (tipo.equals("IF") && fin < terminales.size() && terminales.get(fin).getTipo().equals("ELSE")) {
                fin = buscarFinCuerpo(terminales, fin + 1);
            }
            return fin;
        }
        int puntoComa = buscarIndiceProximoPYCOMA(new ArrayList<>(terminales.subList(inicio, terminales.size())));
        if (puntoComa < 0) throw new IllegalArgumentException("Falta punto y coma.");
        return inicio + puntoComa + 1;
    }

    private int buscarFinCuerpo(ArrayList<Token> terminales, int inicio) {
        if (terminales.get(inicio).getTipo().equals("LLAVEIZQ")) {
            return inicio + buscarIndiceLlaveDeCierre(new ArrayList<>(terminales.subList(inicio, terminales.size()))) + 1;
        }
        return buscarFinInstruccion(terminales, inicio);
    }

    private void agregarCondicional(NodoSintactico subraiz, ArrayList<Token> terminales, boolean dentroBucle) {
        NodoSintactico si = new NodoSintactico(dentroBucle ? "SIBUCLE" : "SI");
        int cierre = buscarIndiceParentesisDeCierre(terminales);
        si.agregarDerivado(new NodoSintactico(terminales.get(0)));
        si.agregarDerivado(new NodoSintactico(terminales.get(1)));
        NodoSintactico condicion = new NodoSintactico("CONDICION");
        agregarCondicion(condicion, new ArrayList<>(terminales.subList(2, cierre)));
        si.agregarDerivado(condicion);
        si.agregarDerivado(new NodoSintactico(terminales.get(cierre)));
        int fin = buscarFinCuerpo(terminales, cierre + 1);
        agregarCuerpo(si, new ArrayList<>(terminales.subList(cierre + 1, fin)), dentroBucle);
        if (fin < terminales.size()) {
            NodoSintactico sino = new NodoSintactico(dentroBucle ? "SINOBUCLE" : "SINO");
            if (dentroBucle) agregarSiNoBucle(sino, new ArrayList<>(terminales.subList(fin, terminales.size())));
            else agregarSiNo(sino, new ArrayList<>(terminales.subList(fin, terminales.size())));
            si.agregarDerivado(sino);
        }
        subraiz.agregarDerivado(si);
    }

    private void agregarCuerpo(NodoSintactico subraiz, ArrayList<Token> terminales, boolean dentroBucle) {
        if (terminales.get(0).getTipo().equals("LLAVEIZQ")) {
            subraiz.agregarDerivado(new NodoSintactico(terminales.get(0)));
            NodoSintactico secuencia = new NodoSintactico(dentroBucle ? "BLOQUEBUCLE" : "SECUENCIA");
            ArrayList<Token> contenido = new ArrayList<>(terminales.subList(1, terminales.size() - 1));
            if (dentroBucle) agregarBloqueBucle(secuencia, contenido);
            else agregarSecuencia(secuencia, contenido);
            subraiz.agregarDerivado(secuencia);
            subraiz.agregarDerivado(new NodoSintactico(terminales.get(terminales.size() - 1)));
        } else {
            agregarInstruccion(subraiz, terminales, dentroBucle);
        }
    }
}
