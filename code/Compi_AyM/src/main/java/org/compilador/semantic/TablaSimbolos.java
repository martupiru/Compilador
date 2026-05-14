package org.compilador.semantic;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Clase encargada de guardar todas las declaraciones en la tabla de simbolos
 * @author Aida Laricchia y Martina Nahman
 */

public class TablaSimbolos {
    /**
     * Hash de las clases
     */
    private Map<String, Clase> htClases = new LinkedHashMap<>();
    /**
     * clase actual
     */
    private Clase claseActual;

    /**
     * metodo actual
     */
    private  Metodo  metodoActual;

    /**
     * metodo start
     */
    private Start start;

    /**
     * indica si se esta actualizando el start
     */
    private boolean startActualizado;

}
