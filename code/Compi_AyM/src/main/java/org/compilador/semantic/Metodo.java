package org.compilador.semantic;

import org.compilador.lexer.token.Token;

import java.util.LinkedHashMap;

public class Metodo {

    private String nombre;

    private Token token;

    private boolean esEstatico;

    private boolean esHeredado;

    private boolean esRedefinido = false;

    private Tipo returnTipo;

    private int posicion;

    /**
     * lista de parametros del metodo
     */
    private LinkedHashMap<String, Parametros> parametros = new LinkedHashMap<>();

    /**
     * lista de variables locale del metodo
     */
    private LinkedHashMap<String, Atributos> variables = new LinkedHashMap<>();

    /**
     * posicion del parametro
     */
    private int posicionParametro = 0;

    /**
     * Constructor para métodos definidos en el código
     */
    public Metodo(Token token, Tipo returnTipo, boolean esEstatico) {
        this.nombre = token.getLexema();
        this.token = token;
        this.returnTipo = returnTipo;
        this.esEstatico = esEstatico;
        this.esHeredado = false; // Es propio de la clase
        this.esRedefinido = false;
        this.posicionParametro = 0;
    }

    /**
     * Constructor de copia para la herencia
     * Útil cuando pasás un método del padre al hijo.
     */
    public Metodo(Metodo otro) {
        this.nombre = otro.nombre;
        this.token = otro.token;
        this.returnTipo = otro.returnTipo;
        this.esEstatico = otro.esEstatico;
        this.esHeredado = true;
        this.esRedefinido = otro.esRedefinido;
        this.posicion = otro.posicion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public boolean isEsEstatico() {
        return esEstatico;
    }

    public void setEsEstatico(boolean esEstatico) {
        this.esEstatico = esEstatico;
    }

    public boolean isEsHeredado() {
        return esHeredado;
    }

    public void setEsHeredado(boolean esHeredado) {
        this.esHeredado = esHeredado;
    }

    public boolean isEsRedefinido() {
        return esRedefinido;
    }

    public void setEsRedefinido(boolean esRedefinido) {
        this.esRedefinido = esRedefinido;
    }

    public Tipo getReturnTipo() {
        return returnTipo;
    }

    public void setReturnTipo(Tipo returnTipo) {
        this.returnTipo = returnTipo;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public LinkedHashMap<String, Parametros> getParametros() {
        return parametros;
    }

    public LinkedHashMap<String, Atributos> getVariables() {
        return variables;
    }

    /**
     * Retorna la posición actual para el parámetro e incrementa el contador.
     * Útil para el cálculo de offsets en el Stack Frame.
     */
    public int getProxPosParametro() {
        return posicionParametro++;
    }

    public int getPosicionParametro() {
        return posicionParametro;
    }
}
