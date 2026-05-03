package org.compilador.TestSyntacticAnalyzer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.compilador.sintax.executor.ExecutorSyntactic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Tests para el Analizador Sintactico de tinyS.
 * Tecnica empleada: caja negra por clases de equivalencia.
 * Los archivos fuente (.s) se encuentran en la carpeta tc/.
 * Se cubren:
 *  Casos validos: programas sintacticamente correctos "CORRECTO: ANALISIS SINTACTICO"
 *  Casos de error: SyntacticException ante entradas invalidas "ERROR: SINTACTICO"
 * @author Martina Nahman
 */

public class SyntacticAnalyzerTest extends TestCase {

    public SyntacticAnalyzerTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(SyntacticAnalyzerTest.class);
    }

    // ---------------------------------------------------------------
    // Auxiliar: lee el archivo .s y ejecuta el analisis sintactico
    // ---------------------------------------------------------------
    private String analizar(String rutaRelativa) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(rutaRelativa));
            String source = new String(bytes);
            return ExecutorSyntactic.runSyntacticAnalysis(source);
        } catch (IOException e) {
            fail("No se pudo leer el archivo: " + rutaRelativa);
            return null;
        }
    }

    // ===============================================================
    // CASOS EXITOSOS
    // ===============================================================

    /**
     * SYN-OK-01: programa minimo valido, solo start vacio.
     * El parser debe aceptar el programa y retornar CORRECTO.
     * Archivo: tc/etapa2_sintactico/sintax_ok_01.s
     */
    public void testSintaxOk01_StartVacio() {
        String resultado = analizar("tc/etapa2_sintactico/sintax_ok_01.s");
        assertTrue(
                "Se esperaba CORRECTO pero fue: " + resultado,
                resultado.startsWith("CORRECTO: ANALISIS SINTACTICO")
        );
    }

    /**
     * SYN-OK-02: clase + implementacion con constructor.
     * Verifica: class, impl y constructor .()
     * Archivo: tc/etapa2_sintactico/sintax_ok_02.s
     */
    public void testSintaxOk02_ClaseImpl() {
        String resultado = analizar("tc/etapa2_sintactico/sintax_ok_02.s");
        assertTrue(
                "Se esperaba CORRECTO pero fue: " + resultado,
                resultado.startsWith("CORRECTO: ANALISIS SINTACTICO")
        );
    }

    /**
     * SYN-OK-03: atributos y asignacion con self.
     * Verifica: tipos, atributos y acceso self.x
     * Archivo: tc/etapa2_sintactico/sintax_ok_03.s
     */
    public void testSintaxOk03_AtributosAsignacion() {
        String resultado = analizar("tc/etapa2_sintactico/sintax_ok_03.s");
        assertTrue(
                "Se esperaba CORRECTO pero fue: " + resultado,
                resultado.startsWith("CORRECTO: ANALISIS SINTACTICO")
        );
    }

    /**
     * SYN-OK-04: if - else simple.
     * Verifica estructura condicional
     * Archivo: tc/etapa2_sintactico/sintax_ok_04.s
     */
    public void testSintaxOk04_IfElse() {
        String resultado = analizar("tc/etapa2_sintactico/sintax_ok_04.s");
        assertTrue(
                "Se esperaba CORRECTO pero fue: " + resultado,
                resultado.startsWith("CORRECTO: ANALISIS SINTACTICO")
        );
    }

    /**
     * SYN-OK-05: while con expresion.
     * Verifica bucle while
     * Archivo: tc/etapa2_sintactico/sintax_ok_05.s
     */
    public void testSintaxOk05_While() {
        String resultado = analizar("tc/etapa2_sintactico/sintax_ok_05.s");
        assertTrue(
                "Se esperaba CORRECTO pero fue: " + resultado,
                resultado.startsWith("CORRECTO: ANALISIS SINTACTICO")
        );
    }

    /**
     * SYN-OK-06: expresiones logicas complejas (&&, ||).
     * Archivo: tc/etapa2_sintactico/sintax_ok_06.s
     */
    public void testSintaxOk06_ExpresionesComplejas() {
        String resultado = analizar("tc/etapa2_sintactico/sintax_ok_06.s");
        assertTrue(
                "Se esperaba CORRECTO pero fue: " + resultado,
                resultado.startsWith("CORRECTO: ANALISIS SINTACTICO")
        );
    }

    /**
     * SYN-OK-07: for-in.
     * Verifica: for (Tipo id in id)
     * Archivo: tc/etapa2_sintactico/sintax_ok_07.s
     */
    public void testSintaxOk07_ForIn() {
        String resultado = analizar("tc/etapa2_sintactico/sintax_ok_07.s");
        assertTrue(
                "Se esperaba CORRECTO pero fue: " + resultado,
                resultado.startsWith("CORRECTO: ANALISIS SINTACTICO")
        );
    }

    /**
     * SYN-OK-08: encadenados de accesos.
     * Archivo: tc/etapa2_sintactico/sintax_ok_08.s
     */
    public void testSintaxOk08_Encadenados() {
        String resultado = analizar("tc/etapa2_sintactico/sintax_ok_08.s");
        assertTrue(
                "Se esperaba CORRECTO pero fue: " + resultado,
                resultado.startsWith("CORRECTO: ANALISIS SINTACTICO")
        );
    }

    /**
     * SYN-OK-09: uso de new y asignacion.
     * Archivo: tc/etapa2_sintactico/sintax_ok_09.s
     */
    public void testSintaxOk09_NewAsignacion() {
        String resultado = analizar("tc/etapa2_sintactico/sintax_ok_09.s");
        assertTrue(
                "Se esperaba CORRECTO pero fue: " + resultado,
                resultado.startsWith("CORRECTO: ANALISIS SINTACTICO")
        );
    }

    /**
     * SYN-OK-10: metodo con retorno y llamada.
     * Archivo: tc/etapa2_sintactico/sintax_ok_10.s
     */
    public void testSintaxOk10_MetodoRetorno() {
        String resultado = analizar("tc/etapa2_sintactico/sintax_ok_10.s");
        assertTrue(
                "Se esperaba CORRECTO pero fue: " + resultado,
                resultado.startsWith("CORRECTO: ANALISIS SINTACTICO")
        );
    }

    /**
     * SYN-OK-11: encadenado de metodos.
     * Archivo: tc/etapa2_sintactico/sintax_ok_11.s
     */
    public void testSintaxOk11_EncadenadoMetodos() {
        String resultado = analizar("tc/etapa2_sintactico/sintax_ok_11.s");
        assertTrue(
                "Se esperaba CORRECTO pero fue: " + resultado,
                resultado.startsWith("CORRECTO: ANALISIS SINTACTICO")
        );
    }

    /**
     * SYN-OK-12: uso de arrays.
     * Archivo: tc/etapa2_sintactico/sintax_ok_12.s
     */
    public void testSintaxOk12_Array() {
        String resultado = analizar("tc/etapa2_sintactico/sintax_ok_12.s");
        assertTrue(
                "Se esperaba CORRECTO pero fue: " + resultado,
                resultado.startsWith("CORRECTO: ANALISIS SINTACTICO")
        );
    }

    /**
     * SYN-OK-13: caso complejo combinando varias construcciones.
     * Archivo: tc/etapa2_sintactico/sintax_ok_13.s
     */
    public void testSintaxOk13_CasoComplejo() {
        String resultado = analizar("tc/etapa2_sintactico/sintax_ok_13.s");
        assertTrue(
                "Se esperaba CORRECTO pero fue: " + resultado,
                resultado.startsWith("CORRECTO: ANALISIS SINTACTICO")
        );
    }

    /**
     * SYN-OK-14: programa completo con objetos, arrays y control de flujo.
     * Archivo: tc/etapa2_sintactico/sintax_ok_14.s
     */
    public void testSintaxOk14_FullPrograma() {
        String resultado = analizar("tc/etapa2_sintactico/sintax_ok_14.s");
        assertTrue(
                "Se esperaba CORRECTO pero fue: " + resultado,
                resultado.startsWith("CORRECTO: ANALISIS SINTACTICO")
        );
    }

    // ===============================================================
    // CASOS DE ERROR
    // ===============================================================

    /**
     * SYN-ERR-01: falta start.
     * Archivo: tc/etapa2_sintactico/sintax_err_01.s
     */
    public void testSintaxErr01_FaltaStart() {
        String resultado = analizar("tc/etapa2_sintactico/sintax_err_01.s");
        assertTrue(
                "Se esperaba ERROR: SINTACTICO pero fue: " + resultado,
                resultado.startsWith("ERROR: SINTACTICO")
        );
    }

    /**
     * SYN-ERR-02: falta llave de cierre.
     * Archivo: tc/etapa2_sintactico/sintax_err_02.s
     */
    public void testSintaxErr02_FaltaLlave() {
        String resultado = analizar("tc/etapa2_sintactico/sintax_err_02.s");
        assertTrue(
                "Se esperaba ERROR: SINTACTICO pero fue: " + resultado,
                resultado.startsWith("ERROR: SINTACTICO")
        );
    }

    /**
     * SYN-ERR-03: falta parentesis.
     * Archivo: tc/etapa2_sintactico/sintax_err_03.s
     */
    public void testSintaxErr03_FaltaParentesis() {
        String resultado = analizar("tc/etapa2_sintactico/sintax_err_03.s");
        assertTrue(
                "Se esperaba ERROR: SINTACTICO pero fue: " + resultado,
                resultado.startsWith("ERROR: SINTACTICO")
        );
    }

    /**
     * SYN-ERR-04: asignacion invalida.
     * Archivo: tc/etapa2_sintactico/sintax_err_04.s
     */
    public void testSintaxErr04_AsignacionInvalida() {
        String resultado = analizar("tc/etapa2_sintactico/sintax_err_04.s");
        assertTrue(
                "Se esperaba ERROR: SINTACTICO pero fue: " + resultado,
                resultado.startsWith("ERROR: SINTACTICO")
        );
    }

    /**
     * SYN-ERR-05: expresion mal formada.
     * Archivo: tc/etapa2_sintactico/sintax_err_05.s
     */
    public void testSintaxErr05_ExpresionInvalida() {
        String resultado = analizar("tc/etapa2_sintactico/sintax_err_05.s");
        assertTrue(
                "Se esperaba ERROR: SINTACTICO pero fue: " + resultado,
                resultado.startsWith("ERROR: SINTACTICO")
        );
    }

    /**
     * SYN-ERR-06: if sin condicion.
     * Archivo: tc/etapa2_sintactico/sintax_err_06.s
     */
    public void testSintaxErr06_IfSinCondicion() {
        String resultado = analizar("tc/etapa2_sintactico/sintax_err_06.s");
        assertTrue(
                "Se esperaba ERROR: SINTACTICO pero fue: " + resultado,
                resultado.startsWith("ERROR: SINTACTICO")
        );
    }

    /**
     * SYN-ERR-07: while mal formado.
     * Archivo: tc/etapa2_sintactico/sintax_err_07.s
     */
    public void testSintaxErr07_WhileInvalido() {
        String resultado = analizar("tc/etapa2_sintactico/sintax_err_07.s");
        assertTrue(
                "Se esperaba ERROR: SINTACTICO pero fue: " + resultado,
                resultado.startsWith("ERROR: SINTACTICO")
        );
    }

    /**
     * SYN-ERR-08: uso incorrecto de new.
     * Archivo: tc/etapa2_sintactico/sintax_err_08.s
     */
    public void testSintaxErr08_NewInvalido() {
        String resultado = analizar("tc/etapa2_sintactico/sintax_err_08.s");
        assertTrue(
                "Se esperaba ERROR: SINTACTICO pero fue: " + resultado,
                resultado.startsWith("ERROR: SINTACTICO")
        );
    }

    /**
     * SYN-ERR-09: llamada sin parentesis.
     * Archivo: tc/etapa2_sintactico/sintax_err_09.s
     */
    public void testSintaxErr09_LlamadaSinParentesis() {
        String resultado = analizar("tc/etapa2_sintactico/sintax_err_09.s");
        assertTrue(
                "Se esperaba ERROR: SINTACTICO pero fue: " + resultado,
                resultado.startsWith("ERROR: SINTACTICO")
        );
    }

    /**
     * SYN-ERR-10: array mal formado.
     * Archivo: tc/etapa2_sintactico/sintax_err_10.s
     */
    public void testSintaxErr10_ArrayInvalido() {
        String resultado = analizar("tc/etapa2_sintactico/sintax_err_10.s");
        assertTrue(
                "Se esperaba ERROR: SINTACTICO pero fue: " + resultado,
                resultado.startsWith("ERROR: SINTACTICO")
        );
    }

    /**
     * SYN-ERR-11: falta punto y coma.
     * Archivo: tc/etapa2_sintactico/sintax_err_11.s
     */
    public void testSintaxErr11_FaltaPuntoYComa() {
        String resultado = analizar("tc/etapa2_sintactico/sintax_err_11.s");
        assertTrue(
                "Se esperaba ERROR: SINTACTICO pero fue: " + resultado,
                resultado.startsWith("ERROR: SINTACTICO")
        );
    }

    /**
     * SYN-ERR-12: encadenado invalido.
     * Archivo: tc/etapa2_sintactico/sintax_err_12.s
     */
    public void testSintaxErr12_EncadenadoInvalido() {
        String resultado = analizar("tc/etapa2_sintactico/sintax_err_12.s");
        assertTrue(
                "Se esperaba ERROR: SINTACTICO pero fue: " + resultado,
                resultado.startsWith("ERROR: SINTACTICO")
        );
    }
}