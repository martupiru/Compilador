package org.compilador.semantic;

import org.compilador.exception.SemanticException;
import org.compilador.lexer.token.Token;


import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Clase para almacenar los datos relacionados a una clase
 *
 * @author Laricchia Aida y Nahman Martina
 */
public class Clase {
    /**
     * nombre clase
     */
    private String nombre;
    /**
     * token clase
     */
    private Token claseToken;
    private String clasePadre;
    private Token clasePadreToken;
    private Map<String, Atributo> atributos = new LinkedHashMap<>();
    private Map<String, Metodo> metodos = new LinkedHashMap<>();
    private Constructor constructor;

    /**
     * Debemos saber si la clase esta definida mas de una vez
     */
    private boolean claseDefinida = false;
    /**
     * Debemos saber si el constructor de la clase esta definida
     * Una clase siempre debe tener definido uno y solo un metodo ·.
     */
    private boolean constructorDefinido = false;

    /**
     * Debemos saber si la impl de la clase esta definida
     * Al menos debe haber un impl para cada class dentro del codigo y uno de los impl para un idclass debe
     * tener al constructor.
     */
    private boolean implDefinido = false;

    /**
     * Constructor principal: cuando el sinatctico encuentra 'class Id'
     */
    public Clase(Token tokenClase) {
        this.nombre = tokenClase.getLexema();
        this.claseToken = tokenClase;
        this.clasePadre = "Object";
        this.clasePadreToken = null;
        this.claseDefinida = true;
    }

    /**
     * Constructor para clases del sistema (Object, Int, String)
     */
    public Clase(String nombre) {
        this.nombre = nombre;
        this.clasePadre = null;
        this.claseDefinida = true;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public Token getClaseToken() {
        return claseToken;
    }
    public void setClaseToken(Token claseToken) {
        this.claseToken = claseToken;
    }

    public Token getClasePadreToken() {
        return clasePadreToken;
    }

    public void setClasePadreToken(Token clasePadreToken) {
        this.clasePadre = clasePadreToken == null ? "Object" : clasePadreToken.getLexema();
        this.clasePadreToken = clasePadreToken;
    }

    public void setConstructor (Constructor constructor) throws SemanticException {
        if (this.constructor ==null) {
            this.constructor = constructor;
        }else  {
            throw new RuntimeException("No se puede establecer el contructor");
        }
    }


    /// /  HACER APPEND DE ATRIBUTOSSSS
    ///
    ///

    ///  HACER APPEND DE METODOS!!!!!!!!!!!
    ///
    ///
    public void agregarMetodo(Metodo metodo){
        if (!metodos.containsKey(metodo.getNombre())){
            metodos.put(metodo.getNombre(),metodo);
        }
    }

}
